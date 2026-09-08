# MadMax AI Workspace Architecture

Milestone 1.2 transforms MadMax into an **AI-native terminal** while preserving strict, 100% decoupling from the core terminal emulator.

---

## 1. System Architecture Diagram

```mermaid
graph TD
    subgraph UI Layer (Theme.Material3)
        TA[TermuxActivity]
        BTN[Drawer Header AI Button]
        BS[AIWorkspaceBottomSheet: BottomSheetDialogFragment]
        EXP[Explain View]
        GEN[Generate View]
        DIAG[Diagnose View]
        HIST[History View]
        GH[GitHub View]
    end

    subgraph AI Service & Orchestration Layer (com.termux.app.madmax.ai.*)
        AWM[AIWorkspaceManager]
        ACS[AICommandService]
        APM[AIProviderManager]
        AHM[AIHistoryManager: JSON File Cache]
        GWM[GitHubWorkspaceManager]
    end

    subgraph Intelligence Engines
        OCI[OfflineCommandIntelligence: 100+ Tools DB]
        AEA[AIErrorAnalyzer: Regex Classification]
        MOCK[Mock AI Provider]
        CLOUD[Cloud AI Connectors: Gemini / Ollama / OpenAI]
    end

    subgraph Core Terminal Subsystem (100% UNTOUCHED)
        TS[TerminalSession]
        TV[TerminalView]
        PTY[Linux Shell PTY / Process]
    end

    TA --> BTN
    BTN --> BS
    BS --> EXP & GEN & DIAG & HIST & GH
    EXP & GEN & DIAG --> ACS
    HIST --> AHM
    GH --> GWM
    ACS --> APM
    APM --> OCI & AEA & MOCK & CLOUD
    GEN -->|1-Tap Insert/Run| TS
    TS --> PTY
```

---

## 2. Component Directory Structure

```
app/src/main/java/com/termux/app/madmax/ai/
├── core/
│   └── AIWorkspaceManager.java          <-- Subsystem coordinator & initializer
├── engine/
│   ├── AIErrorAnalyzer.java             <-- Regex classifier for terminal stderr
│   ├── AIProvider.java                  <-- Async callback provider interface
│   ├── AIProviderManager.java           <-- Router for offline / cloud providers
│   └── OfflineCommandIntelligence.java  <-- 100+ command knowledge base & risk detector
├── model/
│   ├── AICommandExplanation.java        <-- Command breakdown, flags, risk levels
│   ├── AICommandGeneration.java         <-- Prompt translation, confidence score
│   ├── AIErrorDiagnosis.java            <-- Categorized diagnosis, remedy, safe fix
│   ├── AIHistoryItem.java               <-- Query/result history record
│   ├── AIProviderType.java              <-- Provider enumeration
│   └── GitHubRepoItem.java              <-- Repository metadata model
├── services/
│   ├── AICommandService.java            <-- High-level async execution API & metrics
│   ├── AIHistoryManager.java            <-- Thread-safe JSON disk caching
│   └── GitHubWorkspaceManager.java      <-- Workspace detector & Codespace shortcuts
└── ui/
    └── AIWorkspaceBottomSheet.java      <-- Material 3 tabbed BottomSheetDialogFragment
```
