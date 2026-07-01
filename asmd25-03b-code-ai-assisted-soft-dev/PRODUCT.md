# Product

## Vision

An **LLM-powered interactive story game** where every narrative beat, plot twist, and set of choices is generated in real time by a large language model. The player steers a living story that never repeats itself — the AI is the author, and the human is the protagonist.

---

## Core Concept

The player is placed inside a dynamic narrative world. At each step they receive:

1. A **narrative passage** describing the current scene.
2. A **question or dilemma** that drives the plot forward.
3. A set of **choices** to pick from.

Their decision is fed back to the LLM, which continues the story coherently, remembers past choices, and eventually reaches a resolution (victory, defeat, or an open ending).

---

## User Personas

### Casual Player
- Wants an immersive, text-based adventure that feels fresh on every playthrough.
- Values narrative quality and coherent story continuity over technical depth.
- Interaction: launches `StoryApp`, answers prompts, reads generated story passages.

### Developer / Student Extending the Game
- Wants to add new story themes, change the LLM backend, or tune prompt strategies.
- Needs clean extension points: swap the model, change the prompt template, or add new response fields without rewriting unrelated code.

---

## Key Features

| Feature | Description |
|---|---|
| **Dynamic narrative** | Every passage is LLM-generated; no pre-written script |
| **Structured choices** | The LLM responds in JSON so choices are always well-formed |
| **Retry resilience** | Malformed LLM responses are retried transparently |
| **Model-agnostic** | Runs on local Ollama models or Google Gemini with no game-logic changes |
| **Stateful player** | Player attributes (name, traits) persist across turns and influence the narrative |

---

## Success Criteria

- The game produces a coherent multi-turn story with no crashes or unhandled exceptions.
- The LLM always returns parseable JSON; if it does not, the engine retries and recovers gracefully.
- Swapping the underlying LLM model requires no changes outside the engine / configuration layer.
- Each turn presents exactly the narrative, question, and choices the model intended — no raw JSON leaks to the player.
