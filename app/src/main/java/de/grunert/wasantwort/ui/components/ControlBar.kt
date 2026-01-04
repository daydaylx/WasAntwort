package de.grunert.wasantwort.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.grunert.wasantwort.domain.StylePreset

@Composable
fun ControlBar(
    selectedPreset: StylePreset?,
    onPresetSelected: (StylePreset) -> Unit,
    onCustomizeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Customize Button als erstes Element (Icon)
        GlassButton(
            onClick = onCustomizeClick,
            text = "Anpassen",
            height = 40.dp,
            leadingIcon = Icons.Filled.Tune,
            leadingIconContentDescription = "Anpassen",
            modifier = Modifier.padding(end = 4.dp)
        )

        // Presets als Chips
        StylePreset.values().forEach { preset ->
            GlassChip(
                text = preset.displayName,
                selected = preset == selectedPreset,
                onClick = { onPresetSelected(preset) }
            )
        }
    }
}
