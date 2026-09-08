# MadMax Git Branching Strategy & Upstream Alignment

MadMax maintains a dual-branch topology to ensure rapid feature innovation while preserving 100% clean merge compatibility with upstream Termux.

---

## 1. Branch Topology

```mermaid
graph TD
    subgraph Upstream
        UM[upstream/master: Pristine Termux]
    end

    subgraph Origin Repository
        OM[origin/master: Exact mirror of upstream/master]
        OD[origin/dev: Active MadMax Integration]
        F1[feature/material-you]
        F2[feature/session-drawer]
        F3[feature/developer-dashboard]
    end

    UM -->|Periodic git merge/rebase| OM
    OM -->|Merge upstream improvements| OD
    OD -->|Branch out| F1
    OD -->|Branch out| F2
    OD -->|Branch out| F3
    F1 -->|PR Merge| OD
    F2 -->|PR Merge| OD
    F3 -->|PR Merge| OD
```

---

## 2. Branch Roles & Rules

| Branch | Role | Direct Commits Allowed? | PR Target? |
|---|---|:---:|:---:|
| **`master`** | Pristine upstream mirror of `termux/termux-app`. | ❌ Never (Fast-forward only from upstream) | ❌ Never |
| **`dev`** | Integration branch for all tested MadMax features. | ⚠️ Maintainers only | ✅ Default target |
| **`feature/*`** | Feature-specific development branches. | ✅ Yes | ➡️ Targets `dev` |
| **`fix/*`** | Bug fix branches. | ✅ Yes | ➡️ Targets `dev` |

---

## 3. Upstream Synchronization Routine

To sync upstream updates cleanly:
```bash
git checkout master
git fetch upstream
git merge --ff-only upstream/master
git push origin master

git checkout dev
git merge master
# Resolve any non-core presentation conflicts if needed
git push origin dev
```
