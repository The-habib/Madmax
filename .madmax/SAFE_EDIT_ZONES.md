# MadMax Safe Edit Map & Directory Classification

This document classifies every file and directory across the MadMax repository into three distinct safety tiers: **SAFE**, **CAUTION**, and **PROTECTED**.

---

## 1. Classification Definitions

```
┌───────────────────────────────────────────────────────────────────────────────────┐
│ 🟢 SAFE         - Modifiable for customization, branding, UI features, and views. │
│ 🟡 CAUTION      - Core orchestration; test thoroughly across Android lifecycles.  │
│ 🔴 PROTECTED    - Fragile POSIX/SELinux/JNI boundaries; DO NOT ALTER.             │
└───────────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Comprehensive Directory Safety Map

### 2.1 🟢 SAFE (Customization & Extensions)

| Directory / File | Allowed Modifications | Guidelines |
|---|---|---|
| [`app/src/main/java/com/termux/app/madmax/`](file:///home/runner/workspace/app/src/main/java/com/termux/app/madmax/) | Extension core, AI workspace, feature registry, developer tools. | Full freedom to implement additive features, new providers, models, and analytics. |
| [`app/src/main/res/`](file:///home/runner/workspace/app/src/main/res/) | Layouts, drawables, colors, strings, themes, menus. | Full freedom to re-skin, apply Material 3 themes, adjust padding, and add new icons. |
| [`app/src/main/java/com/termux/app/activities/`](file:///home/runner/workspace/app/src/main/java/com/termux/app/activities/) | `SettingsActivity`, `HelpActivity`, new activities. | Safe to build new settings pages, help guides, wizards, and diagnostic tools. |
| [`app/src/main/java/com/termux/app/terminal/io/`](file:///home/runner/workspace/app/src/main/java/com/termux/app/terminal/io/) | `TerminalToolbarViewPager`, `TermuxTerminalExtraKeys`. | Safe to extend extra-keys layouts, create new toolbar tabs, and add custom key handlers. |
| `docs/`, `art/`, `site/` | Documentation, logos, mockups, website assets. | Safe to update branding and guides. |
| `.madmax/` | Engineering documentation, roadmaps, reports. | Safe for workspace metadata. |

---

### 2.2 🟡 CAUTION (Core Orchestration — Test Thoroughly)

| Directory / File | Sensitive Aspects | Guidelines |
|---|---|---|
| [`TermuxActivity.java`](file:///home/runner/workspace/app/src/main/java/com/termux/app/TermuxActivity.java) | Service binding, drawer state, activity lifecycle. | Custom UI additions permitted, but preserve `ServiceConnection` lifecycle, `singleTask` launch mode, and config change handlers. |
| [`TermuxService.java`](file:///home/runner/workspace/app/src/main/java/com/termux/app/TermuxService.java) | Foreground notification, WakeLocks, process retention. | Retain foreground notification behavior, session client callbacks, and wake/wifi lock acquisitions. |
| [`RunCommandService.java`](file:///home/runner/workspace/app/src/main/java/com/termux/app/RunCommandService.java) | Security boundaries for external app intents. | Always retain permission verification against `RUN_COMMAND` and `allow-external-apps` security checks. |
| [`TermuxInstaller.java`](file:///home/runner/workspace/app/src/main/java/com/termux/app/TermuxInstaller.java) | Bootstrap extraction and symlink generation. | Do not modify extraction logic without verifying fresh installs on `aarch64`, `arm`, `x86_64`, and `i686`. |
| [`terminal-view/`](file:///home/runner/workspace/terminal-view/) | Canvas font rendering, touch gestures, IME input. | Can be customized for cursor rendering or touch gestures, but avoid adding allocations in `onDraw()` loops. |
| [`termux-shared/.../settings/`](file:///home/runner/workspace/termux-shared/src/main/java/com/termux/shared/termux/settings/) | Property parsing (`termux.properties`). | Adding new keys to `TermuxPropertyConstants` is safe, but preserve backwards compatibility of existing property names. |

---

### 2.3 🔴 PROTECTED (Do Not Modify / Engine Core)

| Directory / File | Reason for Protection |
|---|---|
| [`terminal-emulator/`](file:///home/runner/workspace/terminal-emulator/) | Pure headless VT100/ANSI terminal state machine. Modifying this risks breaking terminal compliance across thousands of CLI packages. |
| [`terminal-emulator/src/main/jni/termux.c`](file:///home/runner/workspace/terminal-emulator/src/main/jni/termux.c) | Low-level C POSIX syscalls (`openpty`, `setsid`, `fork`, `execvp`, `dup2`, `TIOCSWINSZ`). Fragile native code. |
| [`termux-shared/src/main/cpp/local-socket.cpp`](file:///home/runner/workspace/termux-shared/src/main/cpp/local-socket.cpp) | Native local domain socket server for `termux-am`. Modifying this breaks IPC between userland scripts and Android system services. |
| `targetSdkVersion=28` in [`gradle.properties`](file:///home/runner/workspace/gradle.properties) | Android 10+ (API 29+) enforces W^X execution blocking in app storage. Bumping target SDK breaks binary execution in `$PREFIX/bin`. |
| `downloadBootstraps` in [`app/build.gradle`](file:///home/runner/workspace/app/build.gradle#L218-L237) | Remote URLs and SHA-256 checksums for verified upstream rootfs packages. |
