# MadMax Build & Environment Guide

This document contains complete, reproducible instructions for setting up the build environment and compiling MadMax.

---

## 1. Prerequisites & Toolchain Requirements

| Component | Required Version | Purpose |
|---|---|---|
| **Operating System** | Linux (Ubuntu 22.04+ / Debian 12+) or macOS | Host OS |
| **Java Development Kit** | OpenJDK 17 or 21 | Build execution (required by Gradle 9+) |
| **Gradle Wrapper** | 9.2.1 (Bundled via `./gradlew`) | Build orchestrator |
| **Android Gradle Plugin** | 8.13.2 | Android build pipeline |
| **Android SDK Platform** | API 36 (`platforms;android-36`) | Compile SDK |
| **Android SDK Build-Tools** | 35.0.0+ | APK packaging and AAPT2 |
| **Android NDK** | `29.0.14206865` (`ndk;29.0.14206865`) | Native C/C++ compilation via `ndkBuild` |
| **CMake & Ninja** | 3.28+ & 1.11+ | Native toolchain support |

---

## 2. Environment Configuration

Set the required environment variables in your shell profile (e.g. `~/.bashrc` or `/etc/profile.d/android_env.sh`):

```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export ANDROID_HOME=/home/codespace/android-sdk
export ANDROID_SDK_ROOT=/home/codespace/android-sdk
export PATH=$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH
```

Create `local.properties` in the project root if not already present:

```properties
sdk.dir=/home/codespace/android-sdk
ndk.dir=/home/codespace/android-sdk/ndk/29.0.14206865
```

---

## 3. Command-Line Build Instructions

### 3.1 Download & Cache Pre-built Bootstraps
The Gradle build automatically executes `downloadBootstraps` during compilation, but you can trigger it manually:

```bash
./gradlew downloadBootstraps
```

### 3.2 Compiling Debug APKs
Build debug APKs (generates universal and ABI-specific APKs):

```bash
./gradlew assembleDebug
```

Outputs are written to:
`app/build/outputs/apk/debug/termux-app_apt-android-7-debug_universal.apk`

### 3.3 Compiling Release APKs
```bash
./gradlew assembleRelease
```

### 3.4 Running Unit Tests
```bash
./gradlew testDebugUnitTest
```

---

## 4. Troubleshooting Common Build Issues

### Issue 1: `SDK location not found`
* **Fix:** Define `ANDROID_HOME` or set `sdk.dir` in `local.properties`.

### Issue 2: `Gradle requires JVM 17 or later`
* **Fix:** Verify `java -version` returns Java 17+ and ensure `JAVA_HOME` points to JDK 17+.

### Issue 3: `NDK not configured`
* **Fix:** Install `ndk;29.0.14206865` via `sdkmanager` or ensure `ndkVersion = 29.0.14206865` matches the installed NDK folder.
