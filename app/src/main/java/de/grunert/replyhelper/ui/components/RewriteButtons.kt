package de.grunert.replyhelper.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.grunert.replyhelper.domain.RewriteType

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
            AssistChip(
                onClick = { onRewriteClick(rewriteType) },
                label = {
                    Text(
                        text = rewriteType.displayName,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                modifier = Modifier.weight(1f),
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}


