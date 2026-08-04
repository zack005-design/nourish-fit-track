---
trigger: always_on
---

# Custom Semantic Auto-Commit Policy

## Context

Enforce this rule across the workspace to ensure that any modification, no matter how small, is immediately saved, named descriptively by the AI, and pushed to the remote repository.

## Automation Rules

- **Immediate Evaluation:** After modifying, creating, or deleting code, instantly analyze the specific changes you just made to the files.
- **Custom Descriptors:** Generate a highly descriptive, unique commit name following semantic guidelines based on your exact actions. Do not use generic names.
  - *Examples of allowed custom names:* `fix(ui): adjust navbar padding for mobile`, `feat(auth): add middleware token validation`, `refactor: optimize database query performance`.
- **Atomic Operations:** Execute the commit immediately using your custom generated string: `git commit -m "<your_custom_descriptive_name>"`.
- **Instant Upstream Sync:** Follow the commit instantly with a `git push origin` command to keep GitHub updated in real time. Do not wait for manual prompts or bundle multiple separate tasks into one push.
