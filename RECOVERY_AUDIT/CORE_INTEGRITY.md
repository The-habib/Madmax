# MadMax Protected Core Integrity Audit

**Execution Date:** 2026-09-08  
**Audit Scope:** Independent verification of protected low-level components against upstream Termux base (`3df69d1da197dd9bd71a3bafd902dffd720576b4`).

---

## 1. Executive Summary

A cryptographic and structural diff audit was performed across all protected core subsystems of MadMax. 

**Result: 100% CLEAN & UNTOUCHED.**  
Zero lines of code were modified, added, or deleted inside the protected core boundaries. The terminal emulation engine, native PTY subsystem, local domain socket server, bootstrap extractor, and security execution permissions remain strictly identical to upstream Termux.

---

## 2. Protected Subsystems Audit Matrix

| Protected Subsystem | Scope / File Path | Upstream Base SHA | MadMax Status | Diff Lines |
|---|---|---|---|---|
| **Terminal Emulator Engine** | `terminal-emulator/src/main/java/` | `3df69d1` | 🟢 **UNCHANGED** | 0 |
| **Native PTY Syscall Wrapper** | `terminal-emulator/src/main/jni/termux.c` | `3df69d1` | 🟢 **UNCHANGED** | 0 |
| **Local Domain Socket Server** | `termux-shared/src/main/cpp/local-socket.cpp` | `3df69d1` | 🟢 **UNCHANGED** | 0 |
| **Bootstrap Extractor** | `app/src/main/java/com/termux/app/TermuxInstaller.java` | `3df69d1` | 🟢 **UNCHANGED** | 0 |
| **Bootstrap Download Config** | `app/build.gradle` (`downloadBootstraps` task) | `3df69d1` | 🟢 **UNCHANGED** | 0 |
| **Target SDK Version (W^X)** | `gradle.properties` (`targetSdkVersion=28`) | `3df69d1` | 🟢 **UNCHANGED** | 0 |
| **Terminal View Canvas** | `terminal-view/src/main/java/` | `3df69d1` | 🟢 **UNCHANGED** | 0 |
| **Native C/C++ Build Config** | `Android.mk` / `Application.mk` files | `3df69d1` | 🟢 **UNCHANGED** | 0 |

---

## 3. Subsystem Detailed Analysis

### 3.1 `terminal-emulator/` Engine
* **Command Executed:** `git diff 3df69d1..dev -- terminal-emulator/`
* **Result:** Empty diff (`0 files changed`).
* **Verification:** The VT100/ANSI state machine in `TerminalEmulator.java`, screen buffer operations in `TerminalBuffer.java`, row storage in `TerminalRow.java`, transcript extraction in `TerminalScreen.java`, and process lifecycle management in `TerminalSession.java` are byte-for-byte identical to upstream.

### 3.2 Native PTY Syscalls (`termux.c`)
* **Path:** `terminal-emulator/src/main/jni/termux.c`
* **Result:** Identical.
* **Verification:** Low-level POSIX functions (`openpty`, `fork`, `execvp`, `setsid`, `dup2`, `tcsetattr`, `ioctl` for `TIOCSWINSZ`) remain unmutated. No risks of signal leakage, broken pseudo-terminal file descriptors, or zombie processes.

### 3.3 Local Domain Socket Server (`local-socket.cpp`)
* **Path:** `termux-shared/src/main/cpp/local-socket.cpp`
* **Result:** Identical.
* **Verification:** Native Unix domain socket server used by `termux-am` for inter-process communication between Linux CLI userland scripts and Android system services is unmodified.

### 3.4 Bootstrap Extraction & Package Integrity
* **Path:** `app/src/main/java/com/termux/app/TermuxInstaller.java`
* **Verification:**
  - Bootstrap extraction algorithm, permissions (`chmod 700`), symlink parsing, and zip extraction are pristine.
  - Gradle task `downloadBootstraps` downloads verified upstream rootfs archives (`bootstrap-2026.02.12-r1+apt.android-7`) with SHA-256 verification.

### 3.5 Target SDK Version Security Boundary
* **Setting:** `targetSdkVersion=28` in [`gradle.properties`](file:///home/runner/workspace/gradle.properties)
* **Status:** Confirmed `28`.
* **Rationale:** Android 10+ (API 29+) restricts execution of binaries located in writable application storage (`W^X` enforcement). By strictly preserving `targetSdkVersion=28`, MadMax retains the ability for userland packages in `$PREFIX/bin` to execute without SELinux denial crashes.

---

## 4. Conclusion

MadMax adheres strictly to its primary engineering principle:
> **KEEP THE TERMUX TERMINAL ENGINE STABLE.**

All UI modernization, Material 3 theming, developer tools, and AI intelligence features have been built outside of and on top of this protected core boundary.
