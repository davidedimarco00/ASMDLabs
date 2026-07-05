package u04.datastructures

object ExtensionMethods extends App:
  
  // functional style for declaring operations
  def mult(n: Int, m: Int): Int = n * m
  
  // functional style of operation call
  println:
    mult(5, 3)

  // extension methods:
  // a twist on definition: extracting an argument  
  extension (n: Int) def mult2(m:Int) = n * m
  // standard call in curled version is still possible
  println:
    mult2(5)(3)
  // OO-like syntax
  println:
    5.mult2(3)

  import Sequences.*
  import Sequence.* 

  // cannot chain calls!
  val seq = Cons(10, Cons(20, Cons(30, Nil())))
  println:
    Sequence.sum(map(filter(seq)(_ > 10))(_ + 1))
  
  // define size and filt as extension methods on a generic Sequence[A]
  extension [A](s: Sequence[A]) 

    def size: Int = s match
      case Cons(_, t) => 1 + t.size
      case _ => 0

    def filt(p: A => Boolean): Sequence[A] = s match
      case Cons(h, t) if p(h) => Cons(h, t.filt(p))
      case Cons(_, t) => t.filt(p)
      case _ => Nil()

  // can chain method calls now!
  println:
    seq.filt(_ > 10).size
    
    
  


