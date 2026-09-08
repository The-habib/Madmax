# MadMax Engineering Roadmap

> **Status:** Milestones 1.0, 1.1, 1.2, and 1.3 are **100% COMPLETE & VERIFIED**.

---

## 🎯 Milestones Trajectory

```
Milestone 1.0: Engineering Foundation (Completed 2026-08)
  ├── Toolchain standardization, Gradle 9.2.1, API 36, NDK 29
  ├── .madmax/ rules, blueprints, architecture specs
  └── Protected core boundaries established

Milestone 1.1: Extension Layer & Material 3 (Completed 2026-08)
  ├── Decoupled Extension Manager & Dynamic Feature Registry
  ├── Material 3 DayNight Theming & Monet Dynamic Colors
  ├── Redesigned Session Drawer with rounded cards
  ├── Modernized Settings Hub
  └── Hidden Developer Diagnostics Dashboard (5-tap trigger)

Milestone 1.2: AI Workspace & Command Intelligence (Completed 2026-08)
  ├── Material 3 AI Workspace Bottom Sheet (Explain, Generate, Diagnose, History, GitHub)
  ├── Offline Command Intelligence Engine (initial 28 commands with flag parsing)
  ├── Regex Error Analyzer (automated stderr classification & fixes)
  └── Multi-ABI Debug APK Release pipeline (v1.2.0-dev)

Milestone 1.3: Upstream Security Integration & AI Evolution (Completed 2026-09)
  ├── Upstream security patch cherry-picked (commit 3b66f87 in RunCommandService)
  ├── Expanded offline command intelligence database to 70+ essential CLI tools
  ├── Pipe-to-shell unverified script danger heuristics (curl/wget | sh/bash)
  ├── Extended AI error analyzer (Git conflicts, syntax errors, DNS failures)
  └── Comprehensive unit test suite with 24 tests passing (100% success rate)

Milestone 1.4: Baseline Alignment & Remote Provider Connectors (Current Target)
  ├── GitHub default branch alignment (reconcile dev to master)
  ├── Re-tagging v1.2.0-dev to dev tip
  └── Remote LLM Provider Connectors (Ollama local network, Gemini, OpenAI)

Milestone 2.0: Cloud Ecosystem & Advanced Plugins (Future)
  ├── Deep GitHub Workspace Sync (PR reviews, issue browser via gh CLI)
  └── Modular Socket Plugin Architecture
```

---

## Completed Milestones

### Milestone 1.0: Engineering Foundation
* Architecture blueprints, coding standards, Conventional Commits policy.
* Verification of protected core files (`terminal-emulator/`, `termux.c`, `local-socket.cpp`, `targetSdkVersion=28`).

### Milestone 1.1: Extension Layer & Material 3
* `com.termux.app.madmax.core.MadMaxExtensionManager`
* `com.termux.app.madmax.features.MadMaxFeatureRegistry`
* `com.termux.app.madmax.ui.theme.MadMaxThemeManager`
* `com.termux.app.madmax.dev.DeveloperDashboardActivity` & `SystemMetricsCollector`
* Settings fragments for Appearance, AI, Developer, GitHub, Plugins.

### Milestone 1.2: AI Workspace & Command Intelligence
* Initial `AIWorkspaceBottomSheet` with Explain, Generate, Diagnose, History, and GitHub tabs.
* Decoupled `AIProviderManager` and `AIProvider` abstractions.
* Asynchronous service routing with `AICommandService` and `AIHistoryManager`.

### Milestone 1.3: Upstream Security & AI Evolution
* Clean cherry-pick of `3b66f87` (`RunCommandService.java` security fix).
* 70+ CLI tools in `OfflineCommandIntelligence.java`.
* Unverified remote script pipe detection (`curl ... | bash`).
* 13 diagnosis categories in `AIErrorAnalyzer.java`.
* Complete unit test coverage for new capabilities.

---

## Active & Upcoming Milestones

### Milestone 1.4: Remote AI Providers & Baseline Alignment
1. **GitHub Default Branch Reconciliation:**  
   Reconcile `master` with `dev` on GitHub so new clones immediately receive the full MadMax codebase.
2. **Release Tag Rectification:**  
   Ensure tag `v1.2.0-dev` accurately references the tip of `dev`.
3. **Remote LLM Connectors:**  
   Build network HTTP clients for local Ollama instances (`http://127.0.0.1:11434`) and Gemini / OpenAI endpoints with secure key storage.

### Milestone 2.0: Cloud & Remote Integrations
1. **Deep GitHub Workspace:** Live PR review panel and Codespaces connector.
2. **Advanced Session Metrics:** Per-session CPU/memory usage indicators in the drawer.
