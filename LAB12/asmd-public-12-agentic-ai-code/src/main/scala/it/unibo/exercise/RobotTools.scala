package it.unibo.exercise

import dev.langchain4j.agent.tool.Tool

/**
 * EXERCISE: Implement the tool integration for the Robot.
 * These methods should be annotated with @Tool from LangChain4j, allowing the LLM-based agent
 * to interact with and inspect the Environment.
 * 
 * Your tasks:
 * 1. Understand the main actions the robot can perform (move up/down/left/right, hold, release) and how to retrieve the environment status.
 * 2. Implement each tool's logic by invoking the corresponding actions on the `env` (Environment) object.
 * 3. Return the resulting String/Call statue from each tool call so the agent is informed of the result.
 */
class RobotTools(val env: Environment):
  // TODO!! Implement the tools for the robot agent, annotated with @Tool.
  @Tool(name = "todo", value = Array("Placeholder tool. Replace with actual tools for moving and inspecting the environment."))
  def foo(): Unit = {}

