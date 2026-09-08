# MadMax Android SDK Platform & Tooling Report

**Audit Date:** 2026-08-16  
**Auditor:** MadMax Lead Engineer  
**Status:** 🟢 **VERIFIED (API 36 Compile / API 28 Target)**

---

## 1. SDK Configuration Overview

| Property | Value | Purpose / Rationale |
|---|---|---|
| **`compileSdkVersion`** | `36` | Access to latest Android 15/16 platform APIs, Material 3 components, and compiler optimizations. |
| **`targetSdkVersion`** | `28` | **CRITICAL PROTECTION:** Android 10+ (API 29+) enforces `W^X` policy blocking execution of binaries from writable app private directories. API 28 allows native package binaries to execute in `$PREFIX/bin`. |
| **`minSdkVersion`** | `21` | Supports Android 5.0 (Lollipop) through Android 15+. |
| **`buildToolsVersion`** | `35.0.0` | AAPT2 resource compiler and D8/R8 dexers. |
| **`ndkVersion`** | `29.0.14206865` | Latest LLVM/Clang NDK toolchain. |

---

## 2. Installed SDK Packages (`sdkmanager`)

```
platforms;android-36
build-tools;35.0.0
cmdline-tools;latest (11076708)
platform-tools
ndk;29.0.14206865
```

---

## 3. Verdict: 🟢 FULLY COMPLIANT
The SDK environment maintains backward compatibility while enabling cutting-edge Material 3 theming.
