package de.grunert.wasantwort.ui

import androidx.compose.foundation.layout.Alignment
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import de.grunert.wasantwort.data.AppSettings
import de.grunert.wasantwort.domain.EmojiLevel
import de.grunert.wasantwort.domain.Formality
import de.grunert.wasantwort.domain.Goal
import de.grunert.wasantwort.domain.Length
import de.grunert.wasantwort.domain.Tone
import de.grunert.wasantwort.ui.components.GlassButton
import de.grunert.wasantwort.ui.components.GlassCard
import de.grunert.wasantwort.ui.theme.Accent1
import de.grunert.wasantwort.ui.theme.Danger
import de.grunert.wasantwort.ui.theme.GlassBorderColor
import de.grunert.wasantwort.ui.theme.TextPrimary
import de.grunert.wasantwort.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    currentSettings: AppSettings?,
    onSave: (AppSettings) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settings = currentSettings ?: AppSettings(
        apiKey = "",
        baseUrl = "https://api.openai.com/v1",
        model = "gpt-3.5-turbo",
        defaultTone = Tone.FREUNDLICH,
        defaultGoal = Goal.NACHRAGEN,
        defaultLength = Length.NORMAL,
        defaultEmojiLevel = EmojiLevel.WENIG,
        defaultFormality = Formality.DU
    )

    var apiKey by remember { mutableStateOf(settings.apiKey) }
    var baseUrl by remember { mutableStateOf(settings.baseUrl) }
    var model by remember { mutableStateOf(settings.model) }
    var defaultTone by remember { mutableStateOf(settings.defaultTone) }
    var defaultGoal by remember { mutableStateOf(settings.defaultGoal) }
    var defaultLength by remember { mutableStateOf(settings.defaultLength) }
    var defaultEmojiLevel by remember { mutableStateOf(settings.defaultEmojiLevel) }
    var defaultFormality by remember { mutableStateOf(settings.defaultFormality) }
    var useContext by remember { mutableStateOf(settings.useContext) }
    var validationError by remember { mutableStateOf<String?>(null) }

    GlassCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Einstellungen",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (validationError != null) {
                Text(
                    text = validationError!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = Danger,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            OutlinedTextField(
                value = apiKey,
                onValueChange = {
                    apiKey = it
                    validationError = null
                },
                label = { Text("API Key", color = TextSecondary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                isError = validationError != null && apiKey.isBlank(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedPlaceholderColor = TextSecondary,
                    unfocusedPlaceholderColor = TextSecondary,
                    focusedBorderColor = Accent1,
                    unfocusedBorderColor = GlassBorderColor,
                    focusedLabelColor = Accent1,
                    unfocusedLabelColor = TextSecondary,
                    errorBorderColor = Danger,
                    errorLabelColor = Danger
                )
            )

            OutlinedTextField(
                value = baseUrl,
                onValueChange = {
                    baseUrl = it
                    validationError = null
                },
                label = { Text("Base URL", color = TextSecondary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                singleLine = true,
                isError = validationError != null && !baseUrl.startsWith("http"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedPlaceholderColor = TextSecondary,
                    unfocusedPlaceholderColor = TextSecondary,
                    focusedBorderColor = Accent1,
                    unfocusedBorderColor = GlassBorderColor,
                    focusedLabelColor = Accent1,
                    unfocusedLabelColor = TextSecondary,
                    errorBorderColor = Danger,
                    errorLabelColor = Danger
                )
            )

            OutlinedTextField(
                value = model,
                onValueChange = {
                    model = it
                    validationError = null
                },
                label = { Text("Model", color = TextSecondary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true,
                isError = validationError != null && model.isBlank(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedPlaceholderColor = TextSecondary,
                    unfocusedPlaceholderColor = TextSecondary,
                    focusedBorderColor = Accent1,
                    unfocusedBorderColor = GlassBorderColor,
                    focusedLabelColor = Accent1,
                    unfocusedLabelColor = TextSecondary,
                    errorBorderColor = Danger,
                    errorLabelColor = Danger
                )
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = GlassBorderColor,
                thickness = 1.dp
            )

            Text(
                text = "Standard-Einstellungen",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
            )

            Text(
                text = "Standard-Ton",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 8.dp)
            )
            Tone.values().forEach { tone ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    RadioButton(
                        selected = defaultTone == tone,
                        onClick = { defaultTone = tone }
                    )
                    Text(
                        text = tone.displayName,
                        color = TextPrimary,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            }

            Text(
                text = "Standard-Ziel",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 8.dp)
            )
            Goal.values().forEach { goal ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    RadioButton(
                        selected = defaultGoal == goal,
                        onClick = { defaultGoal = goal }
                    )
                    Text(
                        text = goal.displayName,
                        color = TextPrimary,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            }

            Text(
                text = "Standard-Länge",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 8.dp)
            )
            Length.values().forEach { length ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    RadioButton(
                        selected = defaultLength == length,
                        onClick = { defaultLength = length }
                    )
                    Text(
                        text = length.displayName,
                        color = TextPrimary,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            }

            Text(
                text = "Standard-Emojis",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 8.dp)
            )
            EmojiLevel.values().forEach { emojiLevel ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    RadioButton(
                        selected = defaultEmojiLevel == emojiLevel,
                        onClick = { defaultEmojiLevel = emojiLevel }
                    )
                    Text(
                        text = emojiLevel.displayName,
                        color = TextPrimary,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            }

            Text(
                text = "Standard-Anrede",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 8.dp)
            )
            Formality.values().forEach { formality ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    RadioButton(
                        selected = defaultFormality == formality,
                        onClick = { defaultFormality = formality }
                    )
                    Text(
                        text = formality.displayName,
                        color = TextPrimary,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = GlassBorderColor,
                thickness = 1.dp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Konversations-Kontext verwenden",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = "KI erinnert sich an vorherige Nachrichten",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Switch(
                    checked = useContext,
                    onCheckedChange = { useContext = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Accent1,
                        checkedTrackColor = Accent1.copy(alpha = 0.5f),
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = GlassBorderColor
                    )
                )
            }

            GlassButton(
                onClick = {
                    when {
                        apiKey.isBlank() -> {
                            validationError = "API Key darf nicht leer sein"
                        }
                        !baseUrl.startsWith("http://") && !baseUrl.startsWith("https://") -> {
                            validationError = "Base URL muss mit http:// oder https:// beginnen"
                        }
                        model.isBlank() -> {
                            validationError = "Model darf nicht leer sein"
                        }
                        else -> {
                            onSave(
                                AppSettings(
                                    apiKey = apiKey.trim(),
                                    baseUrl = baseUrl.trim().removeSuffix("/"),
                                    model = model.trim(),
                                    defaultTone = defaultTone,
                                    defaultGoal = defaultGoal,
                                    defaultLength = defaultLength,
                                    defaultEmojiLevel = defaultEmojiLevel,
                                    defaultFormality = defaultFormality,
                                    useContext = useContext
                                )
                            )
                            onDismiss()
                        }
                    }
                },
                text = "Speichern",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 16.dp)
            )
        }
    }
}
