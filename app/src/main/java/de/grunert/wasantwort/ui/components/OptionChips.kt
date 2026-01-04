package de.grunert.wasantwort.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import de.grunert.wasantwort.ui.theme.GlassLightRim
import de.grunert.wasantwort.ui.theme.GlassSurfaceBase
import de.grunert.wasantwort.ui.theme.TextPrimary
import de.grunert.wasantwort.ui.theme.TextSecondary

@Composable
fun <T : Enum<T>> OptionChips(
    title: String,
    options: Array<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    getDisplayName: (T) -> String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        val headerShape = RoundedCornerShape(12.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(headerShape)
                .background(GlassSurfaceBase.copy(alpha = 0.45f))
                .border(
                    width = 1.dp,
                    color = GlassLightRim.copy(alpha = 0.2f),
                    shape = headerShape
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                GlassChip(
                    text = getDisplayName(option),
                    selected = selectedOption == option,
                    onClick = { onOptionSelected(option) }
                )
            }
        }
    }
}

@Composable
fun FormalityToggle(
    title: String,
    isDu: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 8.dp),
            color = TextSecondary
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GlassChip(
                text = "Du",
                selected = isDu,
                onClick = { onToggle(true) }
            )
            GlassChip(
                text = "Sie",
                selected = !isDu,
                onClick = { onToggle(false) }
            )
        }
    }
}
