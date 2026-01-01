# ReplyHelper

Eine Android-App (MVP) für schnelle WhatsApp-Antwortvorschläge via KI-API.

## Features

- **Single-Screen UI**: Minimale Reibung, schneller Workflow
- **Clipboard Integration**: Einfaches Einfügen von Nachrichten
- **5 Antwortvorschläge**: Genau 5 Optionen pro Generierung
- **Anpassbare Parameter**:
  - Ton (Freundlich, Neutral, Kurz, Herzlich, Bestimmt, Flirty)
  - Ziel (Zusagen, Absagen, Verschieben, Nachfragen, Bedanken, Abgrenzen)
  - Länge (1 Satz, Kurz, Normal)
  - Emojis (Aus, Wenig, Normal)
  - Anrede (Du/Sie)
- **Rewrite-Buttons**: Direkte Anpassung einzelner Vorschläge (Kürzer, Freundlicher, Direkter, Ohne Emojis, Mit Rückfrage)
- **Settings**: Konfigurierbare API-Einstellungen und Defaults

## Technischer Stack

- **Sprache**: Kotlin
- **UI**: Jetpack Compose + Material3
- **Architektur**: MVVM mit StateFlow
- **Netzwerk**: Ktor Client
- **Persistenz**: DataStore Preferences
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)

## Build & Run

### Voraussetzungen

- Android Studio Hedgehog (2023.1.1) oder neuer
- JDK 17
- Android SDK mit API Level 34

### Build

```bash
./gradlew build
```

### Install auf Gerät/Emulator

```bash
./gradlew installDebug
```

### Tests ausführen

```bash
./gradlew test
```

## Konfiguration

Nach dem ersten Start:

1. Öffne die Einstellungen (Icon oben rechts)
2. Konfiguriere:
   - **API Key**: Dein API-Key für den KI-Provider
   - **Base URL**: Endpoint (z.B. `https://api.openai.com/v1` oder OpenRouter)
   - **Model**: Model-ID (z.B. `gpt-3.5-turbo`)
   - **Defaults**: Standardwerte für Ton, Ziel, Länge, Emojis, Anrede

### Unterstützte Provider

Die App nutzt ein OpenAI-kompatibles ChatCompletions-Format. Unterstützt werden:

- OpenAI API
- OpenRouter
- Andere OpenAI-kompatible APIs

### API Response Format

Die API muss JSON zurückgeben:

```json
{
  "choices": [
    {
      "message": {
        "content": "{\"suggestions\": [\"Antwort 1\", \"Antwort 2\", \"Antwort 3\", \"Antwort 4\", \"Antwort 5\"]}"
      }
    }
  ]
}
```

Der Content sollte ein JSON-Objekt mit einem `suggestions` Array sein.

## Verwendung

1. **Nachricht einfügen**: Tippe auf "Einfügen" oder füge Text manuell ein
2. **Parameter wählen**: Wähle Ton, Ziel, Länge, Emojis, Anrede (Defaults sind bereits gesetzt)
3. **Generieren**: Tippe auf "Vorschläge generieren"
4. **Auswählen**: Tippe auf einen Vorschlag → wird in die Zwischenablage kopiert
5. **Anpassen**: Nutze die Rewrite-Buttons für schnelle Variationen

## Projektstruktur

```
app/src/main/java/de/grunert/wasantwort/
├── App.kt                      # MainActivity
├── di/
│   └── AppContainer.kt        # Dependency Injection
├── ui/
│   ├── MainScreen.kt          # Haupt-UI
│   ├── SettingsScreen.kt      # Settings-UI
│   ├── components/            # Wiederverwendbare UI-Komponenten
│   └── theme/                 # Material3 Theme
├── domain/
│   ├── Models.kt              # Domain-Models (Enums)
│   ├── PromptBuilder.kt       # Prompt-Generierung
│   └── ParseSuggestions.kt    # Response-Parsing
├── data/
│   ├── AiClient.kt            # Ktor HTTP Client
│   ├── AiDtos.kt              # API DTOs
│   ├── SettingsStore.kt       # DataStore
│   └── Repository.kt          # Business Logic
└── viewmodel/
    ├── MainViewModel.kt       # ViewModel
    └── MainUiState.kt         # UI State (Sealed Classes)
```

## Tests

Unit Tests sind vorhanden für:

- `PromptBuilder`: Prompt-Generierung
- `ParseSuggestions`: JSON-Parsing und Fallback-Logik

```bash
./gradlew test
```

## Lizenz

Siehe LICENSE Datei.

## Status

MVP - Funktional, aber noch nicht vollständig getestet auf allen Geräten.
