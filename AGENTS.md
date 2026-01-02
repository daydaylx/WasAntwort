# Repository Guidelines

This repository hosts WasAntwort, an Android MVP that generates AI reply suggestions.

## Project Structure & Module Organization
- `app/src/main/java/de/grunert/wasantwort/` contains Kotlin app code, split into `ui/`, `viewmodel/`, `data/`, `domain/`, and `di/`.
- `app/src/main/res/` holds Android resources; user-facing strings live in `values/strings.xml` and are kept in German.
- `app/src/test/java/de/grunert/wasantwort/` contains unit tests organized by package.
- Gradle config lives in `build.gradle.kts`, `settings.gradle.kts`, and `gradle/`.

## Build, Test, and Development Commands
- `./gradlew build` builds all variants.
- `./gradlew installDebug` installs the debug APK on a connected device or emulator.
- `./gradlew test` runs unit tests.
- `./gradlew lint` runs Android Lint (report in `app/build/reports/lint-results-debug.html`).
- `./gradlew :app:compileDebugKotlin` runs Kotlin compilation for type checking.
- `./gradlew test --tests "de.grunert.wasantwort.domain.PromptBuilderTest"` runs a single test class.
- `./gradlew clean build` performs a clean rebuild.

## Coding Style & Naming Conventions
- Kotlin style, 4-space indentation, no tabs; keep functions small and composables stateless where possible.
- Class and composable names use PascalCase; functions and variables use camelCase.
- Enum values and UI strings follow the existing German naming in `domain/Models.kt` and `app/src/main/res/values/strings.xml`.

## Testing Guidelines
- Frameworks: JUnit 5 (Jupiter), MockK, kotlinx-coroutines-test.
- Name tests `*Test.kt` and mirror production packages.
- Cover prompt building, response parsing (including fallback paths), and repository error handling.
- Run lint and a compile/typecheck pass before PRs for fast feedback.

## Commit & Pull Request Guidelines
- Recent history mixes `feat(ui): ...`, `fix(build): ...`, `docs: ...` and `Feature:`/`Refactor:`. Prefer `type(scope): summary` in imperative mood (example: `feat(ui): add style presets`).
- PRs should include a short summary, test results (at least `./gradlew test`), and screenshots for UI changes; link issues when applicable.

## Security & Configuration Tips
- Keep secrets out of the repo; `local.properties` and `keystore.properties` are local-only.
- API keys are set in the app settings, not hardcoded.
