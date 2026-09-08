# MadMax Engineering Rules & Core Standards

> **Mission:** Transform MadMax into a production-grade, highly customizable Android terminal workspace while keeping the official Termux terminal emulation engine pristine and 100% upstream-compatible.

---

## 1. Project Philosophy

1. **Upstream Compatibility Above All:** Every change must allow seamless rebases and cherry-picks from `termux/termux-app`.
2. **Modular Decoupling:** New capabilities (Material 3 UI, AI assistants, session managers) must be built as additive overlays or modular layers without mutating the core terminal state machine.
3. **Small, Reversible Changes:** Every feature must be decomposed into isolated, testable, and independently reviewable commits.
4. **Never Break POSIX Mechanics:** The terminal's primary function is a reliable Linux userland. Never sacrifice raw terminal throughput, escape code fidelity, or process signal correctness for UI eye candy.

---

## 2. Hard Non-Negotiable Rules

```
┌───────────────────────────────────────────────────────────────────────────────────┐
│ 🔴 NON-NEGOTIABLE HARD RULES                                                      │
├───────────────────────────────────────────────────────────────────────────────────┤
│ 1. NEVER modify anything inside `terminal-emulator/`.                            │
│ 2. NEVER modify `terminal-emulator/src/main/jni/termux.c`.                        │
│ 3. NEVER modify `termux-shared/src/main/cpp/local-socket.cpp`.                    │
│ 4. NEVER change `targetSdkVersion=28` in `gradle.properties`.                     │
│ 5. NEVER rewrite PTY management, VT100/ANSI parsing, or bootstrap extractors.    │
│ 6. NEVER work directly on `master`. Always work on `dev` or feature branches.     │
│ 7. NEVER commit broken builds or bypass CI lint checks.                           │
└───────────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Git Workflow & Branching Strategy

```
master (Production / Upstream Mirror)
  │
  └──► dev (Active Integration Branch)
        │
        ├──► feature/<feature-name>
        ├──► fix/<bug-fix-name>
        └──► refactor/<scope>
```

### 3.1 Branch Naming Conventions
* **Feature Branches:** `feature/<name>` (e.g. `feature/material3-theming`, `feature/session-drawer`)
* **Bugfix Branches:** `fix/<name>` (e.g. `fix/extrakeys-padding`, `fix/notification-icon`)
* **Refactor Branches:** `refactor/<name>` (e.g. `refactor/settings-ui`)
* **Release Branches:** `release/v<version>` (e.g. `release/v1.0.0`)

### 3.2 Commit Message Convention (Conventional Commits)
All commit messages must follow standard Conventional Commits syntax:

```
<type>(<optional scope>): <short description in present tense>

[optional body explaining rationale and approach]

[optional footer(s)]
```

**Types:**
* `feat`: A new user-facing feature
* `fix`: A bug fix
* `docs`: Documentation only changes
* `style`: Formatting, missing semi-colons, no code change
* `refactor`: Refactoring code without changing public behavior
* `perf`: Performance improvements
* `test`: Adding or correcting tests
* `chore`: Build scripts, dependencies, CI tooling

*Examples:*
* `feat(ui): add material3 dynamic color support to terminal drawer`
* `fix(settings): correct boolean toggle persistence in theme settings`
* `docs(readme): update build instructions for Android SDK 36`

---

## 4. Coding Standards

### 4.1 Java Code Standards
* **Java Version:** Java 1.8 compatibility (with desugaring enabled).
* **Null Safety:** Use `androidx.annotation.NonNull` and `androidx.annotation.Nullable` on all method parameters and return types.
* **Logging:** Always use `com.termux.shared.logger.Logger` with appropriate tag constants. Never use raw `android.util.Log` or `System.out.println`.
* **String Formatting:** Avoid string concatenation in logging loops; use parameterized log methods.
* **Naming Conventions:**
  * Member fields: `mVariableName`
  * Static variables: `sVariableName`
  * Constants: `UPPER_SNAKE_CASE`
  * Methods and local variables: `lowerCamelCase`

### 4.2 XML Standards
* **Resource IDs:** Descriptive snake_case naming prefixed by component type (e.g. `button_new_session`, `view_pager_extra_keys`).
* **Dimensions & Strings:** Never hardcode px values or raw user-facing strings; declare them in `res/values/dimens.xml` and `res/values/strings.xml`.

---

## 5. Upstream Synchronization Workflow

To pull new upstream features and bug fixes from official Termux:

```bash
# 1. Ensure working directory is clean
git status

# 2. Fetch latest upstream commits
git fetch upstream master

# 3. Checkout dev branch
git checkout dev

# 4. Rebase dev on upstream/master
git rebase upstream/master

# 5. Resolve any non-core conflicts (preserve MadMax UI extensions)
# 6. Verify build and run tests
./gradlew testDebugUnitTest

# 7. Push updated dev branch
git push origin dev --force-with-lease
```

---

## 6. Pull Request (PR) Checklist

Before submitting or merging any PR:
- [ ] Working tree builds cleanly (`./gradlew assembleDebug`).
- [ ] Unit tests pass (`./gradlew testDebugUnitTest`).
- [ ] No files in `terminal-emulator/` or protected C/C++ files were touched.
- [ ] `targetSdkVersion=28` is intact.
- [ ] Proper Conventional Commit messages are used.
- [ ] Documentation has been updated for any user-visible changes.
