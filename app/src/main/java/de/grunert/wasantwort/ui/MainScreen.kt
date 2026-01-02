package de.grunert.wasantwort.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
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
import de.grunert.wasantwort.domain.StylePreset
import de.grunert.wasantwort.ui.components.FormalityToggle
import de.grunert.wasantwort.ui.components.GlassButton
import de.grunert.wasantwort.ui.components.GlassCard
import de.grunert.wasantwort.ui.components.GlassTopAppBar
import de.grunert.wasantwort.ui.components.InputCard
import de.grunert.wasantwort.ui.components.OptionChips
import de.grunert.wasantwort.ui.components.RewriteButtons
import de.grunert.wasantwort.ui.components.StyleCustomizationBottomSheet
import de.grunert.wasantwort.ui.components.CosmicBackground
import de.grunert.wasantwort.ui.components.StylePresetsRow
import de.grunert.wasantwort.ui.components.SuggestionCard
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import de.grunert.wasantwort.viewmodel.MainUiState
import de.grunert.wasantwort.viewmodel.ErrorSource
import de.grunert.wasantwort.viewmodel.MainViewModel
import de.grunert.wasantwort.viewmodel.MainScreenState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.IntOffset
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
    var showStyleCustomization by remember { mutableStateOf(false) }
    val history by viewModel.history.collectAsStateWithLifecycle()

    // Shake Animation State
    val shakeOffset = remember { Animatable(0f) }
    
    // Aktuelles Preset ermitteln
    val currentPreset = uiState.findMatchingPreset()
    
    // Clipboard Status prüfen
    fun isClipboardEmpty(): Boolean {
        val clip = clipboardManager.primaryClip
        return clip == null || clip.itemCount == 0 || clip.getItemAt(0)?.text?.toString().isNullOrBlank()
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
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Cosmic Background
            CosmicBackground()
            
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
                    isPasteEnabled = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )

                // Presets Row
                StylePresetsRow(
                    selectedPreset = currentPreset,
                    onPresetSelected = { preset ->
                        viewModel.applyPreset(preset)
                    },
                    onCustomizeClick = { showStyleCustomization = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                )

                // Error State: API-Key fehlt
                val settings = uiState.settings
                if (settings?.apiKey.isNullOrBlank() || settings?.baseUrl.isNullOrBlank()) {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "API-Key fehlt",
                                style = MaterialTheme.typography.titleMedium,
                                color = de.grunert.wasantwort.ui.theme.Danger,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "Bitte konfiguriere zuerst die API-Einstellungen.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = de.grunert.wasantwort.ui.theme.TextSecondary,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            GlassButton(
                                onClick = { showSettings = true },
                                text = "Einstellungen",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                val errorState = uiState.uiState as? MainUiState.Error
                if (errorState?.source == ErrorSource.GENERATE) {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                GlassButton(
                                    onClick = { viewModel.generateSuggestions() },
                                    text = "Nochmal versuchen",
                                    modifier = Modifier.weight(1f)
                                )
                                GlassButton(
                                    onClick = viewModel::clearError,
                                    text = "Schließen",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Generate Button - wird sticky am Ende positioniert
                Spacer(modifier = Modifier.height(24.dp))
                
                if (uiState.uiState is MainUiState.Loading && uiState.suggestions.isEmpty()) {
                    // Skeleton Loading State
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        repeat(3) {
                            SkeletonSuggestionCard()
                        }
                    }
                }

                if (uiState.suggestions.isNotEmpty()) {
                    uiState.suggestions.forEachIndexed { index, suggestion ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = if (index == 0) 0.dp else 16.dp)
                        ) {
                            SuggestionCard(
                                text = suggestion,
                                onCopyClick = {
                                    val clip = ClipData.newPlainText("Antwort", suggestion)
                                    clipboardManager.setPrimaryClip(clip)
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
                                    viewModel.rewriteSuggestion(index, rewriteType)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                
                // Spacer für sticky button
                Spacer(modifier = Modifier.height(80.dp))
            }
            
            // Sticky Generate Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
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
