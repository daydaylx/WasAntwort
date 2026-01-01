package de.grunert.wasantwort.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.grunert.wasantwort.domain.EmojiLevel
import de.grunert.wasantwort.domain.Formality
import de.grunert.wasantwort.domain.Goal
import de.grunert.wasantwort.domain.Length
import de.grunert.wasantwort.domain.Tone
import de.grunert.wasantwort.ui.components.FormalityToggle
import de.grunert.wasantwort.ui.components.GlassButton
import de.grunert.wasantwort.ui.components.GlassTopAppBar
import de.grunert.wasantwort.ui.components.InputCard
import de.grunert.wasantwort.ui.components.OptionChips
import de.grunert.wasantwort.ui.components.RewriteButtons
import de.grunert.wasantwort.ui.components.SuggestionCard
import de.grunert.wasantwort.viewmodel.MainUiState
import de.grunert.wasantwort.viewmodel.MainViewModel
import kotlinx.coroutines.launch

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val coroutineScope = rememberCoroutineScope()

    var showSettings by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    val history by viewModel.history.collectAsStateWithLifecycle()

    // Shake Animation State
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(uiState.uiState) {
        when (val state = uiState.uiState) {
            is MainUiState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.clearError()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            GlassTopAppBar(
                title = "WasAntwort",
                onSettingsClick = { showSettings = true },
                onHistoryClick = { showHistory = true }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            InputCard(
                text = uiState.inputText,
                onTextChange = viewModel::updateInput,
                onPasteClick = {
                    val clipboardText = clipboardManager.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
                    viewModel.updateInput(clipboardText)
                },
                onClearClick = viewModel::clearInput,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            OptionChips(
                title = "Ton",
                options = Tone.values(),
                selectedOption = uiState.tone,
                onOptionSelected = viewModel::updateTone,
                getDisplayName = { it.displayName },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )

            OptionChips(
                title = "Ziel",
                options = Goal.values(),
                selectedOption = uiState.goal,
                onOptionSelected = viewModel::updateGoal,
                getDisplayName = { it.displayName },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )

            OptionChips(
                title = "Länge",
                options = Length.values(),
                selectedOption = uiState.length,
                onOptionSelected = viewModel::updateLength,
                getDisplayName = { it.displayName },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )

            OptionChips(
                title = "Emojis",
                options = EmojiLevel.values(),
                selectedOption = uiState.emojiLevel,
                onOptionSelected = viewModel::updateEmojiLevel,
                getDisplayName = { it.displayName },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )

            FormalityToggle(
                title = "Anrede",
                isDu = uiState.formality == Formality.DU,
                onToggle = { isDu ->
                    viewModel.updateFormality(
                        if (isDu) Formality.DU
                        else Formality.SIE
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )

            GlassButton(
                onClick = {
                    if (uiState.inputText.isBlank()) {
                        coroutineScope.launch {
                            // Simple Shake Animation
                            for (i in 0..2) {
                                shakeOffset.animateTo(10f, animationSpec = tween(50))
                                shakeOffset.animateTo(-10f, animationSpec = tween(50))
                            }
                            shakeOffset.animateTo(0f, animationSpec = tween(50))
                            snackbarHostState.showSnackbar("Bitte erst Nachricht eingeben")
                        }
                    } else {
                        viewModel.generateSuggestions()
                    }
                },
                // Keep enabled even if empty to allow shake feedback, disable only when loading
                enabled = uiState.uiState !is MainUiState.Loading,
                isLoading = uiState.uiState is MainUiState.Loading,
                text = "Vorschläge generieren",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 16.dp)
                    .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
            )

            when (uiState.uiState) {
                is MainUiState.Loading -> {
                    // Skeleton Loading State
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        repeat(3) {
                            SkeletonSuggestionCard()
                        }
                    }
                }
                is MainUiState.Success -> {
                    uiState.suggestions.forEachIndexed { index, suggestion ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = if (index == 0) 0.dp else 12.dp)
                        ) {
                            SuggestionCard(
                                text = suggestion,
                                onCopyClick = {
                                    val clip = ClipData.newPlainText("Antwort", suggestion)
                                    clipboardManager.setPrimaryClip(clip)
                                    coroutineScope.launch {
                                        // Snackbar removed here as SuggestionCard now handles immediate visual feedback
                                        // But we can keep it if desired, or rely on the icon checkmark
                                        // snackbarHostState.showSnackbar("Kopiert") 
                                    }
                                },
                                onShareClick = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, suggestion)
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, null)
                                    context.startActivity(shareIntent)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            RewriteButtons(
                                onRewriteClick = { rewriteType ->
                                    viewModel.rewriteSuggestion(index, rewriteType)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                else -> {}
            }
        }
    }

    if (showSettings) {
        ModalBottomSheet(
            onDismissRequest = { showSettings = false }
        ) {
            SettingsScreen(
                currentSettings = uiState.settings,
                onSave = viewModel::saveSettings,
                onDismiss = { showSettings = false }
            )
        }
    }

    if (showHistory) {
        ModalBottomSheet(
            onDismissRequest = { showHistory = false }
        ) {
            HistoryScreen(
                history = history,
                onEntryClick = viewModel::loadFromHistory,
                onDeleteEntry = viewModel::deleteHistoryEntry,
                onClearHistory = viewModel::clearHistory,
                onDismiss = { showHistory = false }
            )
        }
    }
}

@Composable
fun SkeletonSuggestionCard() {
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(de.grunert.wasantwort.ui.theme.GlassSurfaceBase.copy(alpha = alpha))
    )
}
