# MadMax Codebase Reconstruction Report

**Execution Date:** 2026-09-08  
**Audit Scope:** Comprehensive structural mapping and inventory of all modules, source files, resources, extensions, and configurations.

---

## 1. Top-Level Directory Topology

```
.
├── .github/                      # CI/CD workflows, issue/PR templates, CODEOWNERS
├── .madmax/                      # Engineering governance, rules, architecture, roadmap
├── RELEASE_AUDIT/                # Historical milestone audit scorecards (2026-08-16)
├── app/                          # Main application module (TermuxActivity, MadMax extensions)
├── art/                          # Application icons, banners, and design graphics
├── docs/                         # Upstream Termux documentation
├── fastlane/                     # Metadata and release packaging configs
├── gradle/                       # Gradle wrapper 9.2.1 distribution files
├── terminal-emulator/            # VT100/ANSI emulator engine & native termux.c (PROTECTED)
├── terminal-view/                # Canvas view surface, gestures, and font renderer
└── termux-shared/                # Common models, logger, preference utilities, and IPC
```

---

## 2. Module Breakdown

### 2.1 `:terminal-emulator` (Protected Terminal Engine)
* **Status:** 100% Intact & Pristine (0 lines modified vs upstream).
* **Key Components:**
  - `TerminalEmulator.java`: Headless VT100/ANSI terminal state machine.
  - `TerminalBuffer.java`: Circular screen buffer managing active rows and scrollback.
  - `TerminalSession.java`: Reader thread, byte queue, and process lifecycle host.
  - `src/main/jni/termux.c`: POSIX PTY controller (`openpty`, `fork`, `execvp`, `setsid`).

### 2.2 `:terminal-view` (Terminal Canvas View Surface)
* **Status:** 100% Intact & Pristine (0 lines modified vs upstream).
* **Key Components:**
  - `TerminalView.java`: Android `View` managing IME inputs, key shortcuts, and touch gestures.
  - `TerminalRenderer.java`: Canvas 2D text drawing and color palette mapping.

### 2.3 `:termux-shared` (Shared Utilities & Framework Foundation)
* **Status:** 99.9% Upstream Parity (Only theme XMLs updated for Material 3 inheritance).
* **Key Components:**
  - `com.termux.shared.logger.Logger`: Global structured logging.
  - `com.termux.shared.settings.preferences.SharedProperties`: Property file parser.
  - `com.termux.shared.termux.TermuxConstants`: Paths (`/data/data/com.termux/files/usr`).
  - `src/main/res/values/themes.xml`: Upgraded theme inheritance to Material 3 DayNight.
  - `src/main/cpp/local-socket.cpp`: Native domain socket server for `termux-am` (Untouched).

### 2.4 `:app` (Application Host & MadMax Extensions)
* **Status:** Fully Extended with Decoupled MadMax Modules.
* **Core Components:**
  - `TermuxActivity.java`: Main UI host activity with session drawer and bottom sheet launcher.
  - `TermuxService.java`: Foreground service managing background shell processes (Untouched).
  - `RunCommandService.java`: Background execution intent service (Untouched).
  - `TermuxInstaller.java`: Bootstrap archive unpacker (Untouched).
  - `SettingsActivity.java`: Preferences host with MadMax repo link.

---

## 3. Targeted Inventory of MadMax Components

