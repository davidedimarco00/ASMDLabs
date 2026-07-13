# Exercise: Agentic AI Robot Navigation 
This folder is designed to implement an **Agentic AI** system where an autonomous LLM-based agent 
 guides a robot through a two-dimensional grid world with obstacles to reach a destination (Goal).

---

## Exercise Objective

Your goal is to complete the integration of the intelligent agent (`RobotAgent`) 
 and its operational tools (`RobotTools`) so that the robot is able to:
1. Autonomously inspect and analyze the grid map.
2. Avoid obstacles (marked as `#`).
3. Move along the four cardinal directions (`MoveUp`, `MoveDown`, `MoveLeft`, `MoveRight`).
4. Optionally grab or release objects (`Hold`, `Release`) -- for "advanced" scenarios.
5. Reach the target coordinate (Goal `G`) within a defined step limit.

---

The code is organized into the following components (inside the `it.unibo.exercise` package):

### 1. Already Implemented Components (Do NOT modify):
* **`AgentsAction.scala`**: Enum defining available actions (`MoveUp`, `MoveDown`, `MoveLeft`, `MoveRight`, `Hold`, `Release`).
* **`RobotState.scala`**: Case class modeling the current state of the robot (position `(Double, Double)`, orientation, and whether it holds an object).
* **`Robot.scala`**: Represents the physical robot and manages elementary state transitions (e.g., `move`).
* **`Environment.scala`**: Defines the simulation grid (`width` x `height`), validates robot movements (ensuring it doesn't move out of bounds or hit obstacles), and renders an ASCII map of the grid via `printGrid()`.

### 2. Components to Complete (The Exercise):
* **`RobotTools.scala`**: A helper class containing methods that should be exposed to the LLM as LangChain4j `@Tool`s.
* **`RobotSimulationApp.scala`**: The main application file containing:
  * The `RobotAgent` trait (the AI Agent interface with system and user prompts).
  * The main simulation loop and local LLM client configuration (Ollama).
    * Did you need it? Or can you implement the agent without it? Try to find out by yourself! :)

---

## Step-by-Step Instructions

### 1. Prerequisites and Ollama Setup
Make sure Ollama is installed and running on your system. 
For this exercise, we recommend using a model optimized for Tool-Calling, such as `qwen3.5:4b` (`gemma4:e2b` can also work but may be less efficient for tool execution).

```bash
ollama pull qwen3.5:4b
```

### 2. Implement the Tools (`RobotTools.scala`)
Open [RobotTools.scala](file:///home/gianluca/IdeaProjects/asmd-public-x-agentic-ai-code/src/main/scala/it/unibo/exercise/RobotTools.scala) and complete the methods:
1. **Annotate each method** with LangChain4j's `@Tool` annotation, specifying:
   * `name`: The tool name as seen by the LLM.
   * `value` (or description): A clear explanation of what the tool does, what parameters it accepts, and when the agent should use it. **Clear descriptions are critical for the agent's reasoning!**
2. **Implement the logic** by replacing the `???` placeholders with appropriate interactions with the `env` (Environment) instance:
   * For movement actions, call `env.step(...)` with the corresponding action.

### 3. Configure the RobotAgent (`RobotSimulationApp.scala`)
Open [RobotSimulationApp.scala](file:///home/gianluca/IdeaProjects/asmd-public-x-agentic-ai-code/src/main/scala/it/unibo/exercise/RobotSimulationApp.scala) and proceed as follows:
1. **Uncomment and define the RobotAgent prompt**:
   * Add `@SystemMessage` to instruct the LLM on its role, rules (e.g., step-by-step execution, yielding control after each action, avoiding loops), and the available tools.
   * Add `@UserMessage` containing the current robot state parameterized as `{{state}}`.
   * Define the method signature: `def next` ??? <-
2. **Uncomment the configuration in `runRobotSimulation`**:
   * Set up the `OllamaChatModel` builder pointing to your local Ollama instance (typically `http://localhost:11434`) and model.
   * Instantiate the `RobotTools` class.
   * Configure `AiServices` to build the `RobotAgent` instance, attaching the chat model, conversational memory (`MessageWindowChatMemory`), and tools.
3. **Uncomment the Agent Call in the Loop**:
   * Find and uncomment the `robotAgent.next(env.robot.state)` call inside the `while` loop.

---

## How to Run the Simulation

Once you have completed the implementation, compile and run the application via SBT:

If implemented correctly, you should see the AI Agent step-by-step:
1. Inspect the state of the environment (calling the `get_environment_status` tool).
2. Plan and decide on the next best move based on the target goal and obstacle coordinates.
3. Call the movement tools (`move_up`, `move_down`, etc.).
4. Successfully guide the robot to the goal position at `(3.0, 3.0)`!
