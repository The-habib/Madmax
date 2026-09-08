# MadMax — Next-Gen Android Terminal & Linux Environment

[![MadMax CI/CD](https://github.com/The-habib/Madmax/actions/workflows/ci.yml/badge.svg)](https://github.com/The-habib/Madmax/actions)
[![MadMax Version](https://img.shields.io/badge/version-1.1.0--dev-red.svg)](https://github.com/The-habib/Madmax/releases)
[![Target SDK](https://img.shields.io/badge/targetSdkVersion-28-brightgreen.svg)](gradle.properties)
[![Android](https://img.shields.io/badge/Android-5.0%20to%2015+-blue.svg)](https://developer.android.com)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE.md)
[![Material 3](https://img.shields.io/badge/UI-Material%203%20%2B%20Monet-ff3b30.svg)](https://m3.material.io)

**MadMax** is a production-grade, extensible fork of [Termux](https://github.com/termux/termux-app). It combines the rock-solid, pristine Termux terminal emulation engine with a modern **Material Design 3** visual foundation, **Android 12+ Monet dynamic theming**, an extensible **Feature Flag Registry**, a categorized **Settings Hub**, and a built-in **Developer Diagnostics Dashboard**.

---

## ⚡ Key Highlights

- 🎨 **Material 3 Foundation:** Upgraded presentation layer featuring `Theme.Material3.DayNight.NoActionBar`, 12dp rounded cards, tonal buttons, and pure OLED black terminal surfaces.
- 🌈 **Monet Dynamic Colors:** Automatic wallpaper-based color extraction on Android 12+ devices with curated fallback palettes.
- 📑 **Redesigned Session Drawer:** 280dp navigation drawer with live process status badges, session command titles, and quick action headers.
- ⚙️ **Modular Settings Hub:** Categorized preferences for Appearance, AI Assistant (Phase 2), Plugins (Phase 3), GitHub Workspace (Phase 2), Developer Tools, and Core Termux configurations.
- 🔍 **Developer Diagnostics Dashboard:** Hardware, CPU architecture (ABI), SDK 28 W^X mode verification, memory metrics, and one-tap diagnostic reporting.
- 🛡️ **100% Core Protection:** The ANSI/VT100 parser, PTY syscalls (`termux.c`), Unix socket server (`local-socket.cpp`), and bootstrap packages remain strictly untouched and 100% upstream-compatible.

---

## 🏗️ Architecture & Quick Links

- [**`.madmax/ROADMAP.md`**](.madmax/ROADMAP.md) — Multi-phase product roadmap (AI, Plugins, GitHub CLI, Cloud Launchers).
- [**`.madmax/ARCHITECTURE.md`**](.madmax/ARCHITECTURE.md) — System architecture, component relationships, and PTY data pipelines.
- [**`.madmax/RULES.md`**](.madmax/RULES.md) — Non-negotiable engineering rules and protected zones.
- [**`.madmax/BUILD.md`**](.madmax/BUILD.md) — Reproducible local & CI build instructions.
- [**`CONTRIBUTING.md`**](CONTRIBUTING.md) — Contribution guidelines and safe edit map.
- [**`SECURITY.md`**](SECURITY.md) — Security policy and vulnerability disclosure.

---

## 🛠️ Building From Source

### Prerequisites
- JDK 17 (`openjdk-17-jdk`)
- Android SDK (API 36, Build-Tools 35.0.0)
- Android NDK (`29.0.14206865`)

### Build Commands
```bash
# Set SDK location in local.properties
echo "sdk.dir=$ANDROID_HOME" > local.properties

# Run Unit Tests
./gradlew testDebugUnitTest

# Assemble all Debug APKs
./gradlew assembleDebug
```

Generated APKs will be located in `app/build/outputs/apk/debug/`:
- `termux-app_apt-android-7-debug_universal.apk`
- `termux-app_apt-android-7-debug_arm64-v8a.apk`
- `termux-app_apt-android-7-debug_armeabi-v7a.apk`
- `termux-app_apt-android-7-debug_x86_64.apk`
- `termux-app_apt-android-7-debug_x86.apk`

---

## 📦 Termux Upstream Compatibility

MadMax is designed from day one to preserve seamless upstream synchronization with `termux/termux-app`. All new features live cleanly inside `app/src/main/java/com/termux/app/madmax/` without modifying core emulator internals.

---

## 📄 License

MadMax is licensed under the **GNU General Public License v3.0** (GPLv3). See [LICENSE.md](LICENSE.md) for full license details.
