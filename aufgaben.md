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

---
---

# GLASSMORPHISM-VERBESSERUNGEN

## Referenzbild-Analyse

Das Referenzbild zeigt einen deutlich verbesserten Glassmorphism-Effekt mit:

1. **Farbiger Cosmic-Hintergrund** - Violett/Lila/Pink Nebel statt dunklem Schwarz
2. **Deutlicher Glow-Effekt** - Rosa/Violett Schein um Hauptelemente
3. **Hellere Glasflachen** - Mehr "Frosted Glass" Look
4. **Starkere Borders** - Leuchtende Rander (Light Rim)
5. **Kompakteres Layout** - Chips innerhalb der Hauptkarte
6. **Bottom Sheet** - Heller, mehr Kontrast zum Hintergrund

---

## [GLASS1] Hintergrund verbessern

**Datei:** `ui/components/CosmicBackground.kt`

### Aktuell:
```kotlin
val GlassBackground = Color(0xFF0A0A0F)  // Fast schwarz
```

### Ziel (wie Referenzbild):
- Tiefvioletter Basis-Gradient
- Mehrere farbige Nebel-Orbs (Pink, Violett, Blau)
- Mehr Sattigung und Tiefe

### Aufgaben:
- [ ] Basis-Hintergrundfarbe auf `#0D0B1E` (Tiefviolett) andern
- [ ] Mehr farbige Orbs hinzufugen:
  ```kotlin
  // Orb: Pink/Magenta (oben rechts)
  Brush.radialGradient(
      center = Offset(screenWidth * 0.8f, screenHeight * 0.1f),
      radius = 600f,
      colors = listOf(
          Color(0xFFE040FB).copy(alpha = 0.25f),  // Pink
          Color.Transparent
      )
  )

  // Orb: Violett (mitte links)
  Brush.radialGradient(
      center = Offset(screenWidth * 0.1f, screenHeight * 0.4f),
      radius = 800f,
      colors = listOf(
          Color(0xFF7C4DFF).copy(alpha = 0.20f),  // Violett
          Color.Transparent
      )
  )

  // Orb: Blau (unten)
  Brush.radialGradient(
      center = Offset(screenWidth * 0.5f, screenHeight * 0.9f),
      radius = 700f,
      colors = listOf(
          Color(0xFF448AFF).copy(alpha = 0.18f),  // Blau
          Color.Transparent
      )
  )
  ```
- [ ] Sterne-Farbe leicht violett tonen (`Color(0xFFE8E0FF)`)

---

## [GLASS2] Glow-Effekt fur Hauptkarte

**Datei:** `ui/components/Glass.kt`

### Aktuell:
```kotlin
.shadow(
    elevation = 8.dp,
    spotColor = Color.Black.copy(alpha = 0.2f),
    ...
)
```

### Ziel:
- Farbiger Glow statt schwarzem Schatten
- Violett/Pink Schein um die Karte

### Aufgaben:
- [ ] Neue `GlowCard` Komponente erstellen oder `GlassCard` erweitern
- [ ] Farbigen Shadow implementieren:
  ```kotlin
  // Outer Glow
  Box(
      modifier = Modifier
          .matchParentSize()
          .offset(y = 4.dp)
          .blur(radius = 24.dp)
          .background(
              brush = Brush.radialGradient(
                  colors = listOf(
                      Color(0xFF7C4DFF).copy(alpha = 0.4f),
                      Color(0xFFE040FB).copy(alpha = 0.2f),
                      Color.Transparent
                  )
              )
          )
  )
  ```
- [ ] Glow-Intensitat als Parameter (`glowIntensity: Float = 0.4f`)
- [ ] Glow-Farbe als Parameter (`glowColor: Color = Accent1`)

---

## [GLASS3] Hellere Glass-Oberflache

**Datei:** `ui/theme/Color.kt`

### Aktuell:
```kotlin
val GlassSurfaceAlpha = 0.10f
val GlassGradientLight = Color(0xFF303045).copy(alpha = 0.18f)
```

### Ziel:
- Hellere, mehr "milchige" Glasoberflache
- Besserer Kontrast zum Hintergrund

### Aufgaben:
- [ ] Alpha-Werte erhohen:
  ```kotlin
  val GlassSurfaceAlpha = 0.15f  // War: 0.10f
  val GlassSurfacePressedAlpha = 0.22f  // War: 0.16f
  ```
- [ ] Helleren Gradient:
  ```kotlin
  val GlassGradientLight = Color(0xFF4A4A6A).copy(alpha = 0.25f)
  val GlassGradientDark = Color(0xFF1A1A2E).copy(alpha = 0.15f)
  ```
- [ ] Mehr Weiss im Highlight:
  ```kotlin
  val GlassHighlight = Color(0xFFFFFFFF).copy(alpha = 0.12f)  // War: 0.08f
  val GlassSheen = Color(0xFFFFFFFF).copy(alpha = 0.10f)      // War: 0.06f
  ```

---

