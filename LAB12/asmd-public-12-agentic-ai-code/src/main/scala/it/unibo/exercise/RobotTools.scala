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

  @Tool(name = "move_up", value = Array("Move the robot up one position"))
  def moveUp(): String = env.step(AgentsAction.MoveUp)

  @Tool(name = "move_down", value = Array("Move the robot down one position"))
  def moveDown(): String = env.step(AgentsAction.MoveDown)

  @Tool(name = "move_left", value = Array("Move the robot left one position"))
  def moveLeft(): String = env.step(AgentsAction.MoveLeft)

  @Tool(name = "move_right", value = Array("Move the robot right one position"))
  def moveRight(): String = env.step(AgentsAction.MoveRight)

  @Tool(name = "hold", value = Array("Robot holds/grabs the object at current position"))
  def hold(): String = env.step(AgentsAction.Hold)

  @Tool(name = "release", value = Array("Robot releases the object it is holding"))
  def release(): String = env.step(AgentsAction.Release)

  @Tool(name = "get_status", value = Array("Get the current environment and robot status"))
  def getStatus(): String =
    val robot = env.robot
    s"""Robot Status:
       |Position: ${robot.state.position}
       |Holding Object: ${robot.state.holdingObject}
       |Goal: ${env.goal}
       |Goal Reached: ${env.isGoalReached}
       |Grid:\n${env.printGrid()}""".stripMargin

  @Tool(name = "check_goal", value = Array("Check if the robot has reached the goal"))
  def checkGoal(): String =
    if env.isGoalReached then
      "Success: Robot has reached the goal!"
    else
      s"Robot not at goal yet. Current position: ${env.robot.state.position}, Goal: ${env.goal}"

