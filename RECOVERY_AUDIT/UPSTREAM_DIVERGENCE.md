# MadMax Upstream Divergence Analysis

**Execution Date:** 2026-09-08  
**Audit Scope:** Comparison of MadMax repository with official upstream `termux/termux-app.git`.

---

## 1. Upstream Topology & Common Ancestor

* **Official Upstream Repository:** [`https://github.com/termux/termux-app.git`](https://github.com/termux/termux-app.git)
* **MadMax Fork Point (Common Ancestor):** `3df69d1da197dd9bd71a3bafd902dffd720576b4`  
  - Commit message: `Revert: Add Warp sponsors logo`
  - Author: `agnostic-apollo <agnosticapollo@gmail.com>`
  - Date: `2026-07-15 01:47:01 +0500`

---

## 2. Upstream Commits Missing from MadMax

Between common ancestor `3df69d1` and current upstream `upstream/master`, there is **exactly 1 commit**:

### Commit `3b66f8799635a4dba4a206563048ff0e6792c487`
* **Date:** 2026-08-24 01:50:37 +0500
* **Author:** `agnostic-apollo <agnosticapollo@gmail.com>`
* **Subject:** `Security(RunCommandService): Do not send result back to any file based result config before allow-external-app property has been checked to be true`
* **Diffstat:**
  ```
  app/src/main/java/com/termux/app/RunCommandService.java | 29 +++++++++++++++++++----
  1 file changed, 21 insertions(+), 8 deletions(-)
  ```
* **Security Context:** Addresses a vulnerability where an unauthorized external application could trigger `RunCommandService` with a file-based result config pointing to `termux.properties` (e.g. attempting to force `allow-external-apps=true`), and `RunCommandService` previously wrote the error result to the destination file before validating the property.

### Conflict Assessment for `3b66f87`
* Across all 11 MadMax commits on `origin/dev`, **[`RunCommandService.java`](file:///home/runner/workspace/app/src/main/java/com/termux/app/RunCommandService.java) was completely untouched**.
* A 3-way merge or cherry-pick of `3b66f87` onto `dev` will apply **cleanly with zero merge conflicts**.

---

## 3. MadMax Commits Ahead of Upstream

MadMax has **11 commits** on branch `dev` ahead of upstream base `3df69d1`:

1. `d2aa498` - `chore(workspace): initialize MadMax engineering foundation and toolchain`
2. `80ae781` - `feat(madmax): implement extension layer and dynamic feature registry`
3. `efbfc34` - `feat(theme): upgrade to Material 3 with Monet dynamic color tokens`
4. `833984a` - `feat(ui): redesign session drawer with Material 3 cards and branding`
5. `c90bff1` - `feat(settings): build Material 3 settings hub, developer dashboard, and diagnostic tools`
6. `8a4be13` - `ci: establish GitHub Actions CI/CD pipeline and release automation`
7. `ccdd9d4` - `docs(engineering): add comprehensive developer guides, architecture specs, and audits`
8. `f0b9ad9` - `docs(audit): complete independent release audit and update CI actions to v5`
9. `f14c5c7` - `feat(ai): implement AI Workspace architecture, Command Intelligence engine, and error analyzer`
10. `e4cc1fd` - `docs(ai): add comprehensive AI Workspace architecture blueprints and executive reports`
11. `714298f` - `ci: make wrapper validation resilient to external network timeouts`

---

## 4. Architectural Divergence Evaluation

The architectural divergence between MadMax and Termux app is characterized as **strictly modular and additive**:

1. **No Core Modification:** None of the core terminal emulation engine files (`terminal-emulator/`, `terminal-view/`, `termux.c`, `local-socket.cpp`) have been altered.
2. **Decoupled Extension Layer:** All new features live under `com.termux.app.madmax.*`, isolated from upstream Termux packages.
3. **App-Level Integration Points:** Only 3 existing Java source files in `app/` were modified to hook into MadMax:
   - `TermuxApplication.java`: Added 2 lines to call `MadMaxExtensionManager.init(this)`.
   - `TermuxActivity.java`: Added button listeners to launch the Developer Dashboard and AI Workspace Bottom Sheet.
   - `SettingsActivity.java`: Added a link to the MadMax GitHub repository.
4. **Theme & Layout Resources:** Material 3 themes and updated layouts are defined in standard Android resource XMLs (`activity_termux.xml`, `item_terminal_sessions_list.xml`, `themes.xml`), preserving upstream view IDs (`terminal_view`, `drawer_layout`, etc.).

---

## 5. Synchronization Recommendation

* **Immediate Action:** Do **not** merge or rebase immediately during this recovery phase. Upstream sync must be an explicit, independent operation executed after the baseline is stabilized.
* **Future Upstream Sync Plan:**
  - Create a temporary integration branch `sync/upstream-security-fix`.
  - Cherry-pick `3b66f8799635a4dba4a206563048ff0e6792c487` onto `dev`.
  - Validate with full unit test suite and APK compilation.
  - Merge cleanly into `dev`.
