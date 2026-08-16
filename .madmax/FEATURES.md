# MadMax Feature Catalog & Specifications

This document outlines all current and planned features for the MadMax terminal platform.

---

## 1. Core Platform Capabilities (Inherited & Upstream Compatible)

### 1.1 Full Linux Userland
* Isolated POSIX environment running in `$PREFIX` (`/data/data/com.termux/files/usr`).
* Package management via `apt` and `dpkg`.
* Shell environments: `bash`, `zsh`, `fish`, `sh`.
* Development toolchains: `clang`, `python`, `rust`, `go`, `nodejs`, `git`, `make`, `cmake`.

### 1.2 Multi-Session Management
* Concurrently running background shell instances managed by foreground `TermuxService`.
* Sliding session drawer to switch, create, and destroy sessions.
* Fail-safe process retention with CPU WakeLock and Wi-Fi Lock support.

### 1.3 Customizable Input & Extra Keys
* Dynamic virtual on-screen extra-keys toolbar with multi-page swipeable views.
* Key macro support, hardware keyboard shortcuts, and volume-key control modifiers.

---

## 2. MadMax Enhanced Capabilities (Planned / In Development)

### 2.1 Material 3 Modern Design System
* Dynamic color theming adopting Android 12+ wallpaper palette extraction.
* Modern typography, rounded cards, elevated navigation drawers, and sleek dialogs.
* Fluid micro-animations for drawer transitions and extra-keys toolbar page swipes.

### 2.2 Advanced Session Hub
* Real-time session status badges (Active process name, PID, Exit code).
* Custom session names, emoji labels, and session color coding.
* Visual split previews and drag-and-drop reordering.

### 2.3 Comprehensive Preferences Hub
* Live preview visual editor for extra-keys layout matrices.
* Color scheme palette picker with instant terminal preview.
* Cursor styling (Block, Underline, Bar) with interactive blink rate sliders.

### 2.4 In-Terminal AI Assistant Overlay
* Contextual terminal error analyzer with one-tap fix recommendations.
* Natural language to shell command generator with strict safety filters.
* Non-blocking sliding bottom sheet that respects active terminal focus.

### 2.5 Cloud & Developer Tools Integration
* Seamless GitHub CLI integration and repository workspace manager.
* One-click GitHub Codespaces and remote SSH instance connection manager.
* Built-in system resource monitor (CPU, RAM, active PTYs, network I/O).
