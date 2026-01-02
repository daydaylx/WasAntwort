package de.grunert.wasantwort.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.grunert.wasantwort.domain.StylePreset
import de.grunert.wasantwort.ui.theme.TextSecondary

@Composable
fun StylePresetsRow(
    selectedPreset: StylePreset?,
    onPresetSelected: (StylePreset) -> Unit,
    onCustomizeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StylePreset.values().forEach { preset ->
            GlassChip(
                text = preset.displayName,
                selected = selectedPreset == preset,
                onClick = { onPresetSelected(preset) },
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        
        // "Anpassen" Button
        GlassChip(
            text = "Anpassen",
            selected = false,
            onClick = onCustomizeClick,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}
