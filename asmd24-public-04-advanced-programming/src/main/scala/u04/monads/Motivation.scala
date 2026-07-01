package u04.monads

object Motivation extends App:

  def getRandom(): Option[Double] =
    if Math.random() < 0.9 then Some(Math.random) else None
  
  // preview on for comprehension with scala Option[E]
  val sum: Option[Double] =
    for 
      x <- getRandom()
      y <- getRandom()
      z <- getRandom() 
    yield x+y+z
  println(sum) // Some(x+y+z) or None(), probabilistically
  
  // preview on for comprehension with scala List[E]
  val list: List[String] = 
    for 
      x <- List(10,20,30)
      y <- List("A","B","C") 
    yield x+y
  println(list) // List(10A, 10B, 10C, 20A, 20B, 20C, 30A, 30B, 30C)
  
  // equivalent to
  val list2: List[String] = 
    List(10,20,30).flatMap(x => 
      List("A","B","C").map(y => 
        x+y))
  println(list2) // List(10A, 10B, 10C, 20A, 20B, 20C, 30A, 30B, 30C)