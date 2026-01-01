package de.grunert.wasantwort.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import de.grunert.wasantwort.ui.components.FormalityToggle
import de.grunert.wasantwort.ui.components.InputCard
import de.grunert.wasantwort.ui.components.OptionChips
import de.grunert.wasantwort.ui.components.RewriteButtons
import de.grunert.wasantwort.ui.components.SuggestionCard
import de.grunert.wasantwort.viewmodel.MainUiState
import de.grunert.wasantwort.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState.value
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val coroutineScope = rememberCoroutineScope()

    var showSettings by remember { mutableStateOf(false) }

    // Handle errors and show snackbar
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
            TopAppBar(
                title = { Text("ReplyHelper") },
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Einstellungen"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
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
                modifier = Modifier.fillMaxWidth()
            )

            OptionChips(
                title = "Ton",
                options = de.grunert.replyhelper.domain.Tone.values(),
                selectedOption = uiState.tone,
                onOptionSelected = viewModel::updateTone,
                getDisplayName = { it.displayName },
                modifier = Modifier.fillMaxWidth()
            )

            OptionChips(
                title = "Ziel",
                options = de.grunert.replyhelper.domain.Goal.values(),
                selectedOption = uiState.goal,
                onOptionSelected = viewModel::updateGoal,
                getDisplayName = { it.displayName },
                modifier = Modifier.fillMaxWidth()
            )

            OptionChips(
                title = "Länge",
                options = de.grunert.replyhelper.domain.Length.values(),
                selectedOption = uiState.length,
                onOptionSelected = viewModel::updateLength,
                getDisplayName = { it.displayName },
                modifier = Modifier.fillMaxWidth()
            )

            OptionChips(
                title = "Emojis",
                options = de.grunert.replyhelper.domain.EmojiLevel.values(),
                selectedOption = uiState.emojiLevel,
                onOptionSelected = viewModel::updateEmojiLevel,
                getDisplayName = { it.displayName },
                modifier = Modifier.fillMaxWidth()
            )

            FormalityToggle(
                title = "Anrede",
                isDu = uiState.formality == de.grunert.replyhelper.domain.Formality.DU,
                onToggle = { isDu ->
                    viewModel.updateFormality(
                        if (isDu) de.grunert.replyhelper.domain.Formality.DU
                        else de.grunert.replyhelper.domain.Formality.SIE
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = viewModel::generateSuggestions,
                enabled = uiState.uiState !is MainUiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                if (uiState.uiState is MainUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(end = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Text("Vorschläge generieren")
            }

            when (uiState.uiState) {
                is MainUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(uiState.suggestions) { index, suggestion ->
                            Column {
                                SuggestionCard(
                                    text = suggestion,
                                    onClick = {
                                        val clip = ClipData.newPlainText("Antwort", suggestion)
                                        clipboardManager.setPrimaryClip(clip)
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Kopiert")
                                        }
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
                }
                else -> {}
            }
        }
    }

    if (showSettings) {
        androidx.compose.material3.ModalBottomSheet(
            onDismissRequest = { showSettings = false }
        ) {
            SettingsScreen(
                currentSettings = uiState.settings,
                onSave = viewModel::saveSettings,
                onDismiss = { showSettings = false }
            )
        }
    }
}

