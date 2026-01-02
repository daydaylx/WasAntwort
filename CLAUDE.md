# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**WasAntwort** (formerly ReplyHelper) is an Android MVP app for generating AI-powered WhatsApp reply suggestions. The core goal is speed: from message input to copied reply in under 20 seconds.

**Language:** German (UI strings, prompts, documentation)
**Stack:** Kotlin, Jetpack Compose, Material3, Ktor, DataStore

## Build Commands

```bash
# Build the project
./gradlew build

# Install debug APK to device/emulator
./gradlew installDebug

# Run unit tests
./gradlew test

# Run specific test class
./gradlew test --tests "de.grunert.wasantwort.domain.PromptBuilderTest"

# Clean build
./gradlew clean build
```

## Architecture

### MVVM + Repository Pattern

```
UI (Compose) → ViewModel (StateFlow) → Repository → AiClient/DataStore
                                      ↓
                                   Domain (Models, Builders, Parsers)
```

### Key Components

**ViewModel Layer** (`/viewmodel/`)
- `MainViewModel.kt` - Central state management using `StateFlow<MainScreenState>`
- `MainUiState.kt` - Sealed classes: `Idle | Loading | Success | Error`
- Single source of truth for UI state

**Repository** (`/data/Repository.kt`)
- Orchestrates business logic between ViewModel and data sources
- Returns `Result<T>` for functional error handling
- Manages AiClient lifecycle based on settings changes

**AiClient** (`/data/AiClient.kt`)
- Ktor HTTP client with 10s connect timeout, 30s request timeout
- OpenAI-compatible ChatCompletions format
- Error handling: network, timeout, auth (401), forbidden (403), rate limit (429)

**Domain Layer** (`/domain/`)
- `Models.kt` - Core enums: `Tone`, `Goal`, `Length`, `EmojiLevel`, `Formality`, `RewriteType`
- `PromptBuilder.kt` - Constructs system + user prompts from configuration
- `ParseSuggestions.kt` - Parses API responses with JSON + heuristic fallback
- `ConversationHistory.kt` - History management data structures
- `StylePreset.kt` - Preset combinations of style parameters

**Persistence** (`/data/`)
- `SettingsStore.kt` - DataStore for API config and defaults (cached in memory)
- `HistoryStore.kt` - DataStore for conversation history (max 100 entries)
- Uses kotlinx.serialization for JSON

**Dependency Injection** (`/di/AppContainer.kt`)
- Manual DI (no framework)
- Created once in `App.kt` (MainActivity)
- Provides: SettingsStore, HistoryStore, Repository, ViewModelFactory

## Critical Design Decisions

### Speed Over Features
The app prioritizes minimal friction. Design decisions favor reducing steps:
- Single-screen UI (no navigation)
- Clipboard integration for quick paste/copy
- Default values pre-selected
- No unnecessary confirmation dialogs

### Error Resilience
- `ParseSuggestions.kt` has fallback heuristic parsing if JSON fails
- Always returns exactly 5 suggestions (pads if necessary)
- Network errors show user-friendly German messages

### State Management
- All UI state flows through `MainViewModel._uiState: StateFlow<MainScreenState>`
- UI recomposes reactively via `collectAsStateWithLifecycle()`
- Never mutate state directly - always use ViewModel methods

## Domain Models

### Main Enums (in German)
```kotlin
Tone: Freundlich | Neutral | Kurz | Herzlich | Bestimmt | Flirty
Goal: Zusagen | Absagen | Verschieben | Nachfragen | Bedanken | Abgrenzen
Length: EinSatz | Kurz | Normal
EmojiLevel: Aus | Wenig | Normal
Formality: Du | Sie
RewriteType: Kuerzer | Freundlicher | Direkter | OhneEmojis | MitRueckfrage
```

### Predefined Models
```kotlin
LlamaFree // meta-llama/llama-3.3-70b-instruct (free)
MimoFree // mistralai/mistral-small (free)
Gpt4oMini // gpt-4o-mini (premium)
ClaudeHaiku // claude-3.5-haiku (premium)
```

## API Integration

### Request Flow
1. User input + parameters → `PromptBuilder.buildGeneratePrompt()`
2. Repository → `AiClient.generateSuggestions()` → HTTP POST
3. Parse response via `ParseSuggestions.parseSuggestionsResponse()`
4. Save to `HistoryStore` + update `StateFlow`

### Expected API Response
```json
{
  "choices": [{
    "message": {
      "content": "{\"suggestions\": [\"...\", \"...\", \"...\", \"...\", \"...\"]}"
    }
  }]
}
```

### Supported Providers
Any OpenAI-compatible API (OpenRouter, OpenAI, etc.)

## Testing

### Test Structure
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

### Testing Tools
- JUnit 5 (Jupiter)
- MockK 1.13.8 for mocking
- kotlinx-coroutines-test 1.7.3 for suspend functions

### Writing Tests
- Use `runTest {}` for coroutine tests
- Mock `AiClient` when testing Repository
- Test both JSON parsing and fallback logic in `ParseSuggestionsTest`

## UI Components

### Main Screens
- `MainScreen.kt` - Primary UI (input, chips, suggestions)
- `SettingsScreen.kt` - API configuration
- `HistoryScreen.kt` - Conversation history browser

### Reusable Components (`/ui/components/`)
- `InputCard.kt` - Text input with paste/clear
- `OptionChips.kt` - Horizontal scrollable chip selectors
- `SuggestionCard.kt` - Individual suggestion with copy/rewrite
- `RewriteButtons.kt` - 5 rewrite action buttons
- `StylePresetsRow.kt` - Quick style presets
- `StyleCustomizationBottomSheet.kt` - Advanced customization
- `CosmicBackground.kt` - Animated starfield
- `Glass.kt` - Glassmorphic surface components

### Theme
- Material3 Dark theme
- Custom colors in `/ui/theme/Color.kt`
- German string resources in `/res/values/strings.xml`

## Data Flow Patterns

### Generating Suggestions
```kotlin
// In ViewModel
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

### Saving Settings
```kotlin
// Settings changes invalidate cached AiClient
settingsStore.setApiKey(newKey) // invalidates cache
repository.closeClient() // forces new client on next call
```

## Common Patterns

### Adding New Enum Values
1. Add to enum in `domain/Models.kt`
2. Update `displayName` property
3. Add German string to `res/values/strings.xml`
4. Update `PromptBuilder.kt` to include in prompt
5. Add tests in `ModelsTest.kt`

### Adding New UI Component
1. Create in `/ui/components/`
2. Use Material3 components
3. Follow existing glassmorphic style
4. Accept state as parameters (stateless composables)
5. Hoist state to ViewModel

### Error Handling
Always use German error messages:
- Network errors: "Kein Internet"
- Auth errors: "API-Key prüfen"
- Timeout: "Timeout: Bitte erneut versuchen"
- Rate limit: "Bitte kurz warten"

## File Locations Reference

| Component | Path |
|-----------|------|
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

## Known Constraints

- Min SDK: 26 (Android 8.0)
- Target SDK: 34 (Android 14)
- Max conversation history: 100 entries
- Input text limit: ~4000 characters (soft limit)
- Always returns exactly 5 suggestions
- No external DI framework (intentional)
- No Room database (DataStore for simplicity)

## Development Notes

- UI strings and prompts are in German - maintain this
- Prioritize speed and simplicity over features
- Avoid over-engineering - this is an MVP
- Test both happy path and error cases
- DataStore operations should be wrapped in try-catch
- Always validate API responses before parsing
