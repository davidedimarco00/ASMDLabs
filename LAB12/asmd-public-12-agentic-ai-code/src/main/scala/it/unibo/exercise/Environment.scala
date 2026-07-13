package it.unibo.exercise

case class Environment(
  width: Int,
  height: Int,
  goal: (Double, Double),
  obstacles: Set[(Double, Double)],
  var robot: Robot
):
  def isValidPosition(pos: (Double, Double)): Boolean =
    val (x, y) = pos
    x >= 0.0 && x < width.toDouble &&
    y >= 0.0 && y < height.toDouble &&
    !obstacles.contains(pos)

  def isGoalReached: Boolean =
    // Check if the robot has reached the goal
    robot.state.position == goal

  def step(action: AgentsAction): String =
    val oldPos = robot.state.position
    action match
      case AgentsAction.MoveUp | AgentsAction.MoveDown | AgentsAction.MoveLeft | AgentsAction.MoveRight =>
        val tempRobot = robot.copy(state = robot.state.copy())
        tempRobot.move(action)
        if (isValidPosition(tempRobot.state.position))
          robot.move(action)
          s"Success: Moved robot ${action} from $oldPos to ${robot.state.position}."
        else
          s"Error: Invalid move ${action}! Encountered obstacle or outer boundary. Robot remains at $oldPos."
      case AgentsAction.Hold =>
        robot.move(action)
        "Success: Robot is now holding the object."
      case AgentsAction.Release =>
        robot.move(action)
        "Success: Robot released the object."


  def printGrid(): String =
    (height - 1 to 0 by -1).map: y =>
      (0 until width).map: x =>
        val pos = (x.toDouble, y.toDouble)
        pos match
          case p if p == robot.state.position => if robot.state.holdingObject then " [R] " else "  R  "
          case p if p == goal                 => "  G  "
          case p if obstacles.contains(p)     => "  #  "
          case _                              => "  .  "
      .mkString
    .mkString("\n")

