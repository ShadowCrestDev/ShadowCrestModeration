ShadowCrestModeration (SCM)

ShadowCrestModeration ist ein modernes, leistungsstarkes Moderations-Plugin für Paper & Spigot,
mit einem vollständig GUI-basierten Ticketsystem, privatem Ticket-Chat und internem Team-Chat.

Entwickelt mit Fokus auf Übersichtlichkeit, Stabilität und professionelle Moderations-Workflows.

🚀 Version

v1.2.1

Integriertes Release – enthält alle Änderungen seit v1.1.0
(v1.1.1 war eine interne Entwicklungsversion)

🔧 Kompatibilität

Minecraft: 1.20 – 1.21.x

Server: Paper / Spigot

Java: 21

✨ Features
🎫 Ticketsystem (GUI-first)

Komplett GUI-basiertes Ticketsystem

Schrittweise Ticket-Erstellung:

Kategorie → Zielspieler → Zusatzinfo

Cooldown & Limit für offene Tickets

Staff-Ticket-Übersicht mit Seiten

Ticket-Detail-GUI mit:

Claim / Unclaim

Teleport zum Ersteller

Schließen mit GUI-Auswahl (Gründe)

Ticket-Status:

OPEN, CLAIMED, CLOSED

Automatische Staff-Benachrichtigungen

💬 Privater Ticket-Chat

Privater Chat zwischen Supporter & Ticket-Ersteller

/t <message> zum Antworten

Toggle-Modus für Ticket-Chat

Automatisches Beenden beim Ticket-Close

Sichere Session-Verwaltung

⚡ Ticket Actions GUI

Moderationsaktionen direkt aus dem Ticket

Warn

Kick

Tempban (1h / 1d)

Permanenter Ban

Standard-Grund: Ticket

Saubere Trennung:

Ticket-Detail-GUI

Ticket-Actions-GUI

Vollständig über Language-Dateien konfigurierbar

🧑‍🤝‍🧑 Interner Team-Chat

Interner Chat nur für Staff

/teamchat – Toggle-Modus

/teamchat <msg> – Einmal-Nachricht

Eigenes Chat-Format

Unabhängig vom Ticket-Chat

Permission-basiert

🛠 Moderations-Commands

/warn, /warns, /clearwarns

/kick

/ban, /tempban

/unban

/ipban, /unipban

/ip

/playtime

📋 Join-Logs für Staff

Anzeige beim Join (nur für Staff):

Anzahl Warns

Letzte Verwarnungen

Spielzeit (optional)

Vollständig konfigurierbar

⚙ Konfiguration & Sprache

Vollständig konfigurierbar über:

config.yml

Language/de_DE.yml

Language/en_US.yml

Einheitlicher Prefix

Saubere Placeholder-Unterstützung

Keine Hardcoded-Texte

📦 Installation

Lade die neueste ShadowCrestModeration.jar aus den Releases herunter

Lege die Datei in den plugins/ Ordner

Starte den Server

Passe config.yml und die Language-Dateien an

Optional: /scm reload

📜 Commands
Command	Beschreibung
/ticket	Ticket-GUI öffnen
/t <msg>	Nachricht im Ticket-Chat
/teamchat [msg]	Interner Team-Chat
/scm gui	Staff-Ticket-GUI
/scm accept	Nächstes Ticket annehmen
/scm reload	Config & Language neu laden
/scm info	Plugin-Infos anzeigen
/warn	Spieler verwarnen
/kick	Spieler kicken
/ban	Spieler bannen
/tempban	Spieler temporär bannen
/unban	Bann aufheben
/ipban	IP bannen
/unipban	IP-Bann aufheben
/playtime	Spielzeit anzeigen
🔐 Permissions (Auszug)
Permission	Beschreibung
shadowcrest.mod.ticket.staff	Zugriff auf Staff-Ticket-GUIs
shadowcrest.mod.ticket.notify	Ticket-Benachrichtigungen
shadowcrest.mod.teamchat	Interner Team-Chat
shadowcrest.mod.warn	Warns vergeben
shadowcrest.mod.kick	Kick
shadowcrest.mod.ban	Ban
shadowcrest.mod.tempban	Tempban
shadowcrest.mod.reload	/scm reload
shadowcrest.mod.info	/scm info

(Alle Permissions standardmäßig op)

🧠 Technische Highlights

GUI-Erkennung über PersistentDataContainer

Keine Titel-Hacks

Saubere Listener-Trennung

Moderne Java-Struktur

Stabil & erweiterbar

📄 Lizenz

Dieses Projekt steht unter der Apache License 2.0.
Siehe LICENSE für weitere Informationen.

❤️ Credits

Entwickelt von ShadowCrest
Mit Fokus auf Qualität, Wartbarkeit & professionelle Moderation.
