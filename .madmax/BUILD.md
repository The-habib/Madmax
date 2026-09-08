# MadMax Build & Environment Guide

This document contains complete, verified instructions for setting up the build environment and compiling MadMax.

---

## 1. Prerequisites & Toolchain Requirements

| Component | Verified Version | Purpose |
|---|---|---|
| **Host Operating System** | Linux (Ubuntu 22.04+ / Debian 12+) or macOS | Host OS |
| **Java Development Kit** | OpenJDK 17 or 21 (Temurin / GraalVM) | Build execution |
| **Gradle Wrapper** | 9.2.1 (Bundled via `./gradlew`) | Build orchestrator |
| **Android Gradle Plugin** | 8.13.2 | Android build pipeline |
| **Android SDK Platform** | API 36 (`platforms;android-36`) | Compile SDK |
| **Android SDK Build-Tools** | 35.0.0 | APK packaging and AAPT2 |
| **Android NDK** | `29.0.14206865` (`ndk;29.0.14206865`) | Native C/C++ compilation via `ndkBuild` |

---

## 2. Environment Configuration

### 2.1 Set Environment Variables
```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64  # Or current JDK path
export ANDROID_HOME=/home/runner/android-sdk         # Path to Android SDK
export ANDROID_SDK_ROOT=$ANDROID_HOME
export PATH=$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH
```

### 2.2 Configure `local.properties`
Create `local.properties` in the project root:
```properties
sdk.dir=/home/runner/android-sdk
```

---

## 3. Command-Line Build Instructions

### 3.1 Download Bootstraps (Automatic)
The build automatically triggers `downloadBootstraps` during compilation, fetching rootfs archives (`bootstrap-2026.02.12-r1+apt.android-7`).

### 3.2 Assemble Debug APKs
To compile debug APKs for all architectures:
```bash
./gradlew assembleDebug
```
Compiled outputs are placed in:
`app/build/outputs/apk/debug/`
- `termux-app_apt-android-7-debug_universal.apk` (~121 MB)
- `termux-app_apt-android-7-debug_arm64-v8a.apk` (~38 MB)
- `termux-app_apt-android-7-debug_x86_64.apk` (~38 MB)
- `termux-app_apt-android-7-debug_x86.apk` (~37 MB)
- `termux-app_apt-android-7-debug_armeabi-v7a.apk` (~35 MB)

### 3.3 Running Unit Tests
To run all MadMax unit tests:
```bash
./gradlew :app:testDebugUnitTest --tests "com.termux.app.madmax.*"
```

To run all terminal emulator engine unit tests:
```bash
./gradlew :terminal-emulator:testDebugUnitTest
```

To run the complete test suite:
```bash
./gradlew testDebugUnitTest
```

---

## 4. Troubleshooting Common Issues

### Issue 1: `SDK location not found`
* **Fix:** Define `ANDROID_HOME` or set `sdk.dir` in `local.properties`.

### Issue 2: `Robolectric ShadowActivityThread NPE under Java 19+`
* **Diagnosis:** Robolectric reflection incompatibility when resetting ActivityThread under JDK 19+.
* **Fix:** Run tests with OpenJDK 17 (e.g. Temurin 17), which passes cleanly in GitHub Actions CI.
