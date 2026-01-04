package de.grunert.wasantwort.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
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
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RewriteType.values().forEach { rewriteType ->
            GlassChip(
                text = rewriteType.displayName,
                selected = false,
                onClick = { onRewriteClick(rewriteType) },
                modifier = Modifier.padding(horizontal = 0.dp)
            )
        }
    }
}