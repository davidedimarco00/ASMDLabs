package it.unibo.exercise

case class Robot(
  id: String,
  var state: RobotState,
  name: String = "RoboOne"
):
  import Robot.*
  def move(action: AgentsAction): Unit =
    action match
      case AgentsAction.MoveUp =>
        state = state.copy(position = (state.position._1, state.position._2 + Delta))
      case AgentsAction.MoveDown =>
        state = state.copy(position = (state.position._1, state.position._2 - Delta))
      case AgentsAction.MoveLeft =>
        state = state.copy(position = (state.position._1 - Delta, state.position._2))
      case AgentsAction.MoveRight =>
        state = state.copy(position = (state.position._1 + Delta, state.position._2))
      case AgentsAction.Hold =>
        state = state.copy(holdingObject = true)
      case AgentsAction.Release =>
        state = state.copy(holdingObject = false)

object Robot:
  private val Delta: Double = 1.0