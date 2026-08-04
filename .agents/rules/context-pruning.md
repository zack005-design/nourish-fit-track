---
trigger: always_on
---

# Context Minimisation & Gitignore Boundary Policy

## Context

Enforce this rule across the entire workspace to prevent the AI agent from reading heavy data files, large media assets, or build artifacts, protecting the context window and minimizing token usage.

## Context Filtering Rules

- **Ignore Unnecessary Formats:** Explicitly ignore, bypass, and never read large non-text formats like `.png`, `.jpg`, `.mp4`, `.pdf`, or `.zip` unless a specific file is targeted by the user.
- **Respect Build Artifacts:** Never scan build outputs, distribution directories, or dependency trees (e.g., `node_modules/`, `dist/`, `build/`, `.venv/`, `target/`).
- **Data Path Skipping:** Automatically exclude large datasets, local cache databases, log files (`*.log`), and heavy JSON/CSV files exceeding 50KB from general repository searches or indexing.
- **Dynamic File Reading:** When investigating the codebase, only pull the definitions, types, or targeted functions instead of loading the entire content of large source files.
