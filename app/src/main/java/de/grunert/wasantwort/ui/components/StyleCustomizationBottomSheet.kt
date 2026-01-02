package de.grunert.wasantwort.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.grunert.wasantwort.domain.EmojiLevel
import de.grunert.wasantwort.domain.Formality
import de.grunert.wasantwort.domain.Goal
import de.grunert.wasantwort.domain.Length
import de.grunert.wasantwort.domain.Tone
import de.grunert.wasantwort.ui.theme.TextPrimary
import de.grunert.wasantwort.ui.components.GlassButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyleCustomizationBottomSheet(
    currentTone: Tone,
    currentGoal: Goal,
    currentLength: Length,
    currentEmojiLevel: EmojiLevel,
    currentFormality: Formality,
    onToneSelected: (Tone) -> Unit,
    onGoalSelected: (Goal) -> Unit,
    onLengthSelected: (Length) -> Unit,
    onEmojiLevelSelected: (EmojiLevel) -> Unit,
    onFormalityToggled: (Formality) -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Stil anpassen",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OptionChips(
                title = "Ton",
                options = Tone.values(),
                selectedOption = currentTone,
                onOptionSelected = onToneSelected,
                getDisplayName = { it.displayName },
                modifier = Modifier.fillMaxWidth()
            )

            OptionChips(
                title = "Ziel",
                options = Goal.values(),
                selectedOption = currentGoal,
                onOptionSelected = onGoalSelected,
                getDisplayName = { it.displayName },
                modifier = Modifier.fillMaxWidth()
            )

            OptionChips(
                title = "Länge",
                options = Length.values(),
                selectedOption = currentLength,
                onOptionSelected = onLengthSelected,
                getDisplayName = { it.displayName },
                modifier = Modifier.fillMaxWidth()
            )

            OptionChips(
                title = "Emojis",
                options = EmojiLevel.values(),
                selectedOption = currentEmojiLevel,
                onOptionSelected = onEmojiLevelSelected,
                getDisplayName = { it.displayName },
                modifier = Modifier.fillMaxWidth()
            )

            FormalityToggle(
                title = "Anrede",
                isDu = currentFormality == Formality.DU,
                onToggle = { isDu ->
                    onFormalityToggled(if (isDu) Formality.DU else Formality.SIE)
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            GlassButton(
                onClick = {
                    onApply()
                    onDismiss()
                },
                text = "Übernehmen",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }
    }
}
