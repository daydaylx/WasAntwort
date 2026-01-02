# WasAntwort - Sicherheits- und Qualitatsanalyse

**Datum:** 2026-01-02
**Analysiert von:** Claude Code

---

## Zusammenfassung

Die Analyse des WasAntwort-Projekts hat **1 kritisches**, **3 mittlere** und **5 niedrige** Probleme identifiziert.

| Schweregrad | Anzahl |
|-------------|--------|
| KRITISCH    | 1      |
| MITTEL      | 3      |
| NIEDRIG     | 5      |

---

## KRITISCHE Probleme

### 1. Hardcodierter API-Schlussel im Quellcode

**Datei:** `app/src/main/java/de/grunert/wasantwort/domain/Models.kt:55`

```kotlin
private const val DEFAULT_API_KEY = "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"
```

**Problem:**
- Ein OpenRouter API-Schlussel ist direkt im Quellcode eingebettet
- Dieser Schlussel wird bei JEDEM Clone des Repositories offengelegt
- Der Schlussel wird in der kompilierten APK enthalten sein und kann leicht extrahiert werden
- Angreifer konnen diesen Schlussel missbrauchen, was zu:
  - Finanziellen Kosten fuhrt
  - Rate Limiting fur legitime Nutzer
  - Moglicher Account-Sperrung

**Empfehlung:**
- API-Schlussel SOFORT widerrufen
- API-Schlussel uber Umgebungsvariablen oder zur Laufzeit konfigurieren
- BuildConfig-Felder mit `local.properties` verwenden (nicht in Git committed)
- Fur Free-Modelle: Nutzer zur Eingabe eines eigenen Schlussels auffordern

---

## MITTLERE Probleme

### 2. API-Schlussel in DataStore ohne Verschlusselung

**Datei:** `app/src/main/java/de/grunert/wasantwort/data/SettingsStore.kt:24`

```kotlin
val API_KEY = stringPreferencesKey("api_key")
```

**Problem:**
- API-Schlussel werden unverschlusselt in DataStore gespeichert
- Auf gerooteten Geraten kann dies ausgelesen werden
- Backup-Extraktion kann den Schlussel offenlegen

**Empfehlung:**
- EncryptedSharedPreferences oder AndroidX Security-Crypto verwenden
- `EncryptedDataStore` implementieren

---

### 3. Fehlende SSL/TLS Certificate Pinning

**Datei:** `app/src/main/java/de/grunert/wasantwort/data/AiClient.kt`

**Problem:**
- Keine Certificate-Pinning-Konfiguration
- Man-in-the-Middle-Angriffe moglich
- API-Schlussel konnten abgefangen werden

**Empfehlung:**
- Certificate Pinning fur die OpenRouter API implementieren
- Network Security Config in `res/xml/network_security_config.xml` hinzufugen

---

### 4. Keine Input-Validierung bei der URL

**Datei:** `app/src/main/java/de/grunert/wasantwort/ui/SettingsScreen.kt:449`

```kotlin
!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")
```

**Problem:**
- Minimale URL-Validierung
- `http://` URLs werden akzeptiert (unverschlusselt)
- Keine Validierung des Hostnamens
- Potenzielle SSRF-Schwachstelle, wenn ein Angreifer den Benutzer dazu bringt, eine boswillige URL einzugeben

**Empfehlung:**
- Nur `https://` erlauben
- URL-Parsing mit `java.net.URL` validieren
- Whitelist fur bekannte Provider implementieren

---

## NIEDRIGE Probleme

### 5. Thread-Safety bei cachedClient

**Datei:** `app/src/main/java/de/grunert/wasantwort/data/Repository.kt:17-31`

```kotlin
private var cachedClient: Pair<String, AiClient>? = null

private fun getClient(baseUrl: String, apiKey: String): AiClient {
    val key = "$baseUrl::$apiKey"
    val cached = cachedClient
    ...
}
```

**Problem:**
- `cachedClient` ist nicht thread-safe
- Bei gleichzeitigen Coroutines konnte es zu Race Conditions kommen
- Mogliche Memory Leaks durch nicht geschlossene Clients

**Empfehlung:**
- `Mutex` oder `synchronized` verwenden
- Oder `AtomicReference<Pair<String, AiClient>>` nutzen

---

### 6. Keine Begrenzung der History-Eintrage in der UI

**Datei:** `app/src/main/java/de/grunert/wasantwort/data/HistoryStore.kt:30`

