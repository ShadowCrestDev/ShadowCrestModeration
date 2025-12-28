# ShadowCrestModeration (SCM)

**ShadowCrestModeration** ist ein modernes, leichtgewichtiges Moderations-Plugin für Spigot & Paper.  
Es bietet ein flexibles Warnsystem mit Auto-Strafen, klassische Moderations-Commands, Join-Logs für Staff sowie optionale Playtime-Übersicht.

Entwickelt mit Fokus auf **Übersichtlichkeit, Performance und Konfigurierbarkeit**.

Kompatibilität

Minecraft: 1.19 – 1.21.4+

Server: Paper, Spigot
(Bukkit-kompatibel über Spigot-API)

---

## ✨ Features

- 🔔 **Warn-System**
  - /warn mit Pflicht-Grund
  - Warn-Historie pro Spieler
  - Automatische Strafen (Tempban / Ban) ab definierter Warn-Anzahl

- 🛠 **Moderations-Commands**
  - Kick, Ban, Tempban
  - IP-Ban & IP-Unban
  - Unban, ClearWarns, Warns-Übersicht

- 📋 **Join-Logs für Moderation**
  - Anzeige von Warn-Anzahl & letzter Warns beim Join
  - Optional: Anzeige der Spielzeit
  - Nur sichtbar für Staff (Permission-basiert)

- ⏱ **Playtime**
  - Anzeige der Spielzeit (Tage / Stunden / Minuten)
  - In Join-Logs integrierbar
  - Auch per Command abrufbar

- ⚙ **Vollständig konfigurierbar**
  - Alle Nachrichten über `config.yml`
  - Prefix, Texte, Auto-Strafen, Anzeigeoptionen

- 🧩 **Sauber & kompatibel**
  - Paper & Spigot
  - Moderne Adventure-Components für Kicks
  - Konsolen-Support für alle Commands

---

## 📦 Installation

1. Lade die neueste `.jar` aus den Releases herunter
2. Lege sie in den `plugins/` Ordner deines Servers
3. Starte den Server
4. Konfiguriere das Plugin in `config.yml`
5. Optional: `/scm reload`

---

## 📜 Commands

| Command | Beschreibung |
|------|-------------|
| `/warn <Spieler> <Grund>` | Verwarnung vergeben |
| `/warns <Spieler>` | Verwarnungen anzeigen |
| `/clearwarns <Spieler>` | Alle Warns löschen |
| `/kick <Spieler> <Grund>` | Spieler kicken |
| `/ban <Spieler> <Grund>` | Permanenter Bann |
| `/tempban <Spieler> <Zeit> <Grund>` | Zeitlich begrenzter Bann |
| `/unban <Spieler>` | Bann aufheben |
| `/ipban <Spieler/IP> <Grund>` | IP bannen |
| `/unipban <IP>` | IP-Bann aufheben |
| `/playtime <Spieler>` | Spielzeit anzeigen |
| `/scm reload` | Config neu laden |
| `/scm info` | Plugin-Infos anzeigen |

---

## 🔐 Permissions

| Permission | Beschreibung |
|-----------|--------------|
| `shadowcrest.mod.warn` | /warn |
| `shadowcrest.mod.warns` | /warns |
| `shadowcrest.mod.clearwarns` | /clearwarns |
| `shadowcrest.mod.kick` | /kick |
| `shadowcrest.mod.ban` | /ban |
| `shadowcrest.mod.tempban` | /tempban |
| `shadowcrest.mod.ipban` | /ipban |
| `shadowcrest.mod.unban` | /unban |
| `shadowcrest.mod.unipban` | /unipban |
| `shadowcrest.mod.playtime` | /playtime |
| `shadowcrest.mod.notify` | Join-Logs & Staff-Logs |
| `shadowcrest.mod.reload` | /scm reload |
| `shadowcrest.mod.info` | /scm info |

---

## 🧠 Auto-Strafen (Warn-Settings)

Beispiel aus der `config.yml`:

```yml
warn_settings:
  actions:
    - warns: 3
      action: TEMPBAN
      duration: "24h"
      reason: "Zu viele Verwarnungen"
    - warns: 5
      action: BAN
      reason: "Maximale Verwarnungen erreicht"

---



📄 Lizenz

Dieses Projekt steht unter der Apache License 2.0.
Siehe LICENSE
 für Details.

👤 Autor

ShadowCrest
GitHub: https://github.com/ShadowCrestDev
