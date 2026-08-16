# MadMax Architecture Reference

> **Summary:** High-level architectural reference for developers and AI agents working on MadMax.

---

## 1. System Components Topology

```
┌───────────────────────────────────────────────────────────────────────────┐
│                                 ANDROID UI                                │
│  TermuxActivity ──────► TerminalView ──────► TerminalRenderer (Canvas 2D) │
│  Navigation Drawer ───► ExtraKeysView ─────► Material 3 Preferences Hub   │
└─────────────────────────────────────▲─────────────────────────────────────┘
                                      │ Local Binder IPC
┌─────────────────────────────────────▼─────────────────────────────────────┐
│                            SERVICE PROCESS HOST                           │
│  TermuxService (Foreground Service, Wakelocks, Notification, Sessions)    │
│  - Shell Manager (List<TermuxSession>, List<AppShell>)                    │
│  - IPC Dispatcher (RunCommandService, TermuxAmSocketServer)               │
└─────────────────────────────────────▲─────────────────────────────────────┘
                                      │ Native JNI Calls
┌─────────────────────────────────────▼─────────────────────────────────────┐
│                          HEADLESS ENGINE & PTY                            │
│  TerminalEmulator (DEC/VT100 State Machine, ANSI Parser, Dual Buffers)   │
│  termux.c (Linux openpty, fork, execvp, dup2, setsid, TIOCSWINSZ)         │
│  Sysroot: /data/data/com.termux/files/usr ($PREFIX)                       │
└───────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Component Directory Mapping

| Layer | Path | Core Responsibilities |
|---|---|---|
| **App Shell** | [`app/`](file:///workspaces/Madmax/app/) | Top-level Android manifest, activities, foreground service, bootstrap extraction, and SAF document providers. |
| **Shared Foundation** | [`termux-shared/`](file:///workspaces/Madmax/termux-shared/) | Path constants, settings engines (`SharedProperties`), shell models (`ExecutionCommand`), and crash diagnostics (`ReportActivity`). |
| **Canvas Renderer** | [`terminal-view/`](file:///workspaces/Madmax/terminal-view/) | Custom Android `View` surface, font rendering (`TerminalRenderer`), touch gestures, and selection handles. |
| **Terminal Engine** | [`terminal-emulator/`](file:///workspaces/Madmax/terminal-emulator/) | Pure headless VT100/ANSI parser, circular memory buffers (`TerminalBuffer`), and native Linux PTY controller (`termux.c`). **PROTECTED: DO NOT MODIFY.** |

---

## 3. Data Flow Architecture

### 3.1 Keystroke Flow (User $\rightarrow$ Shell)
1. User touches soft keyboard or on-screen `ExtraKeysView`.
2. `TerminalView` receives key event / IME commit.
3. `KeyHandler` translates keycode + modifiers to ANSI escape bytes.
4. `TerminalSession.write()` writes bytes directly into native PTY `master_fd`.
5. Linux kernel transfers bytes across PTY slave to the child process stdin.

### 3.2 Output Flow (Process $\rightarrow$ Screen)
1. Child process (e.g. `bash`) writes stdout/stderr to PTY slave.
2. Linux kernel makes data available on PTY `master_fd`.
3. `TerminalSession` reader thread reads bytes into `ByteQueue`.
4. `TerminalEmulator.append()` parses escape sequences and updates `TerminalBuffer`.
5. `TerminalView.invalidate()` triggers `TerminalRenderer.onDraw()` to redraw character glyphs on the Android Canvas.

---

*For detailed data structures and low-level code mechanics, see [**`MADMAX_ENGINEERING_BLUEPRINT.md`**](file:///workspaces/Madmax/MADMAX_ENGINEERING_BLUEPRINT.md).*