## [GLASS4] Leuchtende Borders (Light Rim)

**Datei:** `ui/components/Glass.kt`

### Aktuell:
```kotlin
val GlassLightRim = Color(0xFFFFFFFF).copy(alpha = 0.18f)

.border(
    width = 1.dp,
    color = GlassLightRim,
    ...
)
```

### Ziel:
- Starkere, leuchtendere Rander
- Gradient-Border fur mehr Tiefe

### Aufgaben:
- [ ] Light Rim starker machen:
  ```kotlin
  val GlassLightRim = Color(0xFFFFFFFF).copy(alpha = 0.28f)  // War: 0.18f
  ```
- [ ] Gradient-Border implementieren:
  ```kotlin
  .drawBehind {
      val borderGradient = Brush.linearGradient(
          colors = listOf(
              Color.White.copy(alpha = 0.4f),  // Oben: hell
              Color.White.copy(alpha = 0.1f),  // Mitte: schwach
              Color.White.copy(alpha = 0.25f)  // Unten: mittel
          ),
          start = Offset(0f, 0f),
          end = Offset(0f, size.height)
      )
      drawRoundRect(
          brush = borderGradient,
          style = Stroke(width = 1.5.dp.toPx()),
          cornerRadius = CornerRadius(cornerRadius.toPx())
      )
  }
  ```
- [ ] Inner-Highlight am oberen Rand:
  ```kotlin
  // Helle Linie oben (wie Lichtreflex)
  Box(
      modifier = Modifier
          .fillMaxWidth()
          .height(1.dp)
          .background(
              brush = Brush.horizontalGradient(
                  colors = listOf(
                      Color.Transparent,
                      Color.White.copy(alpha = 0.5f),
                      Color.Transparent
                  )
              )
          )
          .align(Alignment.TopCenter)
  )
  ```

---

## [GLASS5] Button mit Glow-Effekt

**Datei:** `ui/components/Glass.kt` - `GlassButton`

### Aktuell:
```kotlin
Button(
    colors = ButtonDefaults.buttonColors(
        containerColor = Accent1.copy(alpha = 0.92f),
        ...
    ),
    ...
)
```

### Ziel:
- Leuchtender Button wie im Referenzbild
- Pulsierender Glow beim Loading

### Aufgaben:
- [ ] Glow unter dem Button:
  ```kotlin
  Box {
      // Glow Layer
      Box(
          modifier = Modifier
              .matchParentSize()
              .offset(y = 6.dp)
              .blur(radius = 16.dp)
              .background(
                  color = Accent1.copy(alpha = 0.5f),
                  shape = RoundedCornerShape(cornerRadius)
              )
      )
      // Actual Button
      Button(...) { ... }
  }
  ```
- [ ] Pulsierender Glow beim Loading:
  ```kotlin
  val glowAlpha by animateFloatAsState(
      targetValue = if (isLoading) 0.7f else 0.5f,
      animationSpec = infiniteRepeatable(
          animation = tween(1000),
          repeatMode = RepeatMode.Reverse
      )
  )
  ```

---

## [GLASS6] Kompakteres Layout (wie Referenz)

**Datei:** `ui/MainScreen.kt`

### Aktuell:
- Schnellstile ausserhalb der Karte
- Viel Leerraum

### Ziel (wie Referenzbild):
- Schnellstile innerhalb der Eingabe-Karte
- Button naher am Content

### Aufgaben:
- [ ] Layout umstrukturieren:
  ```kotlin
  GlassCard {
      Column {
          // Eingabefeld
          OutlinedTextField(...)

          Spacer(modifier = Modifier.height(12.dp))

          // Schnellstile INNERHALB der Karte
          Row(
              horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
              GlassChip("Freundlich", selected = true, ...)
              GlassChip("Kurz & klar", ...)
              GlassChip("Hoeflich ablehnen", ...)
              GlassChip("+", onClick = { showCustomize = true })
          }
      }
  }

  Spacer(modifier = Modifier.height(16.dp))

  // Button direkt darunter
  GlassButton("Vorschlage generieren", ...)
  ```
- [ ] Weniger vertikale Abstande

---

## [GLASS7] Bottom Sheet verbessern

**Datei:** `ui/components/StyleCustomizationBottomSheet.kt`

### Aktuell:
- Dunkle Glass-Oberflache wie Hauptkarte

### Ziel (wie Referenzbild):
- Hellerer "Frosted Glass" Look
- Klare Sektion-Trennung

### Aufgaben:
- [ ] Hellere Surface fur Bottom Sheet:
  ```kotlin
  val BottomSheetSurface = Color(0xFF2A2A3E).copy(alpha = 0.95f)
  ```
- [ ] Sektion-Header mit leichtem Hintergrund:
  ```kotlin
  Text(
      text = "Ziel",
      modifier = Modifier
          .fillMaxWidth()
          .background(Color.White.copy(alpha = 0.05f))
          .padding(horizontal = 16.dp, vertical = 8.dp)
  )
  ```
