# MadMax Autonomous Agent Workflow

This document establishes the protocol for autonomous AI development agents operating on the MadMax repository.

---

## 1. Operating Principles

1. **Verify Before Action:** Inspect the current environment, active branch, and working tree before making modifications.
2. **Execute, Don't Guess:** Use command tools to verify directory contents, build outputs, and test results rather than assuming state.
3. **Preserve Upstream Parity:** Keep changes modular so that `git rebase upstream/master` runs without merge conflicts in the core terminal engine.
4. **Transparent Documentation:** Every architectural addition or feature must be documented in `.madmax/` and accompanied by a concise report.

---

## 2. Standard Task Execution Loop

```
  ┌─────────────────────────────────────────────────────────────┐
  │ 1. INITIALIZE & INSPECT                                     │
  │    - git status && git branch                               │
  │    - Ensure on dev or feature/* branch                      │
  └──────────────────────────────┬──────────────────────────────┘
                                 │
                                 ▼
  ┌─────────────────────────────────────────────────────────────┐
  │ 2. SAFETY CHECK                                             │
  │    - Cross-reference target files with SAFE_EDIT_ZONES.md   │
  │    - Verify target is SAFE or CAUTION                       │
  └──────────────────────────────┬──────────────────────────────┘
                                 │
                                 ▼
  ┌─────────────────────────────────────────────────────────────┐
  │ 3. INCREMENTAL IMPLEMENTATION                               │
  │    - Apply changes in focused, modular steps                │
  │    - Preserve code comments and formatting                  │
  └──────────────────────────────┬──────────────────────────────┘
                                 │
                                 ▼
  ┌─────────────────────────────────────────────────────────────┐
  │ 4. VERIFY & TEST                                            │
  │    - Run `./gradlew assembleDebug`                          │
  │    - Run `./gradlew testDebugUnitTest`                      │
  └──────────────────────────────┬──────────────────────────────┘
                                 │
                                 ▼
  ┌─────────────────────────────────────────────────────────────┐
  │ 5. COMMIT & DOCUMENT                                        │
  │    - Atomic git commit with Conventional Commits message    │
  │    - Update documentation in `.madmax/`                     │
  └─────────────────────────────────────────────────────────────┘
```

---

## 3. Handling Protected Code Boundaries

If a task appears to require modifying a PROTECTED file (e.g. `TerminalEmulator.java` or `termux.c`):
1. **STOP:** Do not modify the protected file.
2. **ADAPT:** Re-architect the solution using an event listener, wrapper class, view client hook, or custom Intent broadcast.
3. **DOCUMENT:** Explain the extension point created in `.madmax/ARCHITECTURE.md`.
