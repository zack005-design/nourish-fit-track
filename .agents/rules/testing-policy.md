---
trigger: always_on
---

# Testing Requirements and Coverage

## Context

Enforce these testing standards whenever creating new features, fixing bugs, or rewriting existing logic.

## Testing Rules

- **Co-generation:** Every new feature function or API route must be generated alongside its corresponding unit test file.
- **Frameworks:** Use the established testing framework of the project (e.g., Jest, PyTest, Mocha).
- **Mocking:** Mock all external API endpoints, database queries, and third-party services to ensure tests run completely isolated.
- **Coverage:** Aim for high edge-case coverage, explicitly testing null values, empty payloads, and error states.
