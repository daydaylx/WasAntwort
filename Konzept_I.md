Konzept: „ReplyHelper“ (APK, privat)
1) Ziel & Problem

Problem: Deine Freundin antwortet spät oder gar nicht, weil sie in der Entscheidung hängt (“Was soll ich schreiben?”), nicht weil sie nicht tippen kann.
Ziel: In unter 20 Sekunden von “Nachricht erhalten” zu “Antwort kopiert” kommen, ohne Nachdenken, ohne Rumformulieren.

Erfolgskriterium (hart):

3 Schritte: Einfügen → Vorschläge → Kopieren

Unter 10 Sekunden bis zur ersten brauchbaren Antwort (bei normalem Netz).

2) Kernfunktionen (MVP)
2.1 Input

Großes Textfeld: „Nachricht hier einfügen“

Button „Einfügen“ (aus Zwischenablage)

Button „Löschen“ (Textfeld leeren)

2.2 Steuerung per Chips (kein Einstellungszoo)

Ton (1 Auswahl):

Freundlich

Neutral

Kurz

Herzlich

Bestimmt

Flirty

Ziel (1 Auswahl):

Zusagen

Absagen

Verschieben

Nachfragen

Bedanken

Abgrenzen (höflich, aber klar)

Länge (1 Auswahl):

1 Satz

Kurz

Normal

Emojis (1 Auswahl):

Aus

Wenig

Normal

Optional (MVP-Plus, aber lohnt sich):

Du/Sie Toggle (Standard: Du)

2.3 Generierung

Primärbutton: „Vorschläge generieren“

Output: genau 5 Antwortvorschläge als Karten

2.4 Ausgabe-Interaktion

Tap auf Karte: Antwort in Clipboard

Kleine Buttons pro Karte (Rewrites):

„Kürzer“

„Freundlicher“

„Direkter“

„Ohne Emojis“

„Mit Rückfrage“

Das sind keine “KI-Features”, das sind Entscheidungsabkürzungen.

3) UX-Flow (minimale Reibung)
Hauptscreen (Single Screen App)

App öffnen

„Einfügen“ tippen (oder Text einfügen)

Ton/Ziel/Länge/Emojis (Defaults sind schon gesetzt)

„Vorschläge generieren“

Vorschlag antippen → kopiert

Zurück zu WhatsApp und einfügen

Wichtig: Kein Multi-Screen-Gelaufe. Keine Chat-Historie als Standard. Keine “Konversationen”. Das killt die Speed.

4) Prompting-Design (damit die KI nicht rumlabert)

Du brauchst zwei Prompt-Typen:

Generate (aus Nachricht → 5 Vorschläge)

Rewrite (aus ausgewähltem Vorschlag → Variation)

4.1 Systemprompt (Generate)

Ziele:

Keine Erklärungen

Keine erfundenen Details

Immer 5 Antworten

Wenn Infos fehlen: mindestens eine Rückfrage

Regeln:

Sprache: Deutsch

Output: Liste mit 5 Antworten (ohne Nummern geht auch, aber nummeriert ist einfacher zu parsen)

Kein Kontext-Erfinden (“Dann sehen wir uns um 18 Uhr”), wenn es nicht im Text steckt

Wenn unklar: eine Rückfrage-Antwort anbieten

4.2 Userprompt (Generate)

Enthält:

Originalnachricht (copy-paste)

Ton

Ziel

Länge

Emoji-Level

Du/Sie

4.3 Rewrite-Prompt

Input:

Originalnachricht (optional)

Gewählte Antwort

Rewrite-Anweisung (“kürzer”, “direkter” usw.)

Output:

1 neue Variante

Warum extra Rewrite statt neu generieren?
Weil es schneller wirkt, weniger Token kostet, und die Nutzerin den “Anker” behält.

5) Technische Architektur (schlank, aber nicht dumm)
5.1 Plattform

Android nativer Build

Empfehlung: Kotlin + Jetpack Compose
Grund: beste UX, schnell implementierbar, keine Framework-Altlasten.

5.2 Module/Schichten

UI (Compose)

InputArea

OptionChips

GenerateButton

SuggestionsList

Toast/Snackbar Feedback (“Kopiert”)

Domain

Models: Tone, Goal, Length, EmojiLevel, Formality

PromptBuilder: baut Strings

Data

AIClient: HTTP Calls

SettingsStore: API Key + Defaults (DataStore)

(Optional) LocalCache: letzte Antworten im RAM (nicht persistent)

5.3 Datenhaltung (minimal)

Persistente Settings:

API Key

Default-Ton/Ziel/Länge/Emojis

Keine Speicherung der eingefügten Nachricht (Standard)

Optional: “Antwort-Favoriten” (nur die Antworttexte, nicht der Originalchat)

6) API & Provider (pragmatisch)

Da du privat bleibst:

Du kannst OpenRouter oder einen Anbieter deiner Wahl nutzen.

App braucht:

Endpoint

Model-ID

API-Key

Timeout/Retry (sonst wirkt’s “kaputt”)

Minimal-Error-Handling:

Netzwerk weg → klare Meldung („Kein Internet“)

API Key falsch → „API-Key prüfen“

Rate limit → „Kurz warten“

7) UI-Design (damit es nicht nach Bastel-App aussieht)
Stil

Clean Dark Theme

Große Buttons (thumb-friendly)

Chips mit klarer Active-State-Markierung

Vorschläge als Cards, gut lesbar, Copy-Icon

Micro-Feedback

Nach Copy: Snackbar “Kopiert”

Während Generate: Loading (Spinner) + Button disabled

Bei Fehler: rote Snackbar + kurzer Klartext

8) Edge Cases (die du einplanen musst, sonst nervt’s)

Nachricht ist super kurz („Ok“)
→ Vorschläge trotzdem liefern (Bestätigung, Nachfrage, Abschluss)

Nachricht ist super lang
→ Hard limit (z.B. 2.000–4.000 Zeichen) + Hinweis “zu lang, kürze”

Inhalt unklar
→ mindestens eine Rückfrage-Antwort

“Arbeit vs privat”
→ Du/Sie Toggle + “professionell” Ton optional

9) Roadmap (kurz, realistisch)
Phase 1 (MVP)

Single Screen UI

Clipboard Paste

Options (Ton/Ziel/Länge/Emojis)

Generate → 5 Vorschläge

Copy on tap

Settings: API Key + Defaults

Phase 2 (Komfort)

Rewrite Buttons pro Vorschlag

Favoriten

“Du/Sie” + “Arbeit/Privat” preset

Phase 3 (Luxus)

Share-Sheet Integration (statt manuell einfügen)

Kontaktprofile (lokal)

Verlauf (optional, verschlüsselt)

10) Warum dieses Konzept funktioniert

Es löst das echte Problem: Entscheidung, nicht Tippen.

Es ist schneller als selbst formulieren.

Es ist nicht überladen.

Es ist robust, weil Copy-Paste immer geht.
