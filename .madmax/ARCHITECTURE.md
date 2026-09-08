# MadMax Architecture Reference

> **Summary:** High-level architectural reference for developers and AI agents working on MadMax.

---

## 1. System Components Topology

```
┌───────────────────────────────────────────────────────────────────────────────────┐
│                           MADMAX EXTENSION & UI LAYER                             │
│                                                                                   │
│   ┌────────────────────────┐  ┌───────────────────────┐  ┌────────────────────┐   │
│   │  AI Workspace Bottom   │  │  Developer Dashboard  │  │ Material 3 Settings│   │
│   │  Sheet (Explain, Gen,  │  │  Activity (Hardware,  │  │ Hub (Fragments,    │   │
│   │  Diagnose, Git, Hist)  │  │  SDK, Live Metrics)   │  │ Feature Flags)     │   │
│   └───────────┬────────────┘  └───────────┬───────────┘  └─────────┬──────────┘   │
│               │                           │                        │              │
│   ┌───────────▼───────────────────────────▼────────────────────────▼──────────┐   │
│   │                  MadMax Extension Manager & Feature Registry              │   │
│   │                  - Monet Dynamic Colors (MadMaxThemeManager)              │   │
│   │                  - Offline Command Intelligence & Error Analyzer          │   │
│   └───────────────────────────────────────┬───────────────────────────────────┘   │
└───────────────────────────────────────────┼───────────────────────────────────────┘
                                            │ Non-Invasive UI Hooks
┌───────────────────────────────────────────▼───────────────────────────────────────┐
│                             TERMUX PRESENTATION & UI                              │
│   TermuxActivity ───────► TerminalView ───────► TerminalRenderer (Canvas 2D)      │
│   Session Drawer ───────► ExtraKeysView ──────► Material 3 Sessions List Card     │
└───────────────────────────────────────────▲───────────────────────────────────────┘
                                            │ Local Binder IPC
┌───────────────────────────────────────────▼───────────────────────────────────────┐
│                              SERVICE PROCESS HOST                                 │
│   TermuxService (Foreground Service, Wakelocks, Notification, Sessions)           │
│   - Shell Manager (List<TermuxSession>, List<AppShell>)                           │
│   - IPC Dispatcher (RunCommandService, TermuxAmSocketServer)                      │
└───────────────────────────────────────────▲───────────────────────────────────────┘
                                            │ Native JNI Calls
┌───────────────────────────────────────────▼───────────────────────────────────────┐
│                         HEADLESS ENGINE & PTY (PROTECTED)                         │
│   TerminalEmulator (DEC/VT100 State Machine, ANSI Parser, Dual Buffers)          │
│   termux.c (Linux openpty, fork, execvp, dup2, setsid, TIOCSWINSZ)                │
│   Sysroot: /data/data/com.termux/files/usr ($PREFIX)                              │
└───────────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Component Directory Mapping

| Layer | Path | Core Responsibilities |
|---|---|---|
| **MadMax Core** | [`app/.../madmax/core/`](file:///home/runner/workspace/app/src/main/java/com/termux/app/madmax/core/) | Extension coordinator (`MadMaxExtensionManager`) and system constants. |
| **Feature Registry** | [`app/.../madmax/features/`](file:///home/runner/workspace/app/src/main/java/com/termux/app/madmax/features/) | Dynamic, thread-safe feature flags (`MadMaxFeature`, `MadMaxFeatureRegistry`). |
| **AI Intelligence** | [`app/.../madmax/ai/`](file:///home/runner/workspace/app/src/main/java/com/termux/app/madmax/ai/) | Offline command intelligence, regex error diagnosis, history, and workspace UI. |
| **Developer Tools** | [`app/.../madmax/dev/`](file:///home/runner/workspace/app/src/main/java/com/termux/app/madmax/dev/) | Diagnostics activity and hardware metric collectors. |
| **Settings Fragments** | [`app/.../madmax/settings/`](file:///home/runner/workspace/app/src/main/java/com/termux/app/madmax/settings/) | Modular preference screens (Appearance, AI, Dev, GitHub, Plugins). |
| **App Shell** | [`app/`](file:///home/runner/workspace/app/) | Top-level Android manifest, activities, foreground service, bootstrap extraction. |
| **Shared Foundation** | [`termux-shared/`](file:///home/runner/workspace/termux-shared/) | Path constants, settings engines (`SharedProperties`), shell models (`ExecutionCommand`). |
| **Canvas Renderer** | [`terminal-view/`](file:///home/runner/workspace/terminal-view/) | Custom Android `View` surface, font rendering (`TerminalRenderer`), touch gestures. |
| **Terminal Engine** | [`terminal-emulator/`](file:///home/runner/workspace/terminal-emulator/) | Headless VT100/ANSI parser, circular buffers, and native Linux PTY (`termux.c`). **PROTECTED: DO NOT MODIFY.** |

---

## 3. Extension Decoupling Architecture

All MadMax capabilities follow strict decoupling rules:
1. **Additive Subpackages:** All new business logic resides under `com.termux.app.madmax.*`.
2. **Minimal Touchpoints:** Core Termux classes (`TermuxActivity`, `TermuxApplication`) only interact with extensions via single-line initializers or standard Android event callbacks.
3. **Safe Interactivity with Terminal:** The AI Workspace inserts text or executes commands solely through standard public session interfaces (`session.write(command)`), capturing transcripts via `session.getEmulator().getScreen().getTranscriptText()`.
4. **Offline by Default:** The intelligence engine operates 100% locally with zero required network calls or mandatory external API dependencies.
