---
trigger: always_on
---

# Code Style and Architecture Rules

## Context

Apply these rules to all code generations, refactorings, and reviews within this project to maintain production-ready standards.

## Code Quality Rules

- **Formatting:** Adhere strictly to the project's default formatter and style guide (e.g., PEP 8 for Python, Prettier for TypeScript).
- **Type Safety:** Always include explicit types, return types, and interfaces. Avoid using generic or 'any' types.
- **Error Handling:** Wrap asynchronous calls and IO operations in descriptive try-catch blocks. Do not swallow errors; log them with clear context.
- **Documentation:** Write concise JSDoc/Docstrings for all public classes, methods, and functions explaining parameters and return values.

## Architectural Boundaries

- Keep components modular and single-responsibility focused.
- Separate business logic from UI rendering components.
- Do not introduce external dependencies without explicit permission.
