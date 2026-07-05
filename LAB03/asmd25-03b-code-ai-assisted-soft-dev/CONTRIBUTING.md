# Contributing

## Workflow

1. **Read before writing**: understand existing interfaces and tests before adding code.
2. **TDD**: write the failing test first, then the minimal implementation to make it pass, then refactor.
3. **Small, focused commits**: one logical change per commit.
4. **Never break tests**: `./gradlew test` must be green before any commit.

---

## Code Style — Java

### Naming
- Classes and interfaces: `PascalCase`
- Methods and variables: `camelCase`
- Constants: `UPPER_SNAKE_CASE`
- Packages: all lowercase, no underscores (`it.unibo.engine`)

### Structure
- One top-level type per file; file name matches class name.
- Put interfaces in the same package as their primary implementation.
- Prefer `final` classes for concrete implementations unless extension is explicitly designed for.
- Prefer package-private visibility over `public` for implementation classes.

### Principles
- **Interface-first**: define the interface before writing any implementation.
- **Constructor injection**: accept dependencies as constructor parameters; no static state.
- **Immutability by default**: use `final` fields, return defensive copies of mutable collections.
- **Fail fast**: validate constructor arguments with `Objects.requireNonNull`; throw `IllegalArgumentException` for invalid domain values.
- **No raw types**: always parameterize generics.

### Formatting
- 4-space indentation (no tabs).
- Opening brace on the same line.
- Maximum line length: 120 characters.
- One blank line between methods; two blank lines between top-level declarations.
- `import` statements: no wildcard imports; organized alphabetically.

---

## Code Style — Gradle / build.gradle.kts

- One `dependency` per line; group by category with a blank line between groups.
- Declare version numbers as `val`s at the top of `build.gradle.kts` when the same version is shared by multiple artifacts.
- Do not mix Kotlin source with the Java application source; keep the build Kotlin-only.
- Keep `plugins` block minimal; document every plugin with a comment explaining its purpose.

---

## Testing Conventions

- **Framework**: JUnit 5 (`@Test`, `@BeforeEach`, `@AfterEach`).
- **Pattern**: Arrange / Act / Assert (AAA) — separate each phase with a blank line.
- **Method names**: `should<Expected>When<Condition>` (e.g., `shouldReturnNarrativeWhenPlayerMakesValidDecision`).
- **Isolation**: tests must not share mutable state; each test creates its own fixtures.
- **Mocking**: use Mockito for external collaborators (LLM models, IO); never mock the class under test.
- **LLM tests**: always mock `ChatModel` or `EmbeddingModel`; do not call real Ollama/Gemini in unit tests.
- **Test placement**: mirror the main package structure under `src/test/java/`.

---

## Directory Layout

```
src/
  main/java/it/unibo/   ← production code (Java)
  test/java/it/unibo/   ← tests (JUnit 5)
  main/resources/       ← data files (dataset.txt, etc.)
build.gradle.kts        ← Gradle build definition
ARCHITECTURE.md         ← system design reference
PRODUCT.md              ← vision, objectives, personas
CONTRIBUTING.md         ← this file
.github/
  copilot-instructions.md        ← always-on Copilot context
  instructions/
    java.instructions.md         ← per-file Java hints
    gradle.instructions.md       ← per-file Gradle hints
  agents/
    tdd.agent.md                 ← TDD workflow agent
```

---

## Dependency Management

- Update versions only when needed; prefer stable releases over snapshots.
- When adding a new dependency, add a comment in `build.gradle.kts` explaining why it is needed.
- Keep test dependencies scoped to `testImplementation` or `testRuntimeOnly`.
- Do not add dependencies that duplicate functionality already provided (e.g., do not add Jackson if Gson is already present).
