# MadMax Engineering Roadmap

This roadmap defines the prioritized evolutionary milestones for MadMax. Every feature is designed to be implemented modularly on top of the untouched Termux core engine.

---

## 🎯 Strategic Milestones Overview

```
Phase 1: Modern Foundations (Q3 2026)
  ├── Material 3 Theming Engine & Dynamic Colors
  ├── Advanced Visual Session Manager
  └── Modernized Preference & Settings Architecture

Phase 2: Developer Ecosystem & Cloud Integration (Q4 2026)
  ├── AI Assistant Integration (Local / API LLM CLI Hook)
  ├── Deep GitHub CLI & Repository Workspace Sync
  └── GitHub Codespaces & Remote SSH Container Launcher

Phase 3: Extensibility & Performance (Q1 2027)
  ├── High-Performance Plugin Architecture
  ├── Real-time Developer Dashboard
  └── Low-Overhead Terminal Performance Monitor
```

---

## Phase 1: Modern Foundations

### 1. Material 3 Theming & Dynamic Colors
* **Description:** Implement Material You / Material Design 3 components across all navigation drawers, dialogs, settings fragments, and toolbar elements. Support dynamic wallpaper color extraction (Android 12+ Monet engine).
* **Affected Files:** `app/src/main/res/values/themes.xml`, `app/src/main/res/values-night/themes.xml`, `app/src/main/res/layout/activity_termux.xml`, `termux-shared/.../theme/TermuxThemeUtils.java`.
* **Risk Level:** 🟢 Low (Pure UI resource and view styling).
* **Implementation Strategy:** Update theme inheritance to `Theme.Material3.DayNight.NoActionBar` while ensuring fallback backward compatibility for Android 5.0 - 11.
* **Merge Safety:** High.

### 2. Advanced Visual Session Manager
* **Description:** Overhaul the session drawer with live process previews, execution status badges (running, idle, exited with code), custom session naming, session color tagging, and drag-and-drop reordering.
* **Affected Files:** `app/.../terminal/TermuxSessionsListViewController.java`, `app/src/main/res/layout/item_terminal_sessions_list.xml`, `app/src/main/res/layout/activity_termux.xml`.
* **Risk Level:** 🟡 Moderate (Interacts with `TermuxService` session lists).
* **Implementation Strategy:** Extend session metadata wrappers without mutating `TerminalSession.java`.
* **Merge Safety:** High.

### 3. Modernized Settings Architecture
* **Description:** Transform the legacy flat preferences into a structured, searchable, modern Material 3 preferences hub with real-time live preview of cursor styles, extra-keys layouts, and colors.
* **Affected Files:** `app/.../activities/SettingsActivity.java`, `app/.../fragments/settings/`, `termux-shared/.../settings/`.
* **Risk Level:** 🟢 Low.
* **Implementation Strategy:** Refactor `PreferenceFragmentCompat` classes into modular categories (Appearance, Terminal, Keys, Cloud, AI, Security).
* **Merge Safety:** High.

---

## Phase 2: Developer Ecosystem & Cloud Integration

### 1. In-Terminal AI Assistant
* **Description:** Add an optional non-intrusive assistant sidebar / bottom sheet capable of explaining terminal errors, generating shell commands with safety checks, and auto-completing complex CLI invocations.
* **Affected Files:** `app/.../terminal/`, `app/src/main/res/layout/`.
* **Risk Level:** 🟢 Low.
* **Implementation Strategy:** Operates as a distinct UI overlay that passes selected text or error streams to local/remote LLM endpoints and optionally injects approved commands into the active `TerminalSession` input stream.
* **Merge Safety:** High.

### 2. Deep GitHub Integration
* **Description:** Direct integration with GitHub CLI (`gh`), repository cloning wizards, issue tracking, and PR review helpers in a dedicated workspace panel.
* **Affected Files:** New modular subpackage `app/.../github/`.
* **Risk Level:** 🟢 Low.
* **Implementation Strategy:** Interacts via background `AppShell` running `gh` commands and renders results in a native UI drawer.
* **Merge Safety:** High.

### 3. Codespaces & Remote SSH Container Launcher
* **Description:** One-click launch and connection to GitHub Codespaces and remote SSH server instances with saved port forwarding and key management.
* **Affected Files:** New modular subpackage `app/.../cloud/`.
* **Risk Level:** 🟢 Low.
* **Implementation Strategy:** Generates SSH session configurations and starts `TerminalSession` running `ssh` or GitHub Codespaces CLI.
* **Merge Safety:** High.

---

## Phase 3: Extensibility & Performance

### 1. Modular Plugin Architecture
* **Description:** Modernize the IPC mechanism for plugin modules with reactive Binder services and bidirectional event streaming.
* **Affected Files:** `termux-shared/.../plugins/`, `termux-shared/.../shell/`.
* **Risk Level:** 🟡 Moderate.
* **Implementation Strategy:** Build reactive wrapper APIs around existing `RUN_COMMAND` intent protocols.
* **Merge Safety:** High.

### 2. Real-time Developer Dashboard
* **Description:** Overlay dashboard displaying system metrics: CPU, RAM, active PTY threads, network traffic, battery drain rate, and storage utilization.
* **Affected Files:** `app/.../terminal/`, `termux-shared/.../models/`.
* **Risk Level:** 🟢 Low.
* **Implementation Strategy:** Native background reader polling `/proc/stat`, `/proc/net/dev`, and Android battery APIs.
* **Merge Safety:** High.

### 3. Low-Overhead Terminal Performance Monitor
* **Description:** Frame timing, character render throughput benchmarks, and scrollback memory allocation tracker for power users and terminal benchmarkers.
* **Affected Files:** `terminal-view/.../TerminalRenderer.java`.
* **Risk Level:** 🟡 Moderate (Needs careful profiling to avoid adding overhead to render loops).
* **Implementation Strategy:** Optional debug overlay tracking draw intervals and frame drops.
* **Merge Safety:** High.
