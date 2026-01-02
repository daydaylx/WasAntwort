# WasAntwort - Aufgabenliste

**Erstellt:** 2026-01-02
**Status:** In Bearbeitung

---

## Ubersicht

Dieses Dokument fasst alle identifizierten Verbesserungspunkte aus der Sicherheits-, Qualitats- und UI/UX-Analyse zusammen.

| Kategorie | Kritisch | Mittel | Niedrig |
|-----------|----------|--------|---------|
| Sicherheit | 1 | 3 | 5 |
| UI/UX | 0 | 4 | 6 |
| **Gesamt** | **1** | **7** | **11** |

---

## KRITISCH - Sofort beheben

### [S1] Hardcodierter API-Schlussel entfernen

**Datei:** `domain/Models.kt:55`

**Problem:** API-Key im Quellcode eingebettet - wird bei jedem Clone offengelegt.

**Aufgaben:**
- [ ] API-Key bei OpenRouter widerrufen
- [ ] `DEFAULT_API_KEY` Konstante entfernen
- [ ] Free-Modelle ohne voreingestellten Key anbieten
- [ ] Nutzer zur Eingabe eigener Keys auffordern
- [ ] Optional: BuildConfig mit `local.properties` fur Entwickler-Keys

---

## MITTEL - Vor Release beheben

### [S2] API-Key verschlusselt speichern

**Datei:** `data/SettingsStore.kt`

**Aufgaben:**
- [ ] `androidx.security:security-crypto` Dependency hinzufugen
- [ ] `EncryptedSharedPreferences` fur API-Key verwenden
- [ ] Migration fur bestehende unverschlusselte Keys

---

### [S3] SSL Certificate Pinning

**Datei:** `data/AiClient.kt`

**Aufgaben:**
- [ ] `res/xml/network_security_config.xml` erstellen
- [ ] Certificate Pins fur `openrouter.ai` hinzufugen
- [ ] In `AndroidManifest.xml` referenzieren

---

### [S4] URL-Validierung verbessern

**Datei:** `ui/SettingsScreen.kt`

**Aufgaben:**
- [ ] Nur `https://` URLs erlauben
- [ ] URL mit `java.net.URL` parsen und validieren
- [ ] Whitelist fur bekannte Provider (OpenRouter, OpenAI)

---

### [U1] Falsches Paste-Icon korrigieren

**Datei:** `ui/components/InputCard.kt:129`

**Problem:** `Icons.Filled.ContentCopy` wird fur Einfugen verwendet.

**Aufgaben:**
- [ ] Icon zu `Icons.Filled.ContentPaste` andern
- [ ] ContentDescription auf "Aus Zwischenablage einfugen" andern

---

### [U2] Rewrite-Optionen sichtbarer machen

**Datei:** `ui/components/RewriteButtons.kt`

**Problem:** Optionen im Dropdown versteckt, Nutzer wissen nicht, dass Umschreiben moglich ist.

**Aufgaben:**
- [ ] Dropdown durch horizontale Chip-Reihe ersetzen
- [ ] Oder: Sichtbaren "Anpassen"-Button mit Icon hinzufugen
- [ ] Tooltip beim ersten Mal anzeigen

---

### [U3] Undo fur Historie-Loschen

**Datei:** `ui/HistoryScreen.kt`

**Aufgaben:**
- [ ] Geloschten Eintrag temporar speichern (5 Sekunden)
- [ ] Snackbar mit "Ruckgangig"-Action anzeigen
- [ ] Bei Timeout endgultig loschen

---

### [U4] Accessibility verbessern

**Dateien:** Alle UI-Komponenten

**Aufgaben:**
- [ ] Alle Icons mit korrekten `contentDescription` versehen
- [ ] `GlassCard` mit `Modifier.semantics { role = Role.Button }` ausstatten
- [ ] TalkBack-Test durchfuhren
- [ ] Kontrast von `TextSecondary` auf `#C0C0C0` erhohen

---

