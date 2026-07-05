package u04.typeclasses

import u04.datastructures.Sequences.*
import u04.datastructures.Optionals.*
import Sequence.*

object ContextBound:

  // Ordered as a typeclass
  trait Ordered[T]:
    def greater(t1: T, t2: T): Boolean

  // max with ad-hoc polymorhism
  // it can be called on T only if an Ordered[T] is available in scope
  def max[T: Ordered](seq: Sequence[T]): T = seq match
    case Cons(h1, Cons(h2, t)) => 
      val m = max(Cons(h2, t)) // can avoid passing context
      if summon[Ordered[T]].greater(h1, m) then h1 else m
    case Cons(h1, Nil()) => h1
  
  // defining the "Ordered" extension for Int
  given Ordered[Int] with
    def greater(t1: Int, t2: Int): Boolean = t1 > t2

  // defining the "Ordered" extension for String
  given Ordered[String] with
    def greater(t1: String, t2: String): Boolean = t1 > t2  

  @main def tryContextBound =  
    // the necessary given are already in scope
    println:
      max(Cons(10, Cons(30, Cons(20, Nil()))))
    println:
      max(Cons("a", Cons("c", Cons("d", Nil()))))  
    // println(max(Cons(1.0, Cons(3.0, Cons(5.0, Nil()))))) // not working