# Release Notes: MadMax v1.1.0-dev

**Release:** `v1.1.0-dev`  
**Date:** 2026-08-16  
**Status:** Pre-release / Developer Preview  
**Commit:** `c90bff11` / `HEAD` on branch `dev`

---

## 🌟 Welcome to MadMax v1.1.0-dev

MadMax v1.1.0-dev is the inaugural release of the **MadMax** fork of Termux, delivering a modern **Material Design 3** visual overhaul, **Android 12+ Monet dynamic theming**, an extensible **Feature Flag Registry**, and a **Developer Diagnostics Dashboard** while preserving 100% upstream compatibility with the protected Termux terminal core.

---

## 🚀 Highlights

### 🎨 Material Design 3 & Monet Engine
- Modernized app and activity styles using `Theme.Material3.DayNight.NoActionBar`.
- Automatically extracts dynamic palette colors from your Android 12+ wallpaper.
- High-contrast pure black `#000000` terminal background for optimal OLED battery savings and crisp readability.

### 📑 Redesigned Session Drawer
- 280dp wide navigation drawer with elevated 12dp rounded cards.
- Live process status dots (Running, Exited, Idle).
- Quick action header for instant access to Settings and Developer Diagnostics.

### ⚙️ Material 3 Settings Hub & Developer Dashboard
- Categorized settings layout with placeholders for AI Assistant (Phase 2), Plugins Hub (Phase 3), and GitHub Workspace Sync (Phase 2).
- Built-in hardware, RAM, CPU ABI, and SDK 28 W^X mode diagnostic inspector.
- Five-tap secret header shortcut to open the Developer Dashboard.

---

## 🔒 Security & Upstream Compatibility

- **`terminal-emulator/`:** 100% pristine upstream code.
- **`termux.c` / `local-socket.cpp`:** Unmodified.
- **`targetSdkVersion=28`:** Preserved to ensure Android binary execution capability within `$PREFIX/bin`.
