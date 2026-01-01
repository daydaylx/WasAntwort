package de.grunert.wasantwort.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.grunert.wasantwort.domain.RewriteType

@Composable
fun RewriteButtons(
    onRewriteClick: (RewriteType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        RewriteType.values().forEach { rewriteType ->
            GlassChip(
                text = rewriteType.displayName,
                selected = false,
                onClick = { onRewriteClick(rewriteType) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}



