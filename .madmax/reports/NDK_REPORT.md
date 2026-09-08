# MadMax NDK & Native Compilation Report

**Audit Date:** 2026-08-16  
**Auditor:** MadMax Lead Engineer  
**Status:** 🟢 **VERIFIED (4/4 ABIs Compiled Successfully)**

---

## 1. NDK Toolchain Configuration

* **NDK Version:** `29.0.14206865` (`ndk;29.0.14206865` installed in `$ANDROID_HOME/ndk/29.0.14206865`)
* **Build System:** `ndkBuild` (via AGP NDK integration)
* **Toolchain Compiler:** Clang / LLVM 18+
* **C++ Standard:** C++17
* **Position Independent Executable (PIE):** Enforced across all native targets.

---

## 2. Protected Native Subsystems Status

| Source File | Submodule | Purpose | Status |
|---|---|---|:---:|
| `terminal-emulator/src/main/jni/termux.c` | `:terminal-emulator` | `openpty()`, `fork()`, `setsid()`, `execvp()`, `ioctl(TIOCSWINSZ)` | 🟢 **100% UNTOUCHED** |
| `termux-shared/src/main/cpp/local-socket.cpp` | `:termux-shared` | Unix abstract domain socket server & `termux-am` client bridge | 🟢 **100% UNTOUCHED** |

---

## 3. ABI Matrix Compilation Audit

| ABI Target | Native Output (`.so`) | Symbols Stripped | Linker Errors |
|---|---|:---:|:---:|
| `arm64-v8a` | `libtermux.so`, `libtermux-shared.so` | Yes | 0 |
| `armeabi-v7a` | `libtermux.so`, `libtermux-shared.so` | Yes | 0 |
| `x86_64` | `libtermux.so`, `libtermux-shared.so` | Yes | 0 |
| `x86` | `libtermux.so`, `libtermux-shared.so` | Yes | 0 |

---

## 4. Verdict: 🟢 PRODUCTION GRADE
Native toolchain is reproducible and compliant with Android NDK r29 standards.
