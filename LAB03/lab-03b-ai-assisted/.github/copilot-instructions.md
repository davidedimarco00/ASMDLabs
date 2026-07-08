# Copilot Instructions

## Project context
- This repository is a Java/Gradle project for the `ConnectFour` assignment.
- Source code lives under `src/main/java/it/unibo` and tests under `src/test/java/it/unibo`.
- Prefer changes that fit the existing project structure and style.

## Coding guidelines
- Keep solutions small, focused, and easy to review.
- Preserve existing public APIs unless a change is explicitly required.
- Prefer clear, readable Java over clever or overly abstract code.
- Use descriptive names for classes, methods, parameters, and variables.
- Keep methods short and single-purpose when practical.
- Reuse existing logic instead of duplicating behavior.
- Follow the same indentation, formatting, and naming conventions already used in the codebase.

## Constraints
- Do not introduce new dependencies unless they are clearly necessary.
- Do not modify build outputs, generated files, or compiled artifacts in `build/`.
- Do not change package names or directory layout unless the task requires it.
- Avoid unrelated refactors or formatting-only changes.
- Avoid breaking backwards compatibility without a clear reason.
- Keep any changes compatible with the current Gradle build and test setup.

## Testing expectations
- Update or add tests whenever behavior changes.
- Prefer deterministic tests with no external I/O, networking, or timing dependence.
- Run the relevant Gradle tests after code changes when possible.
- Fix failing tests before considering the task complete.

## Implementation preferences
- Prefer simple object-oriented design that matches the existing code.
- Use standard library features before adding helper libraries.
- When adding new logic, consider edge cases such as invalid input, empty state, and boundary conditions.
- Keep error handling explicit and predictable.

## Documentation
- Update README or inline comments only when they add real value.
- Keep comments concise and focused on intent, not restating the code.

## Priorities
1. Correctness
2. Readability
3. Testability
4. Minimal change surface
5. Maintainability

