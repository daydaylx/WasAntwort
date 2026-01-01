# Changelog

## [Unreleased]

### Added
- UI: Implement horizontal scrolling for OptionChips to improve layout on small screens.
- UI: Add visual checkmark and haptic feedback when copying suggestions.
- UI: Add skeleton loading state during generation.
- UI: Add shake animation when attempting to generate with empty input.
- UI: Auto-hide keyboard on 'Done' action in input field.
- Assets: Add app icons (adaptive icons).
- Build: Add missing Gradle Wrapper jar.

### Fixed
- Build: Fix missing serialization plugin in root build.gradle.kts.
- Build: Update Compose BOM to 2024.02.00 to support SegmentedButton.
- Code: Fix Ktor client configuration (timeouts, imports).
- Code: Fix unresolved references in SettingsScreen and Theme.
