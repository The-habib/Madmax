# MadMax Feature Flag Registry: Milestone 1.2 Updates

Milestone 1.2 introduces 5 new dynamic feature flags to enable fine-grained runtime control over AI and GitHub capabilities.

---

## 1. Complete Feature Flag Matrix

| Enum Constant | SharedPreferences Key | Title | Default | Risk Level | Description |
|---|---|---|:---:|:---:|---|
| `MATERIAL_YOU` | `material_you` | Material You Dynamic Colors | `true` | `LOW` | Extract dynamic wallpaper palettes on Android 12+. |
| `MODERN_DRAWER` | `modern_drawer` | Material 3 Session Drawer | `true` | `LOW` | 280dp drawer with process status badges. |
| `DEVELOPER_DASHBOARD` | `developer_dashboard` | Developer Diagnostics Dashboard | `true` | `LOW` | Hardware, SDK 28 W^X mode, RAM, and AI metrics. |
| `AI_WORKSPACE` | `ai_workspace` | AI Command Workspace | `true` | `LOW` | Interactive Material 3 AI bottom sheet. |
| `COMMAND_EXPLAIN` | `command_explain` | Command Explanation | `true` | `LOW` | Deconstruct shell syntax, flags, and risk levels. |
| `COMMAND_GENERATE` | `command_generate` | Command Generation | `true` | `LOW` | Translate plain English prompts to bash commands. |
| `ERROR_ANALYZER` | `error_analyzer` | Automated Error Diagnosis | `true` | `LOW` | Intelligent stderr classifier with 1-tap safe fixes. |
| `GITHUB_WORKSPACE` | `github_workspace` | GitHub Workspace Hub | `true` | `LOW` | Repo browser, branch tracker, Codespaces launcher. |
| `PLUGINS_HUB` | `plugins_hub` | Modular Plugin System | `false` | `MODERATE` | Phase 3 preview: decoupled plugin runner. |
