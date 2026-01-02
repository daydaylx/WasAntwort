# Changelog

## [Unreleased]

### Hinzugefügt
- Feature: Unterstützung für das Teilen von Text aus anderen Apps (ACTION_SEND).
- Feature: Automatische Erkennung von Chatverläufen ("Name: Nachricht") für kontextbewusste Antworten.
- UI: Funkelnde Animation für Sterne im kosmischen Hintergrund hinzugefügt.
- UI: "Ghost"-Stil für Eingabefeld implementiert (bessere Integration in GlassCard).
- UI: Horizontales Scrollen für OptionChips implementiert (besseres Layout auf kleinen Bildschirmen).
- UI: Visuelles Häkchen und haptisches Feedback beim Kopieren von Vorschlägen hinzugefügt.
- UI: Skelett-Ladezustand während der Generierung hinzugefügt.
- UI: Schüttel-Animation bei Versuch zu generieren mit leerem Eingabefeld.
- UI: Tastatur automatisch ausblenden bei 'Fertig'-Aktion im Eingabefeld.
- Assets: App-Icons hinzugefügt (Adaptive Icons).
- Build: Fehlende Gradle Wrapper jar hinzugefügt.

### Behoben
- Build: Fehlendes Serialization-Plugin in root build.gradle.kts behoben.
- Build: Update Compose BOM auf 2024.02.00 zur Unterstützung von SegmentedButton.
- Code: Ktor-Client-Konfiguration korrigiert (Timeouts, Imports).
- Code: Unresolved References in SettingsScreen und Theme behoben.