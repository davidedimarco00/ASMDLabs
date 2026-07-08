# Specialised Agents Workflow

## Goal

Evaluate whether specialised Copilot agents can collaborate to produce a higher-quality Connect Four implementation than a single generic agent.

## Starting point

The workflow started only from the existing `ConnectFour` interface.

The file `product.md` was intentionally not used.

## Agents used

- Design Agent
- Test Agent
- Refactor Agent

## Workflow

### 1. Design Agent

The Design Agent analysed the interface and produced a design plan.

Output saved in:

- `docs/design-agent-output.md`

### 2. Test Agent

The Test Agent created behavioural JUnit tests based on the interface and design plan.

Output saved in:

- `docs/test-agent-output.md`

### 3. Refactor Agent

The Refactor Agent implemented the production code so that the tests passed.

## Results

- Compilation:
- Tests:
- Number of tests:
- Manual fixes needed:
- Main assumptions:
- Limitations:

## Initial observations

The specialised workflow separated design, testing, and implementation. This made the development process more controlled than a single one-shot prompt.