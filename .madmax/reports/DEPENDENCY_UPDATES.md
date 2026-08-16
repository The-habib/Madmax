# MadMax Dependency Updates & Evolution Strategy

**Audit Date:** 2026-08-16  
**Auditor:** MadMax Lead Engineer  
**Status:** 🟢 **UP-TO-DATE**

---

## 1. Core Library Upgrade Log

| Dependency | Upstream Baseline | MadMax Current | Status |
|---|---|---|:---:|
| `com.google.android.material:material` | `1.4.0` (M2) | `1.12.0` (Material 3) | 🟢 Upgraded & Stable |
| `androidx.appcompat:appcompat` | `1.3.1` | `1.7.0` | 🟢 Upgraded & Stable |
| `androidx.core:core` | `1.6.0` | `1.13.1` | 🟢 Upgraded & Stable |
| `androidx.preference:preference` | `1.1.1` | `1.2.1` | 🟢 Upgraded & Stable |
| `com.android.tools.build:gradle` (AGP) | `7.0.4` | `8.13.2` | 🟢 Upgraded & Stable |
| `Gradle Wrapper` | `7.4` | `9.2.1` | 🟢 Upgraded & Stable |
| `Java / OpenJDK` | JDK 11 | JDK 17 (Temurin) | 🟢 Upgraded & Stable |

---

## 2. Planned Dependencies for Future Milestones

* **Milestone 1.2 (Session Management):** `androidx.viewpager2:viewpager2:1.1.0` (Horizontal session tabs).
* **Phase 2 (AI Assistant):** `com.squareup.okhttp3:okhttp:4.12.0` / `com.google.ai.client.generativeai:generativeai` (Local & Cloud AI IPC bridge).
* **Phase 3 (Plugin System):** `androidx.room:room-runtime:2.6.1` (Plugin metadata cache).
