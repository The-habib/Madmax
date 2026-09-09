# Changelog

All notable changes to the **MadMax** project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.5.0] - 2026-09-09

### Added
- **Complete End-to-End Application Name Rebrand**: Updated user-facing application name and labels from `Termux` to `MadMax` across all application manifests, `strings.xml`, `TermuxConstants`, notification channels, and build configurations.
- **Terminal Prompt Launcher Assets**: Integrated new minimalist `> _` brand logo across all 5 Android density buckets (`mdpi`, `hdpi`, `xhdpi`, `xxhdpi`, `xxxhdpi`) for both square and circular launcher styles.
- **Adaptive Vector Icon Refresh**: Updated adaptive icon layers with pure white backgrounds and precision-aligned black prompt vectors with full Android 13+ Material You monochrome themed icon support.
- **Android TV & Master Artwork Refresh**: Replaced TV banner (320x180) and repository vector assets in `art/` with the new MadMax prompt branding.
- **Drawer Header Logo Integration**: Embedded a Material 3 rounded card brand logo container directly inside the session navigation drawer header.

---

## [1.1.0-dev] - 2026-08-16

### Added
- **MadMax Extension Layer (`com.termux.app.madmax.*`)**: Decoupled subsystem coordinator initialized at app startup.
- **Dynamic Feature Registry (`MadMaxFeatureRegistry`)**: Thread-safe feature toggling with `SharedPreferences` persistence and live listener callbacks.
- **Material Design 3 Upgrade**: Presentation styles modernized with `Theme.Material3.DayNight.NoActionBar`.
- **Monet Dynamic Color Engine**: Automatic wallpaper color extraction on Android 12+ devices with fallback color tokens (`colors_madmax.xml`).
- **Modern Session Navigation Drawer**: 280dp navigation drawer with live process status badges, session command subtitles, and quick action header icons.
- **Material 3 Settings Hub**: Categorized preferences for Appearance, AI Assistant (Phase 2 preview), Plugins (Phase 3 preview), GitHub Workspace (Phase 2 preview), Developer Tools, and Core Termux configurations.
- **Developer Diagnostics Dashboard (`DeveloperDashboardActivity`)**: Real-time hardware, CPU ABI (`arm64-v8a`, `x86_64`), Target SDK 28 W^X mode verification, memory inspection, and one-tap markdown clipboard export.
- **GitHub Actions CI/CD Pipeline (`ci.yml`, `release.yml`)**: Automated multi-ABI APK building, unit testing, and GitHub release generation on tags.
- **Complete Documentation Suite**: Architectural blueprints, contribution guidelines, release process, and safe edit maps.

### Preserved (Core Engine Integrity)
- **100% Core Protection**: Zero modifications to `terminal-emulator/`, `termux.c`, `local-socket.cpp`, or `targetSdkVersion=28`.
