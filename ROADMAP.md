# Roadmap – ShadowCrestModeration v1.3.0

> **Goal of v1.3.0:**  
> Improve **staff efficiency**, **clarity**, and **safety** without adding complexity.

This version focuses on **quality-of-life improvements for moderators** rather than adding mandatory workflows.

---

## 🎯 Focus
**Make moderation faster, safer, and more transparent for staff.**

---

## ✨ Planned Features

### 🧾 1) Ticket Notes (Internal)
- Staff can add **internal notes** to tickets
- Visible to staff only
- Useful for:
    - Staff handovers
    - Internal evaluations
    - Documentation

**Example:**
> “Player repeatedly reported – keep an eye on them.”

---

### 📜 2) Staff Action Log
- All moderation actions will be logged:
    - Warn
    - Kick
    - Ban / Tempban
    - Ticket actions
- Output options:
    - Staff-only chat messages
    - Optional file logging (`staff-actions.log`)

Improves **team transparency and accountability**.

---

### 🔒 3) Confirmation GUI for Critical Actions
- Additional confirmation step for:
    - Ban
    - Tempban
- Prevents accidental clicks in the Actions GUI

---

### 🔔 4) Optional Discord Webhooks
- Notifications for:
    - New tickets
    - Ticket closed
    - Severe punishments
- Fully optional and configurable

---

### 🧠 5) Auto-Close After Action
- Optional automatic ticket closure after:
    - Ban
    - Tempban
- Configurable per action

---

## 🛠 Technical Goals
- Further modularization of GUI components
- Cleaner listener separation
- Improved extensibility
- Preparation for optional database support in the future

---

## ❌ Not Planned
- No web panel
- No mandatory database
- No breaking API changes
- No forced workflow changes

---

## 🏁 Release Goal
**v1.3.0 = “Staff Efficiency Update”**

A release where staff feel:
> *“This saves time.”*

---

## 🔮 Future Ideas (v1.4.0+)
- Ticket templates
- Category-based auto actions
- Optional staff statistics
