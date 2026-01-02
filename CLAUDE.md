# CLAUDE.md

Diese Datei bietet Anleitung für Claude Code (claude.ai/code) bei der Arbeit mit Code in diesem Repository.

## Projektübersicht

**WasAntwort** (früher ReplyHelper) ist eine Android-MVP-App zur Generierung von KI-gestützten WhatsApp-Antwortvorschlägen. Das Hauptziel ist Geschwindigkeit: von der Nachrichteneingabe bis zur kopierten Antwort in unter 20 Sekunden.

**Sprache:** Deutsch (UI-Strings, Prompts, Dokumentation)
**Stack:** Kotlin, Jetpack Compose, Material3, Ktor, DataStore

## Build-Befehle

```bash
# Projekt bauen
./gradlew build

# Debug-APK auf Gerät/Emulator installieren
./gradlew installDebug

# Unit-Tests ausführen
./gradlew test

# Spezifische Testklasse ausführen
./gradlew test --tests "de.grunert.wasantwort.domain.PromptBuilderTest"

# Sauberer Build
./gradlew clean build
```

## Architektur

### MVVM + Repository Pattern

```
UI (Compose) → ViewModel (StateFlow) → Repository → AiClient/DataStore
                                      ↓
                                   Domain (Models, Builders, Parsers)
```

### Schlüsselkomponenten

**ViewModel Layer** (`/viewmodel/`)
- `MainViewModel.kt` - Zentrales Zustandsmanagement mittels `StateFlow<MainScreenState>`
- `MainUiState.kt` - Sealed Classes: `Idle | Loading | Success | Error`
- Single Source of Truth für den UI-Zustand

**Repository** (`/data/Repository.kt`)
- Orchestriert Geschäftslogik zwischen ViewModel und Datenquellen
- Gibt `Result<T>` für funktionale Fehlerbehandlung zurück
- Verwaltet AiClient-Lebenszyklus basierend auf Einstellungsänderungen

**AiClient** (`/data/AiClient.kt`)
- Ktor HTTP Client mit 10s Verbindungs-Timeout, 30s Anfrage-Timeout
- OpenAI-kompatibles ChatCompletions-Format
- Fehlerbehandlung: Netzwerk, Timeout, Auth (401), Verboten (403), Rate Limit (429)

**Domain Layer** (`/domain/`)
- `Models.kt` - Kern-Enums: `Tone`, `Goal`, `Length`, `EmojiLevel`, `Formality`, `RewriteType`
- `PromptBuilder.kt` - Erstellt System- + Benutzer-Prompts aus Konfiguration
- `ParseSuggestions.kt` - Parst API-Antworten mit JSON + heuristischem Fallback
- `ConversationHistory.kt` - Verwaltung der Verlaufsdatenstrukturen
- `StylePreset.kt` - Voreingestellte Kombinationen von Stilparametern

**Persistenz** (`/data/`)
- `SettingsStore.kt` - DataStore für API-Konfig und Standards (im Speicher gecacht)
- `HistoryStore.kt` - DataStore für Gesprächsverlauf (max. 100 Einträge)
- Nutzt kotlinx.serialization für JSON

**Dependency Injection** (`/di/AppContainer.kt`)
- Manuelle DI (kein Framework)
- Einmalig in `App.kt` (MainActivity) erstellt
- Stellt bereit: SettingsStore, HistoryStore, Repository, ViewModelFactory

## Kritische Designentscheidungen

### Geschwindigkeit vor Features
Die App priorisiert minimale Reibung. Designentscheidungen begünstigen reduzierte Schritte:
- Ein-Screen-UI (keine Navigation)
- Clipboard-Integration für schnelles Einfügen/Kopieren
- Standardwerte vorausgewählt
- Keine unnötigen Bestätigungsdialoge

### Fehlerresilienz
- `ParseSuggestions.kt` hat heuristisches Fallback-Parsing, falls JSON fehlschlägt
- Gibt immer genau 5 Vorschläge zurück (füllt auf, falls nötig)
- Netzwerkfehler zeigen benutzerfreundliche deutsche Nachrichten

### Zustandsmanagement
- Aller UI-Zustand fließt durch `MainViewModel._uiState: StateFlow<MainScreenState>`
- UI recomposet reaktiv via `collectAsStateWithLifecycle()`
- Zustand nie direkt mutieren - immer ViewModel-Methoden nutzen

## Domain Models

### Haupt-Enums (auf Deutsch)
```kotlin
Tone: Freundlich | Neutral | Kurz | Herzlich | Bestimmt | Flirty
Goal: Zusagen | Absagen | Verschieben | Nachfragen | Bedanken | Abgrenzen
Length: EinSatz | Kurz | Normal
EmojiLevel: Aus | Wenig | Normal
Formality: Du | Sie
RewriteType: Kuerzer | Freundlicher | Direkter | OhneEmojis | MitRueckfrage
```

### Vordefinierte Modelle
```kotlin
LlamaFree // meta-llama/llama-3.3-70b-instruct (free)
MimoFree // mistralai/mistral-small (free)
Gpt4oMini // gpt-4o-mini (premium)
ClaudeHaiku // claude-3.5-haiku (premium)
```

## API-Integration

### Anfrage-Ablauf
1. Benutzereingabe + Parameter → `PromptBuilder.buildGeneratePrompt()`
2. Repository → `AiClient.generateSuggestions()` → HTTP POST
3. Antwort parsen via `ParseSuggestions.parseSuggestionsResponse()`
4. In `HistoryStore` speichern + `StateFlow` aktualisieren