- [ ] Chip-Reihen mit mehr Abstand

---

## [GLASS8] Chip-Design verbessern

**Datei:** `ui/components/Glass.kt` - `GlassChip`

### Aktuell:
```kotlin
val backgroundColor = if (selected) {
    Accent1.copy(alpha = 0.3f)
} else {
    GlassSurfaceBase
}
```

### Ziel:
- Aktiverer Chip starker hervorgehoben
- Icon-Support (Kalender-Icon bei "Freundlich")

### Aufgaben:
- [ ] Starkere Hervorhebung fur ausgewahlten Chip:
  ```kotlin
  val backgroundColor = if (selected) {
      Accent1.copy(alpha = 0.6f)  // War: 0.3f
  } else {
      GlassSurfaceBase.copy(alpha = 0.4f)
  }

  val borderColor = if (selected) {
      Accent1.copy(alpha = 0.8f)
  } else {
      GlassLightRim.copy(alpha = 0.15f)
  }
  ```
- [ ] Glow fur ausgewahlten Chip:
  ```kotlin
  if (selected) {
      Box(
          modifier = Modifier
              .matchParentSize()
              .blur(8.dp)
              .background(Accent1.copy(alpha = 0.3f))
      )
  }
  ```
- [ ] Optional: Icon-Parameter hinzufugen:
  ```kotlin
  @Composable
  fun GlassChip(
      text: String,
      selected: Boolean,
      onClick: () -> Unit,
      icon: ImageVector? = null,  // NEU
      ...
  )
  ```

---

## Farbpalette-Erweiterung

**Datei:** `ui/theme/Color.kt`

### Neue Farben hinzufugen:

```kotlin
// Glow Colors
val GlowPrimary = Color(0xFF7C4DFF)      // Violett
val GlowSecondary = Color(0xFFE040FB)    // Pink/Magenta
val GlowTertiary = Color(0xFF448AFF)     // Blau

// Background Nebula Colors
val NebulaPink = Color(0xFFE040FB)
val NebulaViolet = Color(0xFF7C4DFF)
val NebulaBlue = Color(0xFF448AFF)
val NebulaTeal = Color(0xFF00BFA5)

// Enhanced Glass Colors
val GlassSurfaceLight = Color(0xFF3A3A5A).copy(alpha = 0.20f)
val GlassBorderGlow = Color(0xFFFFFFFF).copy(alpha = 0.35f)
```

---

## Visuelle Vergleich

### Vorher (Aktuell):
```
+------------------------------------------+
|  [Dunkler Hintergrund, kaum Farbe]       |
|                                          |
|  +----------------------------------+    |
|  | [Schwache Glass-Karte]           |    |
|  | [Dunner Border]                  |    |
|  +----------------------------------+    |
|                                          |
|  [Schnellstile ausserhalb]               |
|                                          |
|  [Button ohne Glow]                      |
+------------------------------------------+
```

### Nachher (Referenz-Design):
```
+------------------------------------------+
|  [Farbiger Nebel-Hintergrund]            |
|  [Violett/Pink/Blau Orbs]                |
|                                          |
|  +================================+      |
|  ||  [GLOW-EFFEKT]                ||     |
|  ||+------------------------------+||    |
|  ||| [Helle Glass-Karte]          |||    |
|  ||| [Leuchtende Borders]         |||    |
|  ||| [Schnellstile INNEN]         |||    |
|  ||+------------------------------+||    |
|  +================================+      |
|                                          |
|  +~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~+     |
|  |    [Button mit Glow]            |     |
|  +~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~+     |
+------------------------------------------+
```

---

## Priorisierte Glassmorphism-Aufgaben

### Phase 1: Quick Wins
1. [ ] [GLASS3] Alpha-Werte erhohen (5 Min)
2. [ ] [GLASS4] Light Rim starker (5 Min)
3. [ ] [GLASS8] Chip-Hervorhebung (10 Min)

### Phase 2: Hintergrund
4. [ ] [GLASS1] Farbige Orbs hinzufugen (30 Min)
5. [ ] [GLASS1] Sterne-Farbe anpassen (5 Min)

### Phase 3: Glow-Effekte
6. [ ] [GLASS2] Glow fur Hauptkarte (45 Min)
7. [ ] [GLASS5] Button-Glow (30 Min)

### Phase 4: Layout
8. [ ] [GLASS6] Kompakteres Layout (1 Std)
9. [ ] [GLASS7] Bottom Sheet (30 Min)

---

## Checkliste fur Release

- [ ] Kein API-Key im Code
- [ ] Alle Accessibility-Labels vorhanden
- [ ] TalkBack-Test bestanden
- [ ] Performance auf alteren Geraten getestet
- [ ] Crash-Reporting eingerichtet
- [ ] ProGuard-Regeln getestet
- [ ] Glassmorphism-Effekte auf verschiedenen Geraten getestet
- [ ] Glow-Performance auf Low-End-Geraten gepruft
