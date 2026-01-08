# ShadowCrestModeration (SCM)

**ShadowCrestModeration** is a modern, lightweight moderation plugin for **Paper & Spigot**.  
It focuses on **clarity**, **performance**, and **staff efficiency**, featuring a powerful **GUI-based ticket system**, private ticket chats, and integrated moderation tools.

---

## ✨ Features

### 🎫 Ticket System
- Fully **GUI-based ticket creation**
- Step-by-step ticket workflow:
  - Category → Target → Additional info
- Ticket cooldown & maximum open tickets per player
- Staff ticket overview with pagination
- Ticket detail GUI with:
  - Claim / Unclaim
  - Teleport to ticket creator
  - Close ticket with predefined reasons
- Ticket status system (`OPEN`, `CLAIMED`, `CLOSED`)
- Automatic staff notifications

---

### 💬 Ticket Chat
- Private chat between supporter and ticket creator
- Reply using `/t <message>`
- Optional toggle mode for ticket chat
- Automatically disabled when ticket is closed
- Safe session handling

---

### 🛠 Ticket Actions GUI
- Moderate directly from the ticket:
  - Warn
  - Kick
  - Tempban (1h / 1d)
  - Permanent ban
- Default punishment reason: `Ticket`
- Fully language-driven (de_DE / en_US)
- Clean separation between detail GUI and actions GUI

---

### 👥 Internal Team Chat
- Built-in internal staff chat
- Toggle mode or single-message usage
- Separate chat format
- Permission-based
- Independent from ticket chat

---

### 🔔 Moderation System
- Warn system with history
- Automatic punishments (configurable)
- Kick, Ban, Tempban
- IP Ban / Unban
- Clear warnings
- Join logs for staff
- Optional playtime display

---

## ⚙ Configuration
- Fully configurable via `config.yml`
- All messages configurable via language files:
  - `de_DE.yml`
  - `en_US.yml`
- Prefix, cooldowns, limits, formats, actions

---

## 📦 Installation
1. Download the latest `.jar` from **Releases**
2. Place it in your `plugins/` folder
3. Start the server
4. Configure `config.yml` and language files
5. Optional: `/scm reload`

---

## 📜 Commands

### General
| Command | Description |
|------|------------|
| `/ticket` | Open ticket creation GUI |
| `/t <message>` | Reply to active ticket chat |
| `/tc` | Toggle ticket chat mode |
| `/teamchat` | Toggle staff team chat |
| `/teamchat <msg>` | Send a single team chat message |

---

### Moderation
| Command | Description |
|------|------------|
| `/warn <player> <reason>` | Warn a player |
| `/warns <player>` | Show warnings |
| `/clearwarns <player>` | Clear warnings |
| `/kick <player> <reason>` | Kick a player |
| `/ban <player> <reason>` | Permanent ban |
| `/tempban <player> <time> <reason>` | Temporary ban |
| `/unban <player>` | Unban player |
| `/ipban <player/ip> <reason>` | IP ban |
| `/unipban <ip>` | Remove IP ban |
| `/playtime <player>` | Show playtime |

---

### SCM
| Command | Description |
|------|------------|
| `/scm reload` | Reload configuration |
| `/scm info` | Plugin info |
| `/scm gui` | Open staff ticket GUI |
| `/scm accept` | Accept next open ticket |
| `/scm close <id> [reason]` | Close ticket |
| `/scm tpticket <id>` | Teleport to ticket creator |

---

## 🔐 Permissions
- `shadowcrest.mod.warn`
- `shadowcrest.mod.kick`
- `shadowcrest.mod.ban`
- `shadowcrest.mod.tempban`
- `shadowcrest.mod.ipban`
- `shadowcrest.mod.unban`
- `shadowcrest.mod.unipban`
- `shadowcrest.mod.clearwarns`
- `shadowcrest.mod.warns`
- `shadowcrest.mod.playtime`
- `shadowcrest.mod.notify`
- `shadowcrest.mod.ticket.staff`
- `shadowcrest.mod.ticket.accept`
- `shadowcrest.mod.ticket.close`
- `shadowcrest.mod.ticket.tp`
- `shadowcrest.mod.teamchat`
- `shadowcrest.mod.reload`
- `shadowcrest.mod.info`

(Default: OP)

---

## 🔧 Compatibility
- **Minecraft:** 1.19 – 1.21.x
- **Server:** Paper / Spigot
- **Java:** 17+

---

## 🛣 Roadmap
Planned features and future improvements:  
➡️ [ROADMAP.md](ROADMAP.md)

---

## 📄 License
This project is licensed under the **Apache License 2.0**.  
See the `LICENSE` file for details.

---

## ❤️ Support
If you encounter bugs or have feature requests,  
please open an **Issue** on GitHub.