## NIEDRIG - Nice-to-have

### [S5] Thread-Safety im Repository

**Datei:** `data/Repository.kt`

**Aufgaben:**
- [ ] `Mutex` fur `cachedClient` verwenden
- [ ] Oder `AtomicReference` nutzen

---

### [S6] History-Pagination

**Datei:** `data/HistoryStore.kt`

**Aufgaben:**
- [ ] Pagination mit Offset/Limit implementieren
- [ ] Lazy Loading in `HistoryScreen`

---

### [S7] Logging nur im Debug-Build

**Datei:** `data/AiClient.kt`

**Aufgaben:**
- [ ] `if (BuildConfig.DEBUG)` Check vor Logging-Installation

---

### [N1] Strings externalisieren

**Datei:** `res/values/strings.xml`

**Aufgaben:**
- [ ] Alle hardcodierten Strings extrahieren
- [ ] Deutsche Strings in `strings.xml` verschieben

---

### [N2] Keyboard "Send" Action

**Datei:** `ui/components/InputCard.kt`

**Aufgaben:**
- [ ] `ImeAction.Send` statt `ImeAction.Done`
- [ ] Bei Send: `onGenerateClick()` aufrufen

---

### [N3] Settings in Sektionen aufteilen

**Datei:** `ui/SettingsScreen.kt`

**Aufgaben:**
- [ ] Aufklappbare Sektionen implementieren
- [ ] "Erweiterte Einstellungen" separat

---

### [N4] Performance: Sterne reduzieren

**Datei:** `ui/components/CosmicBackground.kt`

**Aufgaben:**
- [ ] Anzahl Sterne von 70 auf 40 reduzieren
- [ ] Optional: Bei Low-Battery deaktivieren

---

### [N5] Performance: GlassCard optimieren

**Datei:** `ui/components/Glass.kt`

**Aufgaben:**
- [ ] Drei Box-Layer in einen `drawBehind` kombinieren
- [ ] `BlendMode` fur Gradients nutzen

---

## UI-Verbesserungsvorschlage

### Aktueller Zustand (Screenshot-Analyse)

```
+------------------------------------------+
| WasAntwort              [Historie] [Gear]|
+------------------------------------------+
|                                          |
|  +------------------------------------+  |
|  | Nachricht                          |  |
|  | +--------------------------------+ |  |
|  | | test                    4/4000 | |  |
|  | |                                | |  |
|  | +--------------------------------+ |  |
|  | [Copy] [X]                         |  |
|  +------------------------------------+  |
|                                          |
|  Schnellstile                            |
|  [Freundlich] [Kurz&klar] [Ablehnen] [An.|
|                                          |
|            << LEERER BEREICH >>          |
|                                          |
|  +------------------------------------+  |
|  |      Vorschlage generieren         |  |
|  +------------------------------------+  |
+------------------------------------------+
```

### Verbesserter Entwurf

```
+------------------------------------------+
| WasAntwort              [Historie] [Gear]|
+------------------------------------------+
|                                          |
|  +------------------------------------+  |
|  | Nachricht                          |  |
|  | +--------------------------------+ |  |
|  | | Nachricht einfugen...   4/4000 | |  |
|  | |                                | |  |
|  | +--------------------------------+ |  |
|  | [Paste-Icon] Einfugen    [X] Leeren|  |  <- Icons mit Labels
|  +------------------------------------+  |
|                                          |
|  Schnellstile                            |
|  [*Freundlich*] [Kurz] [Ablehnen] [+]   |  <- Aktiver Chip hervorgehoben
|                                          |
|  +------------------------------------+  |
|  |  Tipp: Fuge eine WhatsApp-         |  |  <- Hilfe-Hinweis
|  |  Nachricht ein und lass dir        |  |
|  |  passende Antworten generieren.    |  |
|  +------------------------------------+  |
|                                          |
|  +------------------------------------+  |
|  |  [Sparkle] Vorschlage generieren   |  |  <- Icon im Button
|  +------------------------------------+  |
+------------------------------------------+
```