```kotlin
private const val MAX_HISTORY_ENTRIES = 100
```

**Problem:**
- 100 Eintrage mit jeweils 5 Vorschlagen konnen viel Speicher belegen
- Keine Lazy Loading der Historie
- Bei grossen JSON-Strings kann die App langsam werden

**Empfehlung:**
- Pagination implementieren
- Altere Eintrage komprimieren oder loschen
- Lazy Loading in der HistoryScreen

---

### 7. Potenzielle ReDoS in Heuristik-Parser

**Datei:** `app/src/main/java/de/grunert/wasantwort/domain/ParseSuggestions.kt:98`

```kotlin
val arrayMatch = Regex("""\[(.*)\]""", RegexOption.DOT_MATCHES_ALL).find(cleaned)
```

**Problem:**
- Greedy Regex mit `DOT_MATCHES_ALL` auf unbegrenzte Eingabe
- Bei sehr grossen/boswilligen API-Antworten konnte dies langsam sein

**Empfehlung:**
- Input-Lange begrenzen bevor Regex ausgefuhrt wird
- Nicht-greedy Matching `\[.*?\]` verwenden

---

### 8. Fehlende Timeout-Konfiguration fur DataStore

**Dateien:** `SettingsStore.kt`, `HistoryStore.kt`

**Problem:**
- DataStore-Operationen haben keine expliziten Timeouts
- Bei I/O-Problemen konnte die App hangen

**Empfehlung:**
- `withTimeout` um DataStore-Operationen verwenden
- Fallback-Werte bei Timeout

---

### 9. Logging im Release-Build

**Datei:** `app/src/main/java/de/grunert/wasantwort/data/AiClient.kt:43-51`

```kotlin
install(Logging) {
    logger = object : Logger {
        override fun log(message: String) {
            // Logging disabled to avoid logging sensitive data
        }
    }
    level = LogLevel.NONE
}
```

**Problem:**
- Logging ist korrekt deaktiviert, aber das Plugin ist trotzdem installiert
- Unnotiger Overhead im Release-Build

**Empfehlung:**
- Logging-Plugin nur im Debug-Build installieren
- `BuildConfig.DEBUG` prufen

---

## Architektur-Hinweise

### Positive Aspekte

1. **MVVM-Pattern** korrekt implementiert
2. **StateFlow** fur reaktive UI-Updates
3. **Result<T>** fur funktionale Fehlerbehandlung
4. **Gute Testabdeckung** in Domain-Layer
5. **ProGuard-Regeln** korrekt konfiguriert

### Verbesserungspotenzial

1. **Repository-Singleton**: AppContainer erstellt Repository bei jedem Activity-Start neu
   - Besser: Application-Klasse mit Lazy Initialization

2. **Keine Offline-Unterstutzung**: App funktioniert nicht ohne Internet
   - Vorschlage cachen und Retry-Queue implementieren

3. **Keine Analytics/Crash-Reporting**: Schwer, Probleme in Produktion zu erkennen
   - Firebase Crashlytics oder ahnliches integrieren

---

## Abhangigkeiten-Status

| Abhangigkeit | Version | Aktuell? |
|--------------|---------|----------|
| Kotlin | 1.9.20 | Ja |
| Compose BOM | 2024.02.00 | Ja |
| Ktor | 2.3.6 | Ja |
| kotlinx-serialization | 1.6.0 | Ja |
| DataStore | 1.0.0 | Etwas alt |

---

## Empfohlene Massnahmen

### Sofort (Kritisch)
1. [ ] API-Schlussel aus Code entfernen und widerrufen
2. [ ] API-Schlussel uber sichere Konfiguration bereitstellen

### Kurzfristig (1-2 Wochen)
3. [ ] EncryptedDataStore fur API-Schlussel implementieren
4. [ ] SSL Certificate Pinning hinzufugen
5. [ ] URL-Validierung verbessern

### Mittelfristig (1 Monat)
6. [ ] Thread-Safety im Repository verbessern
7. [ ] History-Pagination implementieren
8. [ ] Crash-Reporting integrieren

---

## Fazit

Das Projekt ist fur ein MVP gut strukturiert, hat aber ein **kritisches Sicherheitsproblem** (hardcodierter API-Schlussel), das sofort behoben werden muss. Die mittleren Probleme sollten vor einem offentlichen Release adressiert werden.
