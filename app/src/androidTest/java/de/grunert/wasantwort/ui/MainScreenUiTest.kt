package de.grunert.wasantwort.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import de.grunert.wasantwort.data.Repository
import de.grunert.wasantwort.ui.theme.WasAntwortTheme
import de.grunert.wasantwort.viewmodel.MainViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class MainScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val repository = mockk<Repository>(relaxed = true)

    @Test
    fun testMainScreenContentIsVisible() {
        // Given
        coEvery { repository.getHistory() } returns flowOf(emptyList())
        val viewModel = MainViewModel(repository)

        composeTestRule.setContent {
            WasAntwortTheme {
                MainScreen(viewModel = viewModel)
            }
        }

        // Then
        composeTestRule.onNodeWithText("WasAntwort").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nachricht").assertIsDisplayed()
        composeTestRule.onNodeWithText("Vorschläge generieren").assertIsDisplayed()
    }

    @Test
    fun testEmptyInputShowsErrorWhenGenerating() {
        // Given
        coEvery { repository.getHistory() } returns flowOf(emptyList())
        val viewModel = MainViewModel(repository)

        composeTestRule.setContent {
            WasAntwortTheme {
                MainScreen(viewModel = viewModel)
            }
        }

        // When
        composeTestRule.onNodeWithText("Vorschläge generieren").performClick()

        // Then - Snackbar shows error message
        composeTestRule.onNodeWithText("Bitte erst Nachricht eingeben").assertIsDisplayed()
    }
}
