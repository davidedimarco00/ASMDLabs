# Architecture

## Overview

This project is an educational lab exploring **LLM integration in Java/Kotlin applications**. It uses the `langchain4j` library to interact with local (Ollama) and cloud (Google Gemini) LLM backends. The build system is **Gradle** with Kotlin DSL, but all application code is written in **Java**.

---

## High-Level Components

The codebase is organized around four architectural layers that apply to every application in the lab:

```
┌─────────────────────────────────────────────┐
│                    View                     │  ← UI / presentation (Swing)
├─────────────────────────────────────────────┤
│                 Controller                  │  ← Orchestration, game flow, AI integration
├─────────────────────────────────────────────┤
│                   Model                     │  ← Pure domain logic, no external dependencies
├─────────────────────────────────────────────┤
│                LLM Engine                   │  ← LLM calls, retry, structured output parsing
└─────────────────────────────────────────────┘
```

### Model
The domain layer holds the core rules and state of an application (e.g., game board, story state, player data). It has **no dependencies** on the LLM, the UI framework, or any I/O — making it fully unit-testable. All domain objects are expressed as **interfaces** backed by immutable implementations.

### View
The presentation layer renders state to the user and forwards input events. It depends only on the Model (read-only) and communicates with the Controller through callbacks or observer interfaces. UI toolkit details (Swing, console) are confined here and never leak into other layers.

### Controller
The orchestration layer wires Model, View, and LLM Engine together. It reacts to user input, updates the Model, delegates AI decisions to the LLM Engine, and instructs the View to re-render. It also owns **prompt construction** — translating domain state into a text representation the LLM can consume.

### LLM Engine
The integration layer is responsible for all communication with LLMs (local via Ollama or cloud via Gemini). Its single responsibility is: accept a prompt, call the model, parse the response, and retry on failure. It exposes a narrow interface to the Controller so the underlying model can be swapped without touching any other layer.

---

## Design Principles

- **Interface-first**: every layer boundary is defined as a Java interface; implementations are hidden behind it.
- **Strict layer separation**: dependencies flow downward only (Controller → Model; Controller → Engine; View → Model). No layer imports from a layer above it.
- **Immutability**: domain objects carry no mutable state; controllers coordinate state transitions explicitly.
- **Resilience at the boundary**: the LLM Engine retries failed calls and throws a well-typed exception only after exhausting all attempts, so the rest of the application never sees raw LLM errors.
- **Constructor injection**: all cross-layer dependencies are passed in at construction time; no service locators or static singletons.

---

## Module Map

```
it.unibo
├── basics/     — Standalone LLM demos (embeddings, text generation) — no MVC
├── chat/       — Agent-to-agent dialogue examples
├── prompt/     — Prompt engineering technique demonstrations
├── model/      — Domain model for the interactive story (Model layer)
├── engine/     — LLM Engine abstraction for the interactive story
├── view/       — UI components for the interactive story (View layer)
├── tictactoe/
│   ├── model/       — Game domain: board state and rules (Model layer)
│   ├── view/        — Swing board rendering and input (View layer)
│   └── controller/  — Game flow, AI player, prompt building, response parsing (Controller + Engine layer)
└── StoryApp    — Entry point (wires all layers together)
```


