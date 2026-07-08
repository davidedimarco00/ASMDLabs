# Lab: AI-Assisted Software Engineering

In this lab, we will experiment with GitHub Copilot to explore how AI coding agents can be tuned to follow instructions and produce high-quality code. We will also investigate how **context** and **prompt design** affect the quality of generated code, comparing results across different configurations.

---

## Prerequisites

- **Step 0: Set up Copilot**
    - Install the GitHub Copilot extension for your IDE ([VS Code](https://marketplace.visualstudio.com/items?itemName=GitHub.copilot), [JetBrains](https://plugins.jetbrains.com/plugin/17718-github-copilot), etc.)
    - Activate GitHub Education benefits: [github.com/settings/education/benefits](https://github.com/settings/education/benefits?locale=en-US)

---

## Step 1: Baseline — "Try the Vibe"

Ask Copilot to implement a **Connect Four** game with minimal guidance, and observe how it performs out of the box.

**Evaluate the solution along these axes:**

1. **Code quality** — Are there code smells? Is the code readable and maintainable?
2. **Correctness** — Does the game work as expected? Are edge cases handled?
3. **Creativity** — Does the code use interesting or innovative techniques? Does it show a solid understanding of the problem domain?

Try multiple Copilot models (if available), save each solution, and compare the results.
Try also open source models (via ollama) or other providers, and compare those results as well.

---

## Step 2: Prompt Engineering

Create a `.github/copilot-instructions.md` file to provide Copilot with explicit coding guidelines and constraints. Then regenerate the Connect Four game.

**Evaluate the new solution using the same axes as Step 1, and additionally consider:**

- Is the output more consistent with the instructions you provided?
- How much did the instructions improve (or change) the solution compared to the baseline?

---

## Step 3: Context Engineering

Enrich the project context by adding supporting documentation, for example:

- `PRODUCT.md` — Describes the product vision, features, and requirements.
- `CONTRIBUTING.md` — Defines coding standards, conventions, and contribution guidelines.
- Any other files you think would help Copilot understand the project better.

Update `.github/copilot-instructions.md` to reference and leverage this new context. Regenerate the solution, then evaluate and compare as before.

---

## Step 4: Human-in-the-Loop — AI-Assisted Development

Now, put yourself in the loop alongside Copilot. Choose one or more of the following strategies:

- **Test the generated code** — Write tests after generation to verify correctness.
- **Test-Driven Development (TDD)** — Write tests first, then let Copilot generate the implementation.
- **Refactor iteratively** — Accept Copilot's output, then improve its quality through guided refactoring.

As part of this step, consider creating specialized **agents** (custom instructions or modes) for specific tasks — e.g., a *test agent*, a *refactor agent*, or a *debug agent* — and explore how they can work together to improve the overall solution.
Remember, you should be the *owner* of the codebase, guiding Copilot as a tool rather than relying on it to produce perfect code on its own.

---

## Connect Four (current implementation)

- Game engine: `src/main/java/it/unibo/ConnectFourImpl.java`
- CLI runner: `src/main/java/it/unibo/ConnectFourGame.java`
- Unit tests: `src/test/java/it/unibo/ConnectFourImplTest.java`

Quick commands:

```powershell
./gradlew test
./gradlew run --args=''
```

If `run` is not configured, execute the class directly from your IDE: `it.unibo.ConnectFourGame`.
