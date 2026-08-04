---
trigger: always_on
---

# Automatic Semantic Versioning

## Context

Applies to version tracking configuration files. Ensures every committed iteration increments the release telemetry.

## Automation Rules

- **Patch Bumps:** Upon modifying any functional code block, instantly identify the project's configuration file (e.g., `package.json`, `setup.py`, `Cargo.toml`).
- **Versioning Strategy:** Increment the semantic patch number (X.Y.Z -> X.Y.Z+1) directly within the configuration file.
- **Changelog Tracking:** Automatically append a one-line bullet point to a `CHANGELOG.md` file summarizing what minor change prompted the version update.