---

### UI-Aufgaben

#### [UI1] Eingabefeld verbessern

**Vorher:**
- Icons ohne Labels
- `ContentCopy` Icon fur Paste

**Nachher:**
- Icons mit Beschriftung: "[Paste-Icon] Einfugen" und "[X] Leeren"
- Korrektes `ContentPaste` Icon

**Aufgaben:**
- [ ] `ContentPaste` Icon verwenden
- [ ] Labels unter/neben Icons hinzufugen
- [ ] Besserer Placeholder-Text

---

#### [UI2] Schnellstile optimieren

**Vorher:**
- "Anpassen" wird abgeschnitten
- Aktiver Chip nur durch Rahmen erkennbar

**Nachher:**
- Aktiver Chip mit fullendem Hintergrund + Glow
- "+" Icon fur Anpassen (spart Platz)
- Scroll-Indikator falls mehr Chips

**Aufgaben:**
- [ ] Aktiven Chip mit `Accent1` Hintergrund fullen
- [ ] "Anpassen" durch "+" Icon ersetzen
- [ ] Horizontalen Scroll-Indikator hinzufugen

---

#### [UI3] Leeren Zustand nutzen

**Vorher:**
- Grosser leerer Bereich wenn keine Vorschlage

**Nachher:**
- Hilfe-Card mit Tipp fur neue Nutzer
- Beispiel-Nachricht zum Ausprobieren

**Aufgaben:**
- [ ] `EmptyStateCard` Komponente erstellen
- [ ] Tipp-Text anzeigen wenn `suggestions.isEmpty()`
- [ ] "Beispiel ausprobieren" Button

---

#### [UI4] Generate-Button aufwerten

**Vorher:**
- Nur Text im Button

**Nachher:**
- Icon (Sparkles/Magic Wand) + Text
- Subtile Animation beim Hover/Press
- Deaktiviert + ausgegraut wenn kein Text

**Aufgaben:**
- [ ] `Icons.Filled.AutoAwesome` oder ahnliches Icon hinzufugen
- [ ] Pulse-Animation beim Loading
- [ ] Klarere disabled-State Darstellung

---

#### [UI5] Vorschlage-Cards verbessern

**Aufgaben:**
- [ ] Nummerierung hinzufugen (1/5, 2/5, etc.)
- [ ] Swipe-Geste fur Kopieren
- [ ] Long-Press fur Kontextmenu (Kopieren, Teilen, Umschreiben)
- [ ] Umschreiben-Chips direkt unter jeder Card

---

#### [UI6] Visuelles Feedback

**Aufgaben:**
- [ ] Ripple-Effekt bei Chip-Auswahl
- [ ] Konfetti/Sparkle bei erfolgreichem Kopieren
- [ ] Shake-Animation bei Fehler (bereits vorhanden, beibehalten)

---

## Priorisierte Reihenfolge

### Phase 1: Sicherheit (Sofort)
1. [S1] API-Key entfernen
2. [S2] Verschlusselte Speicherung

### Phase 2: Core UX (1 Woche)
3. [U1] Paste-Icon korrigieren
4. [U4] Accessibility
5. [UI1] Eingabefeld verbessern
6. [UI3] Leeren Zustand nutzen

### Phase 3: Polish (2 Wochen)
7. [U2] Rewrite-Optionen
8. [UI2] Schnellstile optimieren
9. [UI4] Generate-Button
10. [U3] Undo fur Historie

### Phase 4: Optimierung (Ongoing)
11. [N4] Performance: Sterne
12. [N5] Performance: GlassCard
13. [N1] Strings externalisieren

---

## Checkliste fur Release

- [ ] Kein API-Key im Code
- [ ] Alle Accessibility-Labels vorhanden
- [ ] TalkBack-Test bestanden
- [ ] Performance auf alteren Geraten getestet
- [ ] Crash-Reporting eingerichtet
- [ ] ProGuard-Regeln getestet
