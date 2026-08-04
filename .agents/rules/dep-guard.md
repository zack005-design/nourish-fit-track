---
trigger: always_on
---

# Autonomous Dependency Management

## Context

Applies when the agent writes or rewrites code introducing third-party modules or uninstalled imports.

## Automation Rules

- **Import Scans:** Whenever code is generated with a library not currently listed in the environment config, automatically install it using the project's package manager (e.g., `npm`, `pip`, `cargo`).
- **File Updating:** Lock the exact installed version into the package manifest file (`requirements.txt`, `package.json`, etc.) instantly.
- **Isolation:** Never use global environment flags. Always lock versions to the local virtual environment or node directory.
