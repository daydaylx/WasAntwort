package de.grunert.wasantwort.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.grunert.wasantwort.domain.StylePreset
import de.grunert.wasantwort.ui.components.ConfettiEffect
import de.grunert.wasantwort.ui.components.ControlBar
import de.grunert.wasantwort.ui.components.CosmicBackground
import de.grunert.wasantwort.ui.components.EmptyStateCard
import de.grunert.wasantwort.ui.components.GlassButton
import de.grunert.wasantwort.ui.components.GlassCard
import de.grunert.wasantwort.ui.components.GlassTopAppBar
import de.grunert.wasantwort.ui.components.InputCard
import de.grunert.wasantwort.ui.components.LoadingWaveform
import de.grunert.wasantwort.ui.components.RewriteButtons
import de.grunert.wasantwort.ui.components.StyleCustomizationBottomSheet
import de.grunert.wasantwort.ui.components.SuggestionCard
import de.grunert.wasantwort.viewmodel.ErrorSource
import de.grunert.wasantwort.viewmodel.MainScreenState
import de.grunert.wasantwort.viewmodel.MainUiState
import de.grunert.wasantwort.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Findet das passende Preset basierend auf den aktuellen Einstellungen
 */
private fun MainScreenState.findMatchingPreset(): StylePreset? {
    return StylePreset.values().find { preset ->
        preset.tone == tone &&
        preset.goal == goal &&
        preset.length == length &&
        preset.emojiLevel == emojiLevel &&
        preset.formality == formality
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
    var showStyleCustomization by remember { mutableStateOf(false) }
    val history by viewModel.history.collectAsStateWithLifecycle()
    val deletedEntryWithUndo by viewModel.deletedEntryWithUndo.collectAsStateWithLifecycle()

    // Confetti State
    var showConfetti by remember { mutableStateOf(false) }

    // Shake Animation State
    val shakeOffset = remember { Animatable(0f) }
    
    // Aktuelles Preset ermitteln
    val currentPreset = uiState.findMatchingPreset()
    
    // Clipboard Status prüfen
    fun isClipboardEmpty(): Boolean {
        val clip = clipboardManager.primaryClip
        return clip == null || clip.itemCount == 0 || clip.getItemAt(0)?.text?.toString().isNullOrBlank()
    }

    LaunchedEffect(showConfetti) {
        if (showConfetti) {
            delay(1500)
            showConfetti = false
        }
    }

    LaunchedEffect(uiState.uiState) {
        val state = uiState.uiState
        if (state is MainUiState.Error) {
            snackbarHostState.showSnackbar(state.message)
            if (state.source != ErrorSource.GENERATE) {
                viewModel.clearError()
            }
        }
    }

    // Show undo snackbar when entry is deleted
    LaunchedEffect(deletedEntryWithUndo) {
        if (deletedEntryWithUndo != null) {
            val result = snackbarHostState.showSnackbar(
                message = "Eintrag gelöscht",
                actionLabel = "Rückgängig",
                duration = androidx.compose.material3.SnackbarDuration.Short
            )
            if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                viewModel.restoreHistoryEntry()
            }
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
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Cosmic Background
            CosmicBackground()
            
            // Confetti Layer (over background, under UI)
            ConfettiEffect(trigger = showConfetti)
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                // --- Upper Area: Results (Flexible Height) ---
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    // Loading State with Waveform
                    if (uiState.uiState is MainUiState.Loading) {
                        LoadingWaveform(modifier = Modifier.align(Alignment.Center))
                    }
                    
                    // Empty State
                    else if (uiState.suggestions.isEmpty() && uiState.uiState !is MainUiState.Error) {
                        EmptyStateCard(
                            onExampleClick = {
                                viewModel.updateInput("Hey, kannst du mir bitte helfen?")
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    // Error State
                    else if (uiState.uiState is MainUiState.Error && (uiState.uiState as MainUiState.Error).source == ErrorSource.GENERATE) {
                        val errorState = uiState.uiState as MainUiState.Error
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Fehler",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = de.grunert.wasantwort.ui.theme.Danger,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = errorState.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = de.grunert.wasantwort.ui.theme.TextSecondary,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                GlassButton(
                                    onClick = { viewModel.clearError() },
                                    text = "Schließen",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    // Suggestions Carousel (Pager)
                    else if (uiState.suggestions.isNotEmpty()) {
                        val pagerState = rememberPagerState(pageCount = { uiState.suggestions.size })
                        
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center
                        ) {
                            HorizontalPager(
                                state = pagerState,
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 0.dp),
                                pageSpacing = 16.dp
                            ) { page ->
                                val suggestion = uiState.suggestions[page]
                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    SuggestionCard(
                                        text = suggestion,
                                        index = page,
                                        totalCount = uiState.suggestions.size,
                                        onCopyClick = {
                                            val clip = ClipData.newPlainText("Antwort", suggestion)
                                            clipboardManager.setPrimaryClip(clip)
                                            showConfetti = true
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Kopiert")
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
                                            viewModel.rewriteSuggestion(page, rewriteType)
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                            
                            // Pager Indicator (Dots)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                repeat(uiState.suggestions.size) { iteration ->
                                    val color = if (pagerState.currentPage == iteration) 
                                        de.grunert.wasantwort.ui.theme.Accent1 
                                    else 
                                        de.grunert.wasantwort.ui.theme.TextSecondary.copy(alpha = 0.3f)
                                        Box(
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .padding(bottom = 8.dp)
                                                .clip(androidx.compose.foundation.shape.CircleShape)
                                                .background(color)
                                                .size(8.dp)
                                        )
                                }
                            }
                        }
                    }
                }

                // --- Lower Area: Input & Controls (Fixed) ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Floating Tool Belt
                    ControlBar(
                        selectedPreset = currentPreset,
                        onPresetSelected = { viewModel.applyPreset(it) },
                        onCustomizeClick = { showStyleCustomization = true },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Input Card
                    InputCard(
                        text = uiState.inputText,
                        onTextChange = viewModel::updateInput,
                        onPasteClick = {
                            if (isClipboardEmpty()) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Zwischenablage leer")
                                }
                            } else {
                                val clipboardText = clipboardManager.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
                                viewModel.updateInput(clipboardText)
                            }
                        },
                        onClearClick = viewModel::clearInput,
                        onGenerateClick = { viewModel.generateSuggestions() },
                        isPasteEnabled = true,
                        // Styles removed from InputCard as they are now in ControlBar
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Generate Button
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
                        enabled = uiState.inputText.isNotBlank() && uiState.uiState !is MainUiState.Loading,
                        isLoading = uiState.uiState is MainUiState.Loading,
                        text = "Vorschläge generieren",
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
                    )
                }
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
    
    if (showStyleCustomization) {
        StyleCustomizationBottomSheet(
            currentTone = uiState.tone,
            currentGoal = uiState.goal,
            currentLength = uiState.length,
            currentEmojiLevel = uiState.emojiLevel,
            currentFormality = uiState.formality,
            onToneSelected = viewModel::updateTone,
            onGoalSelected = viewModel::updateGoal,
            onLengthSelected = viewModel::updateLength,
            onEmojiLevelSelected = viewModel::updateEmojiLevel,
            onFormalityToggled = viewModel::updateFormality,
            onApply = { /* Änderungen sind bereits im State */ },
            onDismiss = { showStyleCustomization = false }
        )
    }
}
