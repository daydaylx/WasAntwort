# Repository-Richtlinien

Dieses Repository hostet WasAntwort, ein Android-MVP, das KI-Antwortvorschläge generiert.

## Projektstruktur & Modulorganisation
- `app/src/main/java/de/grunert/wasantwort/` enthält den Kotlin-App-Code, aufgeteilt in `ui/`, `viewmodel/`, `data/`, `domain/` und `di/`.
- `app/src/main/res/` enthält Android-Ressourcen; benutzerseitige Strings liegen in `values/strings.xml` und werden auf Deutsch gehalten.
- `app/src/test/java/de/grunert/wasantwort/` enthält Unit-Tests, organisiert nach Paket.
- Gradle-Konfiguration liegt in `build.gradle.kts`, `settings.gradle.kts` und `gradle/`.

## Build-, Test- und Entwicklungsbefehle
- `./gradlew build` baut alle Varianten.
- `./gradlew installDebug` installiert die Debug-APK auf einem verbundenen Gerät oder Emulator.
- `./gradlew test` führt Unit-Tests aus.
- `./gradlew lint` führt Android Lint aus (Bericht in `app/build/reports/lint-results-debug.html`).
- `./gradlew :app:compileDebugKotlin` führt die Kotlin-Kompilierung zur Typprüfung aus.
- `./gradlew test --tests "de.grunert.wasantwort.domain.PromptBuilderTest"` führt eine einzelne Testklasse aus.
- `./gradlew clean build` führt einen sauberen Neuaufbau durch.

## Programmierstil & Namenskonventionen
- Kotlin-Stil, 4-Leerzeichen-Einrückung, keine Tabs; Funktionen klein halten und Composables zustandslos, wo möglich.
- Klassen- und Composable-Namen verwenden PascalCase; Funktionen und Variablen verwenden camelCase.
- Enum-Werte und UI-Strings folgen der bestehenden deutschen Benennung in `domain/Models.kt` und `app/src/main/res/values/strings.xml`.

## Test-Richtlinien
- Frameworks: JUnit 5 (Jupiter), MockK, kotlinx-coroutines-test.
- Tests `*Test.kt` benennen und Produktionspakete spiegeln.
- Prompt-Erstellung, Antwort-Parsing (einschließlich Fallback-Pfade) und Repository-Fehlerbehandlung abdecken.
- Lint und Kompilierung/Typprüfung vor PRs für schnelles Feedback ausführen.

## Commit- & Pull-Request-Richtlinien
- Die jüngste Historie mischt `feat(ui): ...`, `fix(build): ...`, `docs: ...` und `Feature:`/`Refactor:`. Bevorzuge `type(scope): summary` im Imperativ (Beispiel: `feat(ui): style presets hinzufügen`).
- PRs sollten eine kurze Zusammenfassung, Testergebnisse (mindestens `./gradlew test`) und Screenshots für UI-Änderungen enthalten; Issues verlinken, wenn zutreffend.

## Sicherheits- & Konfigurationstipps
- Geheimnisse aus dem Repo heraushalten; `local.properties` und `keystore.properties` sind nur lokal.
- API-Schlüssel werden in den App-Einstellungen gesetzt, nicht hardcodiert.