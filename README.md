# ShadowCrestModeration (SCM)

ShadowCrestModeration ist ein modernes, leistungsstarkes Moderations-Plugin für Paper & Spigot,
mit einem vollständig GUI-basierten Ticketsystem, privatem Ticket-Chat und internem Team-Chat.

Entwickelt mit Fokus auf Übersichtlichkeit, Stabilität und professionelle Moderations-Workflows.

## 🚀 Version

v1.2.1
Integriertes Release – enthält alle Änderungen seit v1.1.0
(v1.1.1 war eine interne Entwicklungsversion)

## 🔧 Kompatibilität

Minecraft: 1.20 – 1.21.x

Server: Paper / Spigot

Java: 21

## ✨ Features

---
### 🎫 Ticketsystem (GUI-first)

Komplett GUI-basiertes Ticketsystem

Schrittweise Ticket-Erstellung
→ Kategorie → Zielspieler → Zusatzinfo

Cooldown & Limit für offene Tickets

Staff-Ticket-Übersicht mit Seiten

Ticket-Detail-GUI mit:

Claim / Unclaim

Teleport zum Ersteller

Schließen über GUI (vordefinierte Gründe)

Ticket-Status: OPEN, CLAIMED, CLOSED

Automatische Staff-Benachrichtigungen

---
### 💬 Privater Ticket-Chat

Privater Chat zwischen Supporter & Ticket-Ersteller

/t <message> zum Antworten

Toggle-Modus für Ticket-Chat

Automatisches Beenden beim Ticket-Close

Sichere Session-Verwaltung

---
### ⚡ Ticket Actions GUI

Moderationsaktionen direkt aus dem Ticket:

Warn

Kick

Tempban (1h / 1d)

Permanenter Ban

Standard-Grund: Ticket

Saubere Trennung:

Ticket-Detail-GUI

Ticket-Actions-GUI

Vollständig sprachabhängig (de_DE / en_US)

---
### 🧑‍🤝‍🧑 Interner Team-Chat

Interner Chat nur für Staff

/teamchat → Toggle-Modus

/teamchat <msg> → Einmal-Nachricht

Eigenes Chat-Format

Unabhängig vom Ticket-Chat

Permission-basiert

---
🛠 Moderations-Commands

Warn-System mit Auto-Strafen

Kick / Ban / Tempban

IP-Ban & IP-Unban

Unban

Playtime-Anzeige

---
### 📋 Join-Logs für Staff

Anzeige beim Join (nur für Staff):

Warn-Anzahl

Letzte Verwarnungen

Spielzeit (optional)

Vollständig konfigurierbar

---
### ⚙ Konfiguration & Sprache

Vollständig konfigurierbar über:

config.yml

Language/de_DE.yml

Language/en_US.yml

Einheitlicher Prefix

Platzhalter-Support

Keine Hardcoded-Texte

---
### 📦 Installation

Neueste ShadowCrestModeration.jar aus den Releases herunterladen

In den Ordner plugins/ legen

Server starten

config.yml & Language-Dateien anpassen

Optional: /scm reload

---
### 📜 Commands

Spieler

/ticket – Ticket-GUI öffnen

/t <msg> – Nachricht im Ticket-Chat

/teamchat <msg> – Interner Team-Chat

Staff

/scm gui – Staff-Ticket-GUI

/scm reload – Config & Sprache neu laden

/scm info – Plugin-Infos

Moderation

/warn, /warns, /clearwarns

/kick

/ban, /tempban

/unban

/ipban, /unipban

/ip

/playtime

---
### 🔐 Permissions (Auszug)

shadowcrest.mod.ticket.staff – Zugriff auf Staff-Ticket-GUIs

shadowcrest.mod.ticket.notify – Ticket-Benachrichtigungen

shadowcrest.mod.teamchat – Interner Team-Chat

shadowcrest.mod.warn - Warne einen Spieler

shadowcrest.mod.kick - Kicke einen Spieler

shadowcrest.mod.ban - Banne einen Spieler

shadowcrest.mod.tempban - Spieler für eine gewisse Zeit Bannen

shadowcrest.mod.reload - Reload Config

shadowcrest.mod.info - Plugin infos

(Alle Permissions standardmäßig op)

---
### 🧠 Technische Highlights

GUI-Erkennung über PersistentDataContainer

Keine Titel-Erkennung

Saubere Listener-Trennung

Moderne Java-Struktur

Stabil & erweiterbar

---
### 📄 Lizenz

Apache License 2.0
Siehe LICENSE für Details.

---
### ❤️ Credits

Entwickelt von ShadowCrest
Fokus auf Qualität, Wartbarkeit & professionelle Moderation.
