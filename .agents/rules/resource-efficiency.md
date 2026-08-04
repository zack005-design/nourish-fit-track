---
trigger: always_on
---

# AI Resource and Compute Optimization

## Context

Enforce this rule across all conversations and operations to minimize token usage, prevent unnecessary model calls, and reduce overall AI compute consumption.

## Optimization Rules

- **Direct Implementations:** Write the required code directly on the first attempt. Avoid generating multiple design iterations, alternative approaches, or structural variations unless explicitly asked.
- **No Verbose Explanations:** Omit conversational filler, conceptual explanations, tutorials, and line-by-line code breakdowns. Output the functional code immediately.
- **Targeted Operations:** Only read, modify, or rewrite the exact functions or lines of code necessary to accomplish the task. Do not rewrite, duplicate, or output entire files if a partial snippet or specific line change is sufficient.
- **Batch Processing:** Whenever possible, bundle related file modifications, terminal checks, or local commands into a single execution turn rather than spreading them across multiple separate model responses.
- **Strict Verification Limits:** Run build checks and test runners strictly once after a major block of changes, rather than continuously looping through predictive validations for small edits.