### Erwartete API-Antwort
```json
{
  "choices": [{
    "message": {
      "content": "{\"suggestions\": [\"...\", \"...\", \"...\", \"...\", \"...\"]}"
    }
  }]
}
```

### Unterstützte Provider
Jede OpenAI-kompatible API (OpenRouter, OpenAI, etc.)

## Testen

### Teststruktur
```
/app/src/test/java/de/grunert/wasantwort/
├── data/
│   └── AiClientTest.kt
└── domain/
    ├── PromptBuilderTest.kt
    ├── ParseSuggestionsTest.kt
    ├── ModelsTest.kt
    └── ConversationHistoryTest.kt
```

### Test-Tools
- JUnit 5 (Jupiter)
- MockK 1.13.8 für Mocking
- kotlinx-coroutines-test 1.7.3 für Suspend-Funktionen

### Tests schreiben
- `runTest {}` für Coroutine-Tests verwenden
- `AiClient` mocken beim Testen des Repositorys
- Sowohl JSON-Parsing als auch Fallback-Logik in `ParseSuggestionsTest` testen

## UI-Komponenten

### Haupt-Screens
- `MainScreen.kt` - Haupt-UI (Eingabe, Chips, Vorschläge)
- `SettingsScreen.kt` - API-Konfiguration
- `HistoryScreen.kt` - Gesprächsverlauf-Browser

### Wiederverwendbare Komponenten (`/ui/components/`)
- `InputCard.kt` - Texteingabe mit Einfügen/Löschen
- `OptionChips.kt` - Horizontal scrollbare Chip-Selektoren
- `SuggestionCard.kt` - Einzelner Vorschlag mit Kopieren/Umschreiben
- `RewriteButtons.kt` - 5 Umschreiben-Aktionsbuttons
- `StylePresetsRow.kt` - Schnelle Stil-Presets
- `StyleCustomizationBottomSheet.kt` - Erweiterte Anpassung
- `CosmicBackground.kt` - Animiertes Sternenfeld
- `Glass.kt` - Glassmorphic Surface-Komponenten

### Theme
- Material3 Dark Theme
- Eigene Farben in `/ui/theme/Color.kt`
- Deutsche String-Ressourcen in `/res/values/strings.xml`

## Datenfluss-Muster

### Vorschläge generieren
```kotlin
// Im ViewModel
viewModelScope.launch {
    _uiState.update { it.copy(uiState = MainUiState.Loading) }
    repository.generateSuggestions(...).fold(
        onSuccess = { suggestions ->
            _uiState.update { it.copy(
                suggestions = suggestions,
                uiState = MainUiState.Success
            )}
        },
        onFailure = { error ->
            _uiState.update { it.copy(uiState = MainUiState.Error(error.message)) }
        }
    )
}
```

### Einstellungen speichern
```kotlin
// Einstellungsänderungen invalidieren gecachten AiClient
settingsStore.setApiKey(newKey) // invalidiert Cache
repository.closeClient() // erzwingt neuen Client beim nächsten Aufruf
```

## Häufige Muster

### Neuen Enum-Wert hinzufügen
1. Zum Enum in `domain/Models.kt` hinzufügen
2. `displayName`-Eigenschaft aktualisieren
3. Deutschen String zu `res/values/strings.xml` hinzufügen
4. `PromptBuilder.kt` aktualisieren, um in Prompt aufzunehmen
5. Tests in `ModelsTest.kt` hinzufügen

### Neue UI-Komponente hinzufügen
1. In `/ui/components/` erstellen
2. Material3-Komponenten verwenden
3. Bestehendem Glassmorphic-Stil folgen
4. Zustand als Parameter akzeptieren (zustandslose Composables)
5. Zustand ins ViewModel heben

### Fehlerbehandlung
Immer deutsche Fehlermeldungen verwenden:
- Netzwerkfehler: "Kein Internet"
- Auth-Fehler: "API-Key prüfen"
- Timeout: "Timeout: Bitte erneut versuchen"
- Rate Limit: "Bitte kurz warten"

## Datei-Referenz

| Komponente | Pfad |
|------------|------|
| App Entry | `/app/src/main/java/de/grunert/wasantwort/App.kt` |
| ViewModel | `/viewmodel/MainViewModel.kt`, `MainUiState.kt` |
| Repository | `/data/Repository.kt` |
| API Client | `/data/AiClient.kt`, `AiDtos.kt` |
| Settings | `/data/SettingsStore.kt`, `HistoryStore.kt` |
| Domain | `/domain/Models.kt`, `PromptBuilder.kt`, `ParseSuggestions.kt` |
| DI | `/di/AppContainer.kt` |
| Main UI | `/ui/MainScreen.kt`, `/ui/SettingsScreen.kt` |
| Components | `/ui/components/*` |
| Theme | `/ui/theme/*` |
| Tests | `/app/src/test/java/de/grunert/wasantwort/*` |

## Bekannte Einschränkungen

- Min SDK: 26 (Android 8.0)
- Target SDK: 34 (Android 14)
- Max Gesprächsverlauf: 100 Einträge
- Eingabetext-Limit: ~4000 Zeichen (Soft Limit)
- Gibt immer genau 5 Vorschläge zurück
- Kein externes DI-Framework (beabsichtigt)
- Keine Room-Datenbank (DataStore der Einfachheit halber)

## Entwicklungsnotizen

- UI-Strings und Prompts sind auf Deutsch - dies beibehalten
- Geschwindigkeit und Einfachheit vor Features priorisieren
- Over-Engineering vermeiden - dies ist ein MVP
- Sowohl Happy Path als auch Fehlerfälle testen
- DataStore-Operationen sollten in try-catch gewrapped sein
- API-Antworten vor dem Parsen immer validieren