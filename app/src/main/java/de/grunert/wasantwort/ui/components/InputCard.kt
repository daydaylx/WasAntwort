package de.grunert.wasantwort.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import de.grunert.wasantwort.ui.theme.Accent1
import de.grunert.wasantwort.ui.theme.Danger
import de.grunert.wasantwort.ui.theme.GlassBorderColor
import de.grunert.wasantwort.ui.theme.GlassSurfaceBase
import de.grunert.wasantwort.ui.theme.TextPrimary
import de.grunert.wasantwort.ui.theme.TextSecondary

import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.draw.shadow
import de.grunert.wasantwort.ui.theme.GlowPrimary

@Composable
fun InputCard(
    text: String,
    onTextChange: (String) -> Unit,
    onPasteClick: () -> Unit,
    onClearClick: () -> Unit,
    onGenerateClick: (() -> Unit)? = null,
    isGenerateEnabled: Boolean = true,
    isPasteEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var isFocused by remember { mutableStateOf(false) }
    val maxChars = 4000
    val canSend = onGenerateClick != null && isGenerateEnabled && text.isNotBlank()

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isFocused) 12.dp else 0.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = GlowPrimary,
                ambientColor = GlowPrimary
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nachricht",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary
                )
                if (text.isNotEmpty()) {
                    val counterColor = if (text.length > 3500) Danger else TextSecondary.copy(alpha = 0.6f)
                    Text(
                        text = "${text.length}/$maxChars",
                        style = MaterialTheme.typography.labelSmall,
                        color = counterColor
                    )
                }
            }
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isFocused = it.isFocused },
                    placeholder = {
                        Text(
                            text = "Nachricht einfügen, dann Vorschläge generieren.",
                            color = TextSecondary
                        )
                    },
                    maxLines = 10,
                    minLines = 4,
                    keyboardOptions = KeyboardOptions(
                        imeAction = if (onGenerateClick != null) ImeAction.Send else ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (canSend) {
                                onGenerateClick?.invoke()
                                keyboardController?.hide()
                            }
                        },
                        onDone = { keyboardController?.hide() }
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = GlassSurfaceBase.copy(alpha = 0.32f),
                        unfocusedContainerColor = GlassSurfaceBase.copy(alpha = 0.24f),
                        disabledContainerColor = GlassSurfaceBase.copy(alpha = 0.20f),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedPlaceholderColor = TextSecondary,
                        unfocusedPlaceholderColor = TextSecondary,
                        focusedBorderColor = Accent1,
                        unfocusedBorderColor = GlassBorderColor,
                        focusedLabelColor = Accent1,
                        unfocusedLabelColor = TextSecondary
                    ),
                    trailingIcon = {
                        if (onGenerateClick != null) {
                            IconButton(
                                onClick = {
                                    if (canSend) {
                                        onGenerateClick()
                                        keyboardController?.hide()
                                    }
                                },
                                enabled = canSend,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Senden",
                                    tint = if (canSend) Accent1 else TextSecondary.copy(alpha = 0.4f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                )
            }

            // Action Buttons Row with Labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Paste Button with Label
                TextButton(
                    onClick = onPasteClick,
                    enabled = isPasteEnabled,
                    modifier = Modifier.padding(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ContentPaste,
                        contentDescription = null,
                        tint = if (isPasteEnabled) Accent1 else TextSecondary.copy(alpha = 0.38f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Einfügen",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isPasteEnabled) Accent1 else TextSecondary.copy(alpha = 0.38f)
                    )
                }

                // Clear Button with Label (nur sichtbar wenn Text vorhanden)
                if (text.isNotEmpty()) {
                    TextButton(
                        onClick = onClearClick,
                        modifier = Modifier.padding(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Leeren",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}
