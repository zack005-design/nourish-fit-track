---
trigger: always_on
---

# Local Pre-Push Verification

## Context

Applies automatically right before a `git commit` or `git push` command is executed by the agent.

## Automation Rules

- **Mandatory Suite Run:** Before executing code deployment or pushing to GitHub, invoke the local test suite runner tool (e.g., `npm test`, `pytest`).
- **Failure Halting:** If any local tests fail or return an error state, immediately halt the Git deployment process.
- **Self-Healing Loop:** Analyze the failed test stack trace, rewrite the offending code until all unit tests pass, and only then proceed with the automatic repository update.
