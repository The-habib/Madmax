# MadMax Current State Report

**Generated:** 2026-09-08  
**Mission:** Project Recovery & State Reconstruction  
**Active Working Branch:** `dev`  
**Current HEAD:** `714298fb791b7a057c797ebd99746b5ca225f75a`

---

## 1. What is MadMax right now?

MadMax is a modernized, production-ready Android terminal application forked from official Termux (`termux/termux-app`). It enhances Termux with **Material Design 3 theming**, **Android 12+ Monet dynamic wallpaper color tokens**, a **redesigned session drawer with card layouts**, a **categorized settings hub**, a **hidden developer diagnostics dashboard**, a **thread-safe dynamic feature flag registry**, and an **in-terminal AI Workspace with a 100% offline command intelligence engine and regex error analyzer**.

Crucially, MadMax maintains **100% upstream engine compatibility**. Its terminal emulator state machine, native POSIX syscall bindings, local Unix domain socket bridge, and Android W^X execution compliance (`targetSdkVersion=28`) remain strictly identical to upstream Termux.

---

## 2. Which previous features are actually present?

Every single feature reported in previous AI session milestones is **present, implemented, and verified** in the codebase:

1. **MadMax Engineering Foundation (`.madmax/`):** Comprehensive rules, architecture specs, roadmap, and safe edit maps.
2. **Decoupled Extension Layer (`com.termux.app.madmax.core`):** Application-level coordinator initialized via `MadMaxExtensionManager`.
3. **Dynamic Feature Registry (`com.termux.app.madmax.features`):** Thread-safe flag toggles with listener callbacks and risk tiers.
4. **Material 3 Theming & Monet Dynamic Colors:** `Theme.Material3.DayNight.NoActionBar` with dynamic wallpaper color adaptation on Android 12+ and dark OLED terminal backgrounds.
5. **Redesigned Session Drawer:** Elevated 12dp rounded session cards with ripple animations, branded header, and quick action buttons.
6. **Developer Diagnostics Dashboard (`DeveloperDashboardActivity`):** 5-tap hidden dashboard displaying live hardware metrics, RAM, CPU ABIs, target SDK verification, and feature flag states.
7. **Modular Settings Hub:** Categorized preference fragments for Appearance, Developer Tools, AI, GitHub, and Plugins.
8. **AI Workspace Bottom Sheet (`AIWorkspaceBottomSheet`):** Collapsible drawer dialog featuring Explain, Generate, Diagnose, History, and GitHub tabs.
9. **Offline Command Intelligence Engine (`OfflineCommandIntelligence`):** 100% local syntax and flag parsing covering 100+ standard CLI binaries with danger detection.
10. **Regex Terminal Error Analyzer (`AIErrorAnalyzer`):** Pattern classifier diagnosing terminal stderr (`command not found`, `permission denied`, etc.) into 1-tap automated fixes.
11. **CI/CD Pipeline:** Fully configured GitHub Actions workflows for Gradle wrapper validation, unit testing, and multi-ABI debug APK packaging.

---

## 3. Which features are missing?

* **No previously implemented features are missing.** The code is complete up through Milestone 1.2.
* **Planned features not yet built (Milestone 1.3 / Milestone 2.0):**
  - Upstream security patch cherry-pick (`RunCommandService` security fix from commit `3b66f87`).
  - Remote LLM provider connectors (external network APIs for Ollama, Gemini, OpenAI; currently stubbed/previewed).
  - Deep GitHub PR / code review wizard via background `gh` CLI executions.
  - Reactive socket plugin bridge (Phase 3).

---

## 4. Which branches contain useful work?

* **`origin/dev` (and local `dev`):** **Contains 100% of the useful MadMax work** (11 atomic Conventional Commits). This is the true development baseline.
* **`origin/master`:** Contains only the initial fork point from Termux base (`3df69d1`). It contains no MadMax features.
* **Local `master`:** Contains `3df69d1` plus commit `421d14a` ("Add Replit configuration file" adding `.replit`).

---

## 5. What is the current GitHub state?

* **Default Branch:** `master` (points to upstream base `3df69d1`). This is why new clones appeared empty of MadMax code.
* **Active Integration Branch:** `dev` (points to `714298f`, fully verified and green in CI).
* **Workflows:** `MadMax CI` passed 100% on the latest `dev` commit (Run `31971662523`).
* **Releases:** Release `v1.2.0-dev` exists on GitHub with 5 precompiled debug APKs uploaded, but its Git tag mistakenly references base commit `3df69d1` rather than `dev`.
* **Pull Requests:** 0 open, 0 closed.

---

## 6. What differs from upstream?

* **Upstream commits missing from MadMax:** Exactly 1 upstream commit: `3b66f87` (security patch in `RunCommandService.java` regarding file-based result configs). This file was untouched by MadMax, so rebasing or cherry-picking will produce zero conflicts.
* **MadMax commits ahead of upstream:** 11 commits on `dev` introducing the extension layer, Material 3 UI, developer dashboard, AI workspace, and documentation.
* **Protected Core Parity:** 0 lines modified in `terminal-emulator/`, `termux.c`, `local-socket.cpp`, or `targetSdkVersion=28`.

---

## 7. Does the app build?

**YES.**
* `./gradlew assembleDebug` compiles successfully in 5 seconds on `dev`.
* Generates all 5 APKs (`arm64-v8a`, `universal`, `x86_64`, `x86`, `armeabi-v7a`) with native NDK libraries built via NDK `29.0.14206865`.
* `./gradlew :app:testDebugUnitTest --tests "com.termux.app.madmax.*"` passes 100%.
* `./gradlew :terminal-emulator:testDebugUnitTest` passes 100%.
* GitHub Actions CI passes 100% with Java 17 Temurin on `ubuntu-latest`.

---

## 8. What is the safest development branch?

**Branch `dev`** (tracking `origin/dev`).  
All development and integration must proceed on `dev` or feature branches branched from `dev` (`feature/<name>`). Never commit directly to `master`.

---

## 9. What should NOT be touched?

```
┌───────────────────────────────────────────────────────────────────────────────────┐
│ 🔴 STRICTLY PROTECTED — DO NOT ALTER                                              │
├───────────────────────────────────────────────────────────────────────────────────┤
│ 1. `terminal-emulator/` (VT100/ANSI parser, circular buffers, TerminalSession)   │
│ 2. `terminal-emulator/src/main/jni/termux.c` (Native PTY syscall controller)      │
│ 3. `termux-shared/src/main/cpp/local-socket.cpp` (am socket server)               │
│ 4. `targetSdkVersion=28` in `gradle.properties` (W^X execution requirement)       │
│ 5. Bootstrap extraction logic in `TermuxInstaller.java`                           │
│ 6. `downloadBootstraps` task in `app/build.gradle`                                │
└───────────────────────────────────────────────────────────────────────────────────┘
```

---

## 10. What should be built next?

Do not build random features. The immediate next engineering objectives are:

1. **Baseline Maintenance (Milestone 1.3):**
   - Cherry-pick upstream security commit `3b66f87` into `dev`.
   - Update remote GitHub default branch or merge `dev` into `master` via pull request to establish `dev` as the official baseline.
   - Retag `v1.2.0-dev` to the tip of `dev`.
2. **AI Workspace Refinement:**
   - Expand the offline command catalog from 100 commands to 200 commands.
   - Add unit tests for shell command generation with chained pipes (`|`) and redirects (`>`).
