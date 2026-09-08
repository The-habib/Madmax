# MadMax Feature Catalog & Specifications

> **Status:** All features in Section 1 and Section 2 are **IMPLEMENTED & VERIFIED** in the current codebase.

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

## 2. MadMax Enhanced Capabilities (Implemented in Milestone 1.1 & 1.2)

### 2.1 Material 3 Design & Monet Dynamic Theming
* Full Material 3 theme inheritance (`Theme.Material3.DayNight.NoActionBar`).
* Android 12+ wallpaper dynamic color extraction powered by `MadMaxThemeManager`.
* Dark OLED-optimized backgrounds with curated `#FF3B30` brand accents and surface containers.

### 2.2 Redesigned Session Drawer
* Material 3 rounded session cards with ripple animations and visual selection states.
* MadMax header typography with quick launcher buttons for Developer Diagnostics and Settings.

### 2.3 Dynamic Feature Flag Registry
* Thread-safe feature flag management via `MadMaxFeatureRegistry`.
* Configurable risk tiers (`LOW`, `MODERATE`, `HIGH`).
* Dynamic listener dispatching (`FeatureFlagListener`) for real-time reactivity without app restarts.

### 2.4 Developer Diagnostics Dashboard
* Hidden diagnostic screen accessible via 5 taps on the drawer header or through Settings.
* Real-time hardware inspection: CPU ABIs, available/total RAM, memory pressure warnings.
* Software diagnostics: Target SDK (`28` W^X compliance), Compile SDK (`36`), Git commit SHA, build type.
* Live status toggles for all registered feature flags and 1-tap clipboard report export.

### 2.5 Material 3 Settings Hub
* Modular categorized preferences:
  - **Appearance:** Dynamic Monet theming, modern drawer cards.
  - **AI Assistant:** Intelligence engine controls, provider selection, privacy boundary.
  - **Developer Tools:** Feature flags, diagnostic tools launcher.
  - **GitHub Workspace:** CLI authentication, repository sync preferences.
  - **Plugins Hub:** Extension preview and IPC bridge status.

### 2.6 AI Workspace Bottom Sheet
* Draggable, collapsible Material 3 bottom sheet accessible directly from the drawer header.
* **Explain Tab:** Analyzes shell commands, breaks down flags, and highlights destructive operations.
* **Generate Tab:** Translates natural language descriptions into safe shell syntax with 1-tap terminal insert.
* **Diagnose Tab:** Direct 1-tap capture of active terminal scrollback to diagnose errors and suggest remedies.
* **History Tab:** Searchable, persistent log of generated and analyzed commands.
* **GitHub Tab:** Quick repository cards with 1-tap cloning and Codespaces links.

### 2.7 100% Offline Command Intelligence Engine
* Pure local regex and syntax parser covering 100+ standard Linux and Termux commands (`tar`, `curl`, `find`, `grep`, `git`, `chmod`, `pkg`, etc.).
* Automated safety checks flagging dangerous patterns (`rm -rf /`, `mkfs`, unquoted variables).
* Operates completely offline with zero data transmission.

### 2.8 Regex Terminal Error Analyzer
* Instant pattern-based classification of common shell errors (`command not found`, `permission denied`, `no space left on device`, `dpkg locked`).
* Suggests safe, automated 1-tap fix commands (`pkg install <pkg>`, `chmod +x`, etc.).
