package u04.contextual

import u04.datastructures.Sequences.*
import u04.datastructures.Optionals.*
import Sequence.*

object GivenContextualParameters:
  import UsingContextualParameters.*

  // defining a canonical term using a previous definition
  given g: OrderingModule[Int] = MyStandardIntOrdering // "g: " non necessary

  // defining a canonical term on-site: an alternate syntax
  given OrderingModule[String] with
    def greater(t1: String, t2: String): Boolean = t1 < t2

  // those given, are here "in scope"
  @main def tryGivenContextualParameters =  
    println:
      max(Cons(10, Cons(30, Cons(20, Nil()))))
    println:
      max(Cons("10", Cons("30", Cons("20", Nil()))))

  
@main def tryImportGiven =  
  import UsingContextualParameters.* 
  // importing all given
  import GivenContextualParameters.given 
  // importing a specific given
  // import GivenContextualParameters.g
  // importing just * would not work!

  println:
    max(Cons(10, Cons(30, Cons(20, Nil()))))

  val ord = summon[OrderingModule[Int]] // gives MyStandardIntOrdering
