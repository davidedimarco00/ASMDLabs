package u09.examples

import u09.model.QMatrix

object TryQMatrixObstacles extends App:

  import u09.model.QMatrix.Move.*
  import u09.model.QMatrix.*


  val obstacles: Set[Node] = Set(
    (0,2), (1,2), (2,2),
    (2,4), (3,4), (4,4)
  )

  val rl: QMatrix.Facade = Facade(
    width = 5,
    height = 6,
    initial = (0, 0),
    terminal = { case (_, r) => r == 5 },   // arrivare in fondo
    reward = { case _ => -0.1 },             // costo per passo
    jumps = PartialFunction.empty,
    obstacles = obstacles,
    gamma = 0.9,
    alpha = 0.5,
    epsilon = 0.3,
    v0 = 0
  )

  val q0 = rl.qFunction
  val q1 = rl.makeLearningInstance().learn(10000, 100, q0)
  println(rl.showEnvironment(rl))
  println(rl.show(q1.vFunction, "%2.2f"))
  println(rl.show(s => q1.bestPolicy(s).toString, "%7s"))