### 3.1 Extension Core (`com.termux.app.madmax.core`)
| File | Size | Role / Description |
|---|---|---|
| [`MadMaxConstants.java`](file:///home/runner/workspace/app/src/main/java/com/termux/app/madmax/core/MadMaxConstants.java) | 1,140 B | Centralized constants: preference names, intent actions, feature flags prefix. |
| [`MadMaxExtensionManager.java`](file:///home/runner/workspace/app/src/main/java/com/termux/app/madmax/core/MadMaxExtensionManager.java) | 1,327 B | Lifecycle entrypoint initialized in `TermuxApplication.onCreate()`. Inits features and themes. |

### 3.2 Dynamic Feature Registry (`com.termux.app.madmax.features`)
| File | Size | Role / Description |
|---|---|---|
| [`MadMaxFeature.java`](file:///home/runner/workspace/app/src/main/java/com/termux/app/madmax/features/MadMaxFeature.java) | 2,997 B | Enum defining flags (`MATERIAL_YOU`, `DEVELOPER_DASHBOARD`, `AI_WORKSPACE`, etc.), defaults, and risk levels. |
| [`MadMaxFeatureRegistry.java`](file:///home/runner/workspace/app/src/main/java/com/termux/app/madmax/features/MadMaxFeatureRegistry.java) | 3,393 B | Thread-safe manager persisting flags in `SharedPreferences` with callback dispatching. |
| [`FeatureFlagListener.java`](file:///home/runner/workspace/app/src/main/java/com/termux/app/madmax/features/FeatureFlagListener.java) | 286 B | Observer interface for reactive feature flag updates. |

### 3.3 Theme & Presentation Layer (`com.termux.app.madmax.ui.theme`)
| File | Size | Role / Description |
|---|---|---|
| [`MadMaxThemeManager.java`](file:///home/runner/workspace/app/src/main/java/com/termux/app/madmax/ui/theme/MadMaxThemeManager.java) | 1,714 B | Android 12+ Monet dynamic color applicator using `DynamicColors.applyToActivitiesIfAvailable()`. |
| [`colors_madmax.xml`](file:///home/runner/workspace/app/src/main/res/values/colors_madmax.xml) | 2,126 B | Curated Material 3 color tokens (`#FF3B30` brand accent, surface containers, tonal swatches). |
| [`activity_termux.xml`](file:///home/runner/workspace/app/src/main/res/layout/activity_termux.xml) | 8,817 B | Upgraded drawer layout with MadMax header, diagnostic button, and AI workspace launcher. |
| [`item_terminal_sessions_list.xml`](file:///home/runner/workspace/app/src/main/res/layout/item_terminal_sessions_list.xml) | 3,450 B | Material 3 rounded session card with ripple effect. |

### 3.4 Developer Tools & Diagnostics (`com.termux.app.madmax.dev`)
| File | Size | Role / Description |
|---|---|---|
| [`DeveloperDashboardActivity.java`](file:///home/runner/workspace/app/src/main/java/com/termux/app/madmax/dev/DeveloperDashboardActivity.java) | 4,234 B | Fullscreen dashboard showing hardware, build, SDK, and feature registry diagnostics. |
| [`SystemMetricsCollector.java`](file:///home/runner/workspace/app/src/main/java/com/termux/app/madmax/dev/SystemMetricsCollector.java) | 4,978 B | Live hardware stats reader: RAM, low-memory status, ABIs, target SDK, and AI metrics. |
| [`activity_developer_dashboard.xml`](file:///home/runner/workspace/app/src/main/res/layout/activity_developer_dashboard.xml) | 7,651 B | Material 3 dashboard layout with categorized metric cards and copy action. |

### 3.5 AI Workspace & Offline Intelligence (`com.termux.app.madmax.ai.*`)
| Package | Class / File | Role / Description |
|---|---|---|
| **`core`** | [`AIWorkspaceManager.java`](file:///home/runner/workspace/app/src/main/java/com/termux/app/madmax/ai/core/AIWorkspaceManager.java) | Coordinates AI components; initializes history and services. |
| **`engine`** | [`OfflineCommandIntelligence.java`](file:///home/runner/workspace/app/src/main/java/com/termux/app/madmax/ai/engine/OfflineCommandIntelligence.java) | 100% offline shell parser covering 100+ commands, flag explanations, and danger detection. |
| **`engine`** | [`AIErrorAnalyzer.java`](file:///home/runner/workspace/app/src/main/java/com/termux/app/madmax/ai/engine/AIErrorAnalyzer.java) | Regex classifier diagnosing terminal stderr into actionable fixes. |
| **`engine`** | [`AIProvider.java`](file:///home/runner/workspace/app/src/main/java/com/termux/app/madmax/ai/engine/AIProvider.java) | Interface for LLM backends. |
| **`engine`** | [`AIProviderManager.java`](file:///home/runner/workspace/app/src/main/java/com/termux/app/madmax/ai/engine/AIProviderManager.java) | Registry routing queries to active provider (Offline / Ollama / Gemini / OpenAI). |
| **`model`** | `AICommandExplanation.java` | Data model for flag breakdowns, synopsis, and safety indicators. |
| **`model`** | `AICommandGeneration.java` | Data model for natural language to shell translations. |
| **`model`** | `AIErrorDiagnosis.java` | Data model for root causes, remedy commands, and safe execution flags. |
| **`model`** | `AIHistoryItem.java` | Model for query history serialization. |
| **`model`** | `AIProviderType.java` | Enum of supported backends. |
| **`model`** | `GitHubRepoItem.java` | Model for GitHub repositories in workspace panel. |
| **`services`** | [`AICommandService.java`](file:///home/runner/workspace/app/src/main/java/com/termux/app/madmax/ai/services/AICommandService.java) | Thread pool dispatcher executing AI tasks asynchronously. |
| **`services`** | [`AIHistoryManager.java`](file:///home/runner/workspace/app/src/main/java/com/termux/app/madmax/ai/services/AIHistoryManager.java) | Persistent JSON storage for query history. |
| **`services`** | [`GitHubWorkspaceManager.java`](file:///home/runner/workspace/app/src/main/java/com/termux/app/madmax/ai/services/GitHubWorkspaceManager.java) | Manager for GitHub repos, cloning commands, and Codespaces. |
| **`ui`** | [`AIWorkspaceBottomSheet.java`](file:///home/runner/workspace/app/src/main/java/com/termux/app/madmax/ai/ui/AIWorkspaceBottomSheet.java) | Tabbed Material 3 bottom sheet: Explain, Generate, Diagnose, History, GitHub. |

### 3.6 Settings Hub Fragments (`com.termux.app.madmax.settings.fragments`)
* `AiPreferencesFragment.java`
* `AppearancePreferencesFragment.java`
* `DeveloperPreferencesFragment.java`
* `GitHubPreferencesFragment.java`
* `PluginsPreferencesFragment.java`

### 3.7 Documentation & Specifications
* **`.madmax/`**: `RULES.md`, `ARCHITECTURE.md`, `ROADMAP.md`, `FEATURES.md`, `BUILD.md`, `PROMPTS.md`, `AGENT_WORKFLOW.md`, `SAFE_EDIT_ZONES.md`.
* **Root Specs**: `MADMAX_ARCHITECTURE.md`, `MADMAX_ENGINEERING_BLUEPRINT.md`, `AI_WORKSPACE_ARCHITECTURE.md`, `COMMAND_INTELLIGENCE.md`, `AI_PROVIDER_GUIDE.md`, `GITHUB_WORKSPACE.md`, `FEATURE_FLAG_GUIDE.md`, `BRANCH_STRATEGY.md`, `RELEASE_PROCESS.md`, `TESTING_GUIDE.md`, `UI_ARCHITECTURE.md`.
* **Milestone Reports**: `MADMAX_FOUNDATION_COMPLETE.md`, `MILESTONE_1_1_COMPLETE.md`, `MILESTONE_1_2_COMPLETE.md`, `MADMAX_RELEASE_AUDIT_COMPLETE.md`.

---

## 4. Summary of Codebase State

The current codebase is in a complete, highly organized state on branch `dev`. The modular separation ensures that all MadMax innovations sit comfortably on top of the Termux foundation without compromising upstream compatibility or terminal engine stability.
