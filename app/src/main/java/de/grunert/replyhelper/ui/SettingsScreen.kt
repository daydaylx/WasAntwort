package de.grunert.replyhelper.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import de.grunert.replyhelper.data.AppSettings
import de.grunert.replyhelper.domain.EmojiLevel
import de.grunert.replyhelper.domain.Formality
import de.grunert.replyhelper.domain.Goal
import de.grunert.replyhelper.domain.Length
import de.grunert.replyhelper.domain.Tone

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

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Einstellungen",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = apiKey,
            onValueChange = { apiKey = it },
            label = { Text("API Key") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        OutlinedTextField(
            value = baseUrl,
            onValueChange = { baseUrl = it },
            label = { Text("Base URL") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = model,
            onValueChange = { model = it },
            label = { Text("Model") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        Text(
            text = "Standard-Einstellungen",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Default Tone
        Text("Standard-Ton", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 8.dp))
        Tone.values().forEach { tone ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = defaultTone == tone,
                    onClick = { defaultTone = tone }
                )
                Text(tone.displayName, modifier = Modifier.padding(end = 16.dp))
            }
        }

        // Default Goal
        Text("Standard-Ziel", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 8.dp))
        Goal.values().forEach { goal ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = defaultGoal == goal,
                    onClick = { defaultGoal = goal }
                )
                Text(goal.displayName, modifier = Modifier.padding(end = 16.dp))
            }
        }

        // Default Length
        Text("Standard-Länge", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 8.dp))
        Length.values().forEach { length ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = defaultLength == length,
                    onClick = { defaultLength = length }
                )
                Text(length.displayName, modifier = Modifier.padding(end = 16.dp))
            }
        }

        // Default Emoji Level
        Text("Standard-Emojis", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 8.dp))
        EmojiLevel.values().forEach { emojiLevel ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = defaultEmojiLevel == emojiLevel,
                    onClick = { defaultEmojiLevel = emojiLevel }
                )
                Text(emojiLevel.displayName, modifier = Modifier.padding(end = 16.dp))
            }
        }

        // Default Formality
        Text("Standard-Anrede", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 8.dp))
        Formality.values().forEach { formality ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = defaultFormality == formality,
                    onClick = { defaultFormality = formality }
                )
                Text(formality.displayName, modifier = Modifier.padding(end = 16.dp))
            }
        }

        Button(
            onClick = {
                onSave(
                    AppSettings(
                        apiKey = apiKey,
                        baseUrl = baseUrl,
                        model = model,
                        defaultTone = defaultTone,
                        defaultGoal = defaultGoal,
                        defaultLength = defaultLength,
                        defaultEmojiLevel = defaultEmojiLevel,
                        defaultFormality = defaultFormality
                    )
                )
                onDismiss()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Speichern")
        }
    }
}

