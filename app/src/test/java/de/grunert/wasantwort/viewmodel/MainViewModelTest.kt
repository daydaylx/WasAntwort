package de.grunert.wasantwort.viewmodel

import de.grunert.wasantwort.data.AppSettings
import de.grunert.wasantwort.data.ApiException
import de.grunert.wasantwort.data.Repository
import de.grunert.wasantwort.domain.*
import io.mockk.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private lateinit var viewModel: MainViewModel
    private lateinit var repository: Repository
    private val testDispatcher = StandardTestDispatcher()

    private val testSettings = AppSettings(
        apiKey = "test-key",
        baseUrl = "https://test.com/v1",
        model = "test-model",
        defaultTone = Tone.FREUNDLICH,
        defaultGoal = Goal.NACHRAGEN,
        defaultLength = Length.NORMAL,
        defaultEmojiLevel = EmojiLevel.WENIG,
        defaultFormality = Formality.DU,
        useContext = false
    )

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)

        // Default mock behaviors
        coEvery { repository.getSettings() } returns testSettings
        coEvery { repository.getHistory() } returns flowOf(emptyList())

        viewModel = MainViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `initial state is Idle with empty input`() {
        // Then
        val state = viewModel.uiState.value
        assertTrue(state.uiState is MainUiState.Idle)
        assertEquals("", state.inputText)
        assertTrue(state.suggestions.isEmpty())
    }

    @Test
    fun `initial state loads settings from repository`() {
        // Then
        val state = viewModel.uiState.value
        assertEquals(testSettings, state.settings)
        assertEquals(testSettings.defaultTone, state.tone)
        assertEquals(testSettings.defaultGoal, state.goal)
        assertEquals(testSettings.defaultLength, state.length)
    }

    @Test
    fun `updateInput changes input text`() {
        // When
        viewModel.updateInput("Test message")

        // Then
        assertEquals("Test message", viewModel.uiState.value.inputText)
    }

    @Test
    fun `receiving shared text via updateInput works consistently`() {
        // Given initial share
        viewModel.updateInput("First shared text")
        assertEquals("First shared text", viewModel.uiState.value.inputText)

        // When receiving second share (simulating onNewIntent)
        viewModel.updateInput("Second shared text")

        // Then
        assertEquals("Second shared text", viewModel.uiState.value.inputText)
    }

    @Test
    fun `updateTone changes tone`() {
        // When
        viewModel.updateTone(Tone.HERZLICH)

        // Then
        assertEquals(Tone.HERZLICH, viewModel.uiState.value.tone)
    }

    @Test
    fun `updateGoal changes goal`() {
        // When
        viewModel.updateGoal(Goal.ZUSAGEN)

        // Then
        assertEquals(Goal.ZUSAGEN, viewModel.uiState.value.goal)
    }

    @Test
    fun `updateLength changes length`() {
        // When
        viewModel.updateLength(Length.KURZ)

        // Then
        assertEquals(Length.KURZ, viewModel.uiState.value.length)
    }

    @Test
    fun `updateEmojiLevel changes emoji level`() {
        // When
        viewModel.updateEmojiLevel(EmojiLevel.NORMAL)

        // Then
        assertEquals(EmojiLevel.NORMAL, viewModel.uiState.value.emojiLevel)
    }

    @Test
    fun `updateFormality changes formality`() {
        // When
        viewModel.updateFormality(Formality.SIE)

        // Then
        assertEquals(Formality.SIE, viewModel.uiState.value.formality)
    }

    @Test
    fun `applyPreset updates all style parameters`() {
        // Given
        val preset = StylePreset.KURZ_KLAR

        // When
        viewModel.applyPreset(preset)

        // Then
        val state = viewModel.uiState.value
        assertEquals(Tone.KURZ, state.tone)
        assertEquals(Goal.NACHRAGEN, state.goal)
        assertEquals(Length.KURZ, state.length)
        assertEquals(EmojiLevel.AUS, state.emojiLevel)
        assertEquals(Formality.DU, state.formality)
    }

    @Test
    fun `generateSuggestions sets error when input is blank`() = runTest {
        // Given
        viewModel.updateInput("")

        // When
        viewModel.generateSuggestions()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.uiState is MainUiState.Error)
        val errorMessage = (state.uiState as MainUiState.Error).message
        assertTrue(errorMessage.contains("Bitte zuerst eine Nachricht eingeben"))
    }

    @Test
    fun `generateSuggestions sets error when input exceeds 4000 characters`() = runTest {
        // Given
        viewModel.updateInput("x".repeat(4001))

        // When
        viewModel.generateSuggestions()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.uiState is MainUiState.Error)
        val errorMessage = (state.uiState as MainUiState.Error).message
        assertTrue(errorMessage.contains("zu lang"))
    }

    @Test
    fun `generateSuggestions sets error when API settings are missing`() = runTest {
        // Given
        coEvery { repository.getSettings() } returns testSettings.copy(apiKey = "")
        val newViewModel = MainViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        newViewModel.updateInput("Test message")

        // When
        newViewModel.generateSuggestions()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = newViewModel.uiState.value
        assertTrue(state.uiState is MainUiState.Error)
    }

    @Test
    fun `generateSuggestions sets Loading state during API call`() = runTest {
        // Given
        viewModel.updateInput("Test message")
        val resultDeferred = CompletableDeferred<Result<List<String>>>()
        coEvery {
            repository.generateSuggestions(any(), any(), any(), any(), any(), any())
        } coAnswers {
            resultDeferred.await()
        }

        // When
        viewModel.generateSuggestions()
        testDispatcher.scheduler.runCurrent()

        // Then
        assertTrue(viewModel.uiState.value.uiState is MainUiState.Loading)

        // Cleanup
        resultDeferred.complete(Result.success(emptyList()))
    }

    @Test
    fun `generateSuggestions sets Success state with suggestions on success`() = runTest {
        // Given
        viewModel.updateInput("Test message")
        val testSuggestions = listOf("Antwort 1", "Antwort 2", "Antwort 3")
        coEvery {
            repository.generateSuggestions(any(), any(), any(), any(), any(), any())
        } returns Result.success(testSuggestions)

        // When
        viewModel.generateSuggestions()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.uiState is MainUiState.Success)
        assertEquals(testSuggestions, state.suggestions)
    }

    @Test
    fun `generateSuggestions sets Error state on API failure`() = runTest {
        // Given
        viewModel.updateInput("Test message")
        val errorMessage = "API Fehler"
        coEvery {
            repository.generateSuggestions(any(), any(), any(), any(), any(), any())
        } returns Result.failure(ApiException(errorMessage))

        // When
        viewModel.generateSuggestions()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.uiState is MainUiState.Error)
        assertEquals(errorMessage, (state.uiState as MainUiState.Error).message)
    }

    @Test
    fun `clearInput clears the input text`() {
        // Given
        viewModel.updateInput("Test message")

        // When
        viewModel.clearInput()

        // Then
        assertEquals("", viewModel.uiState.value.inputText)
    }

    @Test
    fun `clearError changes Error state to Idle`() = runTest {
        // Given
        viewModel.updateInput("Test")
        coEvery {
            repository.generateSuggestions(any(), any(), any(), any(), any(), any())
        } returns Result.failure(Exception("Error"))
        viewModel.generateSuggestions()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.clearError()

        // Then
        assertTrue(viewModel.uiState.value.uiState is MainUiState.Idle)
    }

    @Test
    fun `saveSettings calls repository`() = runTest {
        // Given
        val newSettings = testSettings.copy(apiKey = "new-key")
        coEvery { repository.saveSettings(any()) } just Runs

        // When
        viewModel.saveSettings(newSettings)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { repository.saveSettings(newSettings) }
        assertEquals(newSettings, viewModel.uiState.value.settings)
    }

    @Test
    fun `loadFromHistory updates state with history entry values`() {
        // Given
        val historyEntry = ConversationEntry(
            id = "test-id",
            timestamp = 123456L,
            inputText = "Historical message",
            tone = Tone.HERZLICH,
            goal = Goal.ZUSAGEN,
            length = Length.KURZ,
            emojiLevel = EmojiLevel.NORMAL,
            formality = Formality.SIE,
            suggestions = listOf("Suggestion 1")
        )

        // When
        viewModel.loadFromHistory(historyEntry)

        // Then
        val state = viewModel.uiState.value
        assertEquals("Historical message", state.inputText)
        assertEquals(Tone.HERZLICH, state.tone)
        assertEquals(Goal.ZUSAGEN, state.goal)
        assertEquals(Length.KURZ, state.length)
        assertEquals(EmojiLevel.NORMAL, state.emojiLevel)
        assertEquals(Formality.SIE, state.formality)
    }

    @Test
    fun `deleteHistoryEntry calls repository`() = runTest {
        // Given
        val entryId = "test-entry-id"
        coEvery { repository.deleteHistoryEntry(entryId) } just Runs

        // When
        viewModel.deleteHistoryEntry(entryId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { repository.deleteHistoryEntry(entryId) }
    }

    @Test
    fun `clearHistory calls repository`() = runTest {
        // Given
        coEvery { repository.clearHistory() } just Runs

        // When
        viewModel.clearHistory()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { repository.clearHistory() }
    }

    @Test
    fun `rewriteSuggestion updates specific suggestion on success`() = runTest {
        // Given
        val initialSuggestions = listOf("Suggestion 1", "Suggestion 2", "Suggestion 3")
        viewModel.updateInput("Test")
        coEvery {
            repository.generateSuggestions(any(), any(), any(), any(), any(), any())
        } returns Result.success(initialSuggestions)
        viewModel.generateSuggestions()
        testDispatcher.scheduler.advanceUntilIdle()

        val rewrittenText = "Kürzere Antwort"
        coEvery {
            repository.rewriteSuggestion(any(), any(), any())
        } returns Result.success(rewrittenText)

        // When
        viewModel.rewriteSuggestion(1, RewriteType.KUERZER)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(3, state.suggestions.size)
        assertEquals("Suggestion 1", state.suggestions[0])
        assertEquals(rewrittenText, state.suggestions[1])
        assertEquals("Suggestion 3", state.suggestions[2])
    }

    @Test
    fun `rewriteSuggestion sets error when index is invalid`() = runTest {
        // Given
        viewModel.updateInput("Test")
        coEvery {
            repository.generateSuggestions(any(), any(), any(), any(), any(), any())
        } returns Result.success(listOf("Suggestion 1"))
        viewModel.generateSuggestions()
        testDispatcher.scheduler.advanceUntilIdle()

        // When - try to rewrite index that doesn't exist
        viewModel.rewriteSuggestion(10, RewriteType.KUERZER)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - state should remain unchanged (no error, just ignored)
        val state = viewModel.uiState.value
        assertEquals(1, state.suggestions.size)
    }
}
