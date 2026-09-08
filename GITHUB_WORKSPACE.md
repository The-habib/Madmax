# MadMax GitHub Workspace Hub

The GitHub Workspace Hub connects MadMax with your GitHub developer workflow directly inside the terminal app.

---

## 1. Features & Capabilities

- **Repository Directory:** Curated list of popular and active repositories with description, star counts, and default branch badges.
- **1-Tap Terminal Clone:** Automatically generates and executes `git clone <url>` directly in your active terminal session.
- **GitHub Codespaces Launcher:** 1-tap browser intent launching cloud developer environments with full containerized toolchains.
- **Local Git Tracker:** Scans current terminal working directory to extract active branch and recent commit SHA.

---

## 2. Integrated Workflow

```mermaid
sequenceDiagram
    autonumber
    actor User
    participant Sheet as AI Workspace (GitHub Tab)
    participant Manager as GitHubWorkspaceManager
    participant Term as TerminalSession
    participant Web as Android Browser

    User->>Sheet: Tap 'GitHub' tab
    Sheet->>Manager: Query repository items & local git state
    Manager-->>Sheet: Display repo cards & branch badge
    alt Clone in Termux
        User->>Sheet: Tap 'Clone in Termux'
        Sheet->>Term: Execute `git clone <repo_url>`
        Sheet->>Sheet: Dismiss bottom sheet
    else Launch Codespace
        User->>Sheet: Tap 'Codespace'
        Sheet->>Web: Launch `https://github.com/codespaces/new?repo=<name>`
    end
```
