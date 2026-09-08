## Description
<!-- Provide a clear description of the proposed changes. -->

## Associated Milestone
- [ ] Milestone 1.1 — Extension Layer & Material 3 Foundation
- [ ] Milestone 1.2 — Session Manager & UX
- [ ] Phase 2 — AI Assistant / GitHub Integration
- [ ] Phase 3 — Plugin Ecosystem & Cloud Tools
- [ ] Bug Fix / Documentation

---

## 🔒 Hard Rule Compliance Checklist (Mandatory)

Please confirm that this pull request complies with all MadMax core protection constraints:

- [ ] **No changes to `terminal-emulator/`**: The VT100 / ANSI parser and character cell buffer remain pristine.
- [ ] **No changes to `termux.c`**: Native PTY syscalls (`openpty`, `fork`, `execvp`) remain unmodified.
- [ ] **No changes to `local-socket.cpp`**: Socket server implementation remains unmodified.
- [ ] **`targetSdkVersion=28` preserved**: The app preserves API 28 in `gradle.properties` (W^X storage execution constraint).
- [ ] **Clean App Layer Separation**: All new functionality resides inside the MadMax extension layer (`com.termux.app.madmax.*`).
- [ ] **Tested Locally**: `./gradlew testDebugUnitTest` and `./gradlew assembleDebug` run without errors.
- [ ] **Conventional Commit format**: Commits follow `feat:`, `fix:`, `chore:`, `docs:`, `refactor:`, `test:`.

---

## Type of Change
- [ ] New feature (non-breaking change adding functionality)
- [ ] Bug fix (non-breaking change fixing an issue)
- [ ] Documentation update
- [ ] CI / CD / Build configuration

---

## Verification & Screenshots
<!-- Attach test logs, APK build confirmation, or UI screenshots if applicable. -->
