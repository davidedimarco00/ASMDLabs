package u04.contextual

import u04.datastructures.Sequences.*
import u04.datastructures.Optionals.*
import Sequence.*

object UsingContextualParameters:

  trait OrderingModule[T]:
    def greater(t1: T, t2: T): Boolean

  def max[T](seq: Sequence[T])(using ordering: OrderingModule[T]): T = seq match
    case Cons(h1, Cons(h2, t)) => 
      val m = max(Cons(h2, t))(using ordering)
      if ordering.greater(h1, m) then h1 else m
    case Cons(h1, Nil()) => h1
  
  object MyStandardIntOrdering extends OrderingModule[Int]:
    def greater(t1: Int, t2: Int): Boolean = t1 > t2

  object MyStandardStringOrdering extends OrderingModule[String]:
    def greater(t1: String, t2: String): Boolean = t1 < t2  

  @main def tryUsingContextualParameters =  
    println:
      max(Cons(10, Cons(30, Cons(20, Nil()))))(using MyStandardIntOrdering)
    println:
      max(Cons("10", Cons("30", Cons("20", Nil()))))(using MyStandardStringOrdering)
