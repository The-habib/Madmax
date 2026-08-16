# MadMax Command Intelligence Engine

The Command Intelligence engine provides three primary developer utilities: **Explain Command**, **Generate Command**, and **Diagnose Error**.

---

## 1. Explain Command

Decomposes complex shell commands into human-readable components with real-time risk assessment:
- **Binary Identification:** Identifies tool and displays core summary.
- **Flag Extraction:** Deconstructs combined flags (e.g. `-czvf` $\rightarrow$ `-c`, `-z`, `-v`, `-f`).
- **Risk Assessment Levels:**
  - `SAFE` (Green): Standard non-destructive operations (`ls`, `grep`, `df`).
  - `CAUTION` (Yellow): Operations modifying files or network states (`chmod`, `rm`, `sed -i`, `kill`).
  - `DESTRUCTIVE` (Red): Hazardous commands (`rm -rf /`, fork bombs, `mkfs`, `dd if=/dev/zero`).

---

## 2. Generate Command

Translates plain English prompts into standard, production-grade bash commands:
- "find all large files over 100mb" $\rightarrow$ `find . -type f -size +100M -exec ls -lh {} + | sort -k 5 -hr`
- "kill process on port 8080" $\rightarrow$ `kill -9 $(lsof -t -i:8080)`
- "extract tar.gz" $\rightarrow$ `tar -xzvf archive.tar.gz`
- "reverse ssh tunnel" $\rightarrow$ `ssh -L 8080:localhost:8080 -N -f user@remote_server`
- "undo last git commit" $\rightarrow$ `git reset --soft HEAD~1`

---

## 3. Diagnose Error

Automated classification of terminal stderr outputs:
- **`COMMAND_NOT_FOUND`:** Suggests exact `pkg install <package>` package mapping.
- **`PERMISSION_DENIED`:** Suggests `chmod +x <file>` or `termux-setup-storage`.
- **`PACKAGE_LOCK`:** Detects held dpkg lock and provides recovery command.
- **`PORT_CONFLICT`:** Detects occupied port and provides process termination snippet.
- **`DEPENDENCY_MISSING`:** Detects Python (`pip install`) and Node (`npm install`) missing modules.
