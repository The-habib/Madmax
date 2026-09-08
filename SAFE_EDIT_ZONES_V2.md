# MadMax Safe Edit Zones (Version 2.0)

This matrix defines the strict classification of every directory and file in the repository to guarantee upstream merge safety.

---

## 1. Safety Zone Topology

```mermaid
graph TD
    subgraph Green Zone: Safe to Modify
        EXT[app/src/main/java/com/termux/app/madmax/]
        SET[app/src/main/java/com/termux/app/activities/SettingsActivity.java]
        RES[app/src/main/res/xml/madmax_*.xml]
        CLR[app/src/main/res/values/colors_madmax.xml]
        DOC[.madmax/ & Documentation]
        CI[.github/workflows/]
    end

    subgraph Yellow Zone: Caution Required
        ACT[app/src/main/java/com/termux/app/TermuxActivity.java]
        APP_INIT[app/src/main/java/com/termux/app/TermuxApplication.java]
        MAN[app/src/main/AndroidManifest.xml]
        SESS[app/src/main/java/com/termux/app/terminal/TermuxSessionsListViewController.java]
    end

    subgraph Red Zone: Strictly Protected Core
        CORE[terminal-emulator/*]
        PTY[terminal-emulator/src/main/jni/termux.c]
        SOCK[termux-shared/src/main/cpp/local-socket.cpp]
        SDK[targetSdkVersion=28 in gradle.properties]
        BOOT[Bootstrap packages & extractors]
    end

    Green Zone -->|Extends & Modernizes| Yellow Zone
    Yellow Zone -->|Wraps & Calls| Red Zone
```

---

## 2. Comprehensive Directory Classification

| Path | Zone | Status / Action |
|---|:---:|---|
| `terminal-emulator/` | 🔴 **PROTECTED** | Never modify. 100% upstream byte match required. |
| `terminal-emulator/src/main/jni/termux.c` | 🔴 **PROTECTED** | Never modify. Low-level Linux PTY syscalls. |
| `termux-shared/src/main/cpp/local-socket.cpp` | 🔴 **PROTECTED** | Never modify. Unix abstract domain socket server. |
| `gradle.properties` (`targetSdkVersion=28`) | 🔴 **PROTECTED** | Never change. API 28 is required for Linux W^X execution. |
| `app/src/main/java/com/termux/app/madmax/` | 🟢 **SAFE** | Full innovation zone. AI, Plugins, Theming, Dev tools. |
| `app/src/main/res/values/colors_madmax.xml` | 🟢 **SAFE** | MadMax Material 3 color system and tokens. |
| `app/src/main/res/xml/madmax_*.xml` | 🟢 **SAFE** | Modular preference screens. |
| `.github/workflows/` | 🟢 **SAFE** | CI/CD automation and release pipelines. |
| `.madmax/` | 🟢 **SAFE** | Engineering reports and architectural blueprints. |
| `app/src/main/java/com/termux/app/TermuxActivity.java` | 🟡 **CAUTION** | UI hooks allowed; do not touch session client routing. |
| `app/src/main/java/com/termux/app/terminal/TermuxSessionsListViewController.java` | 🟡 **CAUTION** | View binding improvements allowed; preserve click/rename delegation. |
