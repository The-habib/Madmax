# Security Policy

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 1.1.x   | :white_check_mark: |
| 1.0.x   | :x:                |

---

## Reporting a Vulnerability

The MadMax engineering team takes security and user privacy seriously. Since MadMax operates with full shell execution privileges inside the Android application sandbox, security boundaries are strictly enforced.

If you discover a security vulnerability, please do NOT report it publicly in a GitHub issue. Instead, follow these steps:

1. **Email:** Send details to the repository maintainer or open a [GitHub Security Advisory](https://github.com/The-habib/Madmax/security/advisories/new).
2. **Details:** Include:
   - Affected MadMax version and commit hash
   - Android OS version and device architecture (ABI)
   - Step-by-step reproduction steps or PoC
   - Potential impact
3. **Response Timeline:** We aim to acknowledge reports within 48 hours and provide a patch or mitigation strategy within 7 business days.

---

## Protected Execution Constraints

- **W^X Execution Safety:** MadMax enforces `targetSdkVersion=28` to maintain Linux binary execution capability from `$PREFIX/bin` within the private application sandbox while isolating processes.
- **Local Unix Sockets:** Socket communication between `termux-am` and the app is bound to private abstract domain sockets guarded by application UID.
