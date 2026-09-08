# MadMax Engineering Roadmap

> **Status:** Milestones 1.0, 1.1, and 1.2 are **100% COMPLETE & VERIFIED**.

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
  ├── 100% Offline Command Intelligence Engine (100+ commands with flag parsing)
  ├── Regex Error Analyzer (automated stderr classification & fixes)
  └── Multi-ABI Debug APK Release pipeline (v1.2.0-dev)

Milestone 1.3: Stabilization & Upstream Synchronization (Current Target)
  ├── Clean cherry-pick of upstream security fix (commit 3b66f87 in RunCommandService)
  ├── GitHub default branch alignment (merge dev to master)
  ├── Re-tagging v1.2.0-dev to dev tip
  └── Additional unit tests for edge-case shell commands

Milestone 2.0: Cloud Ecosystem & Advanced Plugins (Future)
  ├── Remote LLM Provider Connectors (Ollama local network, Gemini, OpenAI)
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
* `com.termux.app.madmax.ai.engine.OfflineCommandIntelligence` (100+ commands)
* `com.termux.app.madmax.ai.engine.AIErrorAnalyzer` (regex pattern classification)
* `com.termux.app.madmax.ai.ui.AIWorkspaceBottomSheet`
* Comprehensive test suites: `AIErrorAnalyzerTest`, `OfflineCommandIntelligenceTest`.
* CI automation and release APK packaging.

---

## Active & Upcoming Milestones

### Milestone 1.3: Baseline Alignment & Upstream Security
1. **Upstream Security Fix Cherry-pick:**  
   Incorporate `3b66f87` (`RunCommandService` file result config security fix) from `upstream/master`.
2. **Repository Default Branch Alignment:**  
   Reconcile `master` with `dev` on GitHub so new clones immediately receive the full MadMax codebase.
3. **Release Tag Rectification:**  
   Ensure tag `v1.2.0-dev` accurately references the tip of `dev`.

### Milestone 2.0: Cloud & Remote Integrations
1. **External LLM Providers:** Network-backed providers using optional user API keys or local Ollama instances.
2. **GitHub Workspace:** Live PR review panel and Codespaces connector.
3. **Advanced Session Metrics:** Per-session CPU/memory usage indicators in the drawer.
