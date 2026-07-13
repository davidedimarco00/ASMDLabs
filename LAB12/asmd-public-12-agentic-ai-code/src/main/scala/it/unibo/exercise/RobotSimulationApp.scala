package it.unibo.exercise

import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.model.ollama.OllamaChatModel
import dev.langchain4j.service.{AiServices, SystemMessage, UserMessage, V}
import dev.langchain4j.service.V

/**
 * EXERCISE: Define the RobotAgent trait.
 * 
 * Your tasks:
 * 1. Define the system instructions for the robot agent using @SystemMessage.
 *    The instruction should guide the agent to act as a navigation AI that uses tools
 *    to help the robot reach a goal position.
 * 2. Define the user message format using @UserMessage.
 * 3. Define the next method with a RobotState argument annotated with @V.
 */
trait RobotAgent:
  @SystemMessage(Array("""You are an intelligent navigation AI assistant for a robot in a grid world.
Your mission: Guide the robot to reach the goal position safely while avoiding obstacles.

CRITICAL RULES:
1. You MUST execute ONLY ONE tool call per response.
2. After executing a tool, immediately return a text response describing what you did.
3. DO NOT chain multiple tool calls - the system will call you again for the next step.
4. Start by using get_status to see the environment.
5. Then execute ONE movement (move_up, move_down, move_left, move_right) OR hold/release.
6. After the action, explain what you did and wait for the next turn.

AVAILABLE TOOLS:
- get_status: Check current environment state and robot position
- move_up, move_down, move_left, move_right: Move the robot one step
- hold: Grab an object at current position
- release: Release a held object
- check_goal: Verify if goal is reached

Goal: Navigate the robot to (3.0, 3.0) while avoiding obstacles.

Remember: Execute ONE tool, then return text explanation. The system controls the loop."""))
  @UserMessage(Array("""Current robot state: {{state}} What is the next single action? Execute it and explain."""))
  def next(@V("state") state: RobotState): String

@main
def runRobotSimulation(): Unit =
  println("==========================================================")
  println("  ROBOT SIMULATION WITH AGENTIC AI (EXERCISE)             ")
  println("==========================================================")

  // 1. Set up the starting robot and environment
  val initialRobotState = RobotState(
    position = (0.0, 0.0),
    orientation = 0.0,
    holdingObject = false
  )
  val robot = Robot(id = "robo-01", state = initialRobotState, name = "Explorer-1")

  // Create a 4x4 grid.
  // Start at (0,0), Goal at (3,3). Obstacles block some paths.
  val env = Environment(
    width = 4,
    height = 4,
    goal = (3.0, 3.0),
    obstacles = Set((1.0, 1.0), (1.0, 2.0), (2.0, 1.0)),
    robot = robot
  )

  // TODO: 2. Build the Ollama Chat Model.
  // We recommend 'qwen3.5:4b' or 'gemma4:e2b' for this exercise, but you can experiment with others.
  // Ensure your Ollama server is running locally (http://localhost:11434).

  println("Initializing Ollama Chat Model (qwen3.5:4b)...")
  val model = OllamaChatModel.builder()
    .baseUrl("http://localhost:11434")
    .modelName("qwen3.5:4b")
    .temperature(0.0)
    .timeout(java.time.Duration.ofSeconds(60))
    .logRequests(true)
    .logResponses(true)
    .build()


  // TODO: 3. Bind the environment to your tools helper
  val tools = new RobotTools(env)
  // TODO: 4. Build the AI Service with tools and memory window
  
  val chatMemory = MessageWindowChatMemory.withMaxMessages(20)
  val robotAgent: RobotAgent = AiServices.builder(classOf[RobotAgent])
    .chatModel(model)
    .tools(tools)
    .chatMemory(chatMemory)
    .build()

  // 5. Start the external step-by-step simulation loop
  println("\nStarting navigation loop...")
  var stepCount = 0
  val maxSteps = 20

  while (!env.isGoalReached && stepCount < maxSteps) {
    stepCount += 1
    println(s"\n${"=" * 60}")
    println(s"STEP $stepCount")
    println("=" * 60)
    
    // Save state before action
    val stateBefore = env.robot.state
    println(s"📍 State BEFORE: Pos=${stateBefore.position}, Holding=${stateBefore.holdingObject}")
    println("\nGrid:")
    println(env.printGrid())
    
    // TODO: 6. Invoke the agent passing the current state.
    try {
      val agentResponse = robotAgent.next(env.robot.state)
      println(s"\n🤖 Agent Response: $agentResponse")
    } catch {
      case e: Exception =>
        println(s"❌ Agent Error: ${e.getMessage}")
        e.printStackTrace()
    }
    
    // Show state after action
    val stateAfter = env.robot.state
    println(s"\n📍 State AFTER: Pos=${stateAfter.position}, Holding=${stateAfter.holdingObject}")
    
    // Show what changed
    if (stateBefore.position != stateAfter.position) {
      println(s"  ✓ Position changed: ${stateBefore.position} → ${stateAfter.position}")
    }
    if (stateBefore.holdingObject != stateAfter.holdingObject) {
      println(s"  ✓ Holding changed: ${stateBefore.holdingObject} → ${stateAfter.holdingObject}")
    }
    
    // Check if goal reached
    if (env.isGoalReached) {
      println("\n🎯 GOAL REACHED!")
    }
    
    // Brief sleep to visualize the progress smoothly
    Thread.sleep(1500)
  }

  println("\n==========================================================")
  if (env.isGoalReached)
    println("🎉 GOAL REACHED! The robot arrived safely at the goal position (3.0, 3.0)!")
  else
    println("❌ SIMULATION STOPPED / TIMEOUT.")
  println("==========================================================")
  println("Final Environment State:")
  println(env.printGrid())
  println("Exercise setup complete! Ready for your implementation.")

