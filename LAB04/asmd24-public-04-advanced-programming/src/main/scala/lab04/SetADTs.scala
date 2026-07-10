package scala.lab04

import u04.datastructures.Sequences.*
import Sequence.*

object SetADTs:
  
  trait SetADT:
    type Set[A]
    def empty[A](): Set[A]
    extension [A](s: Set[A])
      def add(element: A): Set[A]
      def contains(a: A): Boolean
      def union(other: Set[A]): Set[A]
      def intersection(other: Set[A]): Set[A]
      infix def ||(other: Set[A]): Set[A] = s.union(other)
      infix def &&(other: Set[A]): Set[A] = s.intersection(other)
      def remove(a: A): Set[A]
      def toSequence(): Sequence[A]
      def size(): Int
      def ===(other: Set[A]): Boolean
    

  object BasicSetADT extends SetADT:

    opaque type Set[A] = Sequence[A]

    def empty[A](): Set[A] = Nil()

    extension [A](s: Set[A])
      def add(element: A): Set[A] = s match
        case Cons(h, _) if h == element => s
        case Cons(h, t)  => Cons(h, t.add(element))
        case _ => Cons(element, Nil())

      def remove(a: A): Set[A] = s.filter(_ != a)  

      def contains(a: A): Boolean = s match
        case Cons(h, t) => h == a || t.contains(a)
        case Nil() => false

      def toSequence(): Sequence[A] = s

      def union(s2: Set[A]): Set[A] = s2 match
        case Cons(h, t) => Cons(h, s.remove(h).union(t))
        case Nil() => s

      def intersection(s2: Set[A]): Set[A] = s match
        case Cons(h, t) if s2.contains(h) => Cons(h, t.intersection(s2.remove(h)))
        case Cons(_, t) => t.intersection(s2)
        case Nil() => Nil()

      def size(): Int = s match
        case Cons(_, t) => 1 + t.size()
        case Nil() => 0

      def ===(other: Set[A]): Boolean =
        s.union(other).size() == s.size()

  // Tree-based implementation using Binary Search Tree
  object TreeSetADT extends SetADT:
    
    // Binary Search Tree data structure
    enum Tree[A]:
      case Empty()
      case Node(value: A, left: Tree[A], right: Tree[A])
    
    import Tree.*

    opaque type Set[A] = Tree[A]

    def empty[A](): Set[A] = Empty()

    extension [A](s: Set[A])
      
      def add(element: A): Set[A] = s match
        case Empty() => Node(element, Empty(), Empty())
        case Node(v, l, r) if v == element => s
        case Node(v, l, r) => 
          // We need ordering, but for testing purposes we can use hash codes
          if element.hashCode() < v.hashCode() then
            Node(v, l.add(element), r)
          else
            Node(v, l, r.add(element))

      def contains(a: A): Boolean = s match
        case Empty() => false
        case Node(v, l, r) if v == a => true
        case Node(v, l, r) =>
          // Check both sides since hash collisions may cause elements on either side
          l.contains(a) || r.contains(a)

      def remove(a: A): Set[A] = s match
        case Empty() => Empty()
        case Node(v, l, r) if v == a =>
          // Remove node: merge left and right subtrees
          mergeTrees(l, r)
        case Node(v, l, r) =>
          // Need to check both sides due to potential hash collisions
          val newLeft = l.remove(a)
          val newRight = r.remove(a)
          if (newLeft == l) && (newRight == r) then s
          else Node(v, newLeft, newRight)

      private def mergeTrees(t1: Tree[A], t2: Tree[A]): Tree[A] = (t1, t2) match
        case (Empty(), t) => t
        case (t, Empty()) => t
        case (Node(v, l, r), t2) => Node(v, l, mergeTrees(r, t2))

      private def appendSequences(s1: Sequence[A], s2: Sequence[A]): Sequence[A] = s1 match
        case Nil() => s2
        case Cons(h, t) => Cons(h, appendSequences(t, s2))

      def toSequence(): Sequence[A] = s match
        case Empty() => Nil()
        case Node(v, l, r) =>
          // In-order traversal
          appendSequences(l.toSequence(), Cons(v, r.toSequence()))

      def union(s2: Set[A]): Set[A] = s match
        case Empty() => s2
        case Node(v, l, r) =>
          // Add current value to s2, then union left and right
          l.union(r.union(s2.add(v)))

      def intersection(s2: Set[A]): Set[A] =
        // Convert to sequences, filter, and convert back
        // This ensures correctness even with hash collisions
        def fromSequence(seq: Sequence[A]): Set[A] = seq match
          case Nil() => Empty()
          case Cons(h, t) => fromSequence(t).add(h)
        
        def filterSeq(seq: Sequence[A], other: Set[A]): Sequence[A] = seq match
          case Nil() => Nil()
          case Cons(h, t) if other.contains(h) => Cons(h, filterSeq(t, other))
          case Cons(_, t) => filterSeq(t, other)
        
        fromSequence(filterSeq(s.toSequence(), s2))

      def size(): Int = s match
        case Empty() => 0
        case Node(_, l, r) => 1 + l.size() + r.size()

      def ===(other: Set[A]): Boolean =
        // Two sets are equal if they have same size and union doesn't add elements
        s.size() == other.size() && s.union(other).size() == s.size()


@main def trySetADTModule =
  import SetADTs.*
  val setADT: SetADT = BasicSetADT
  import setADT.*

  val s1: Set[Int] = empty().add(10).add(20).add(30)
  val s2: Set[Int] = empty().add(10).add(11)
  // val s3: Set[Int] = Cons(10, Nil()) // because Set is defined opaque
  println(s1.toSequence()) // (10, 20, 30)
  println(s2.toSequence()) // (10, 11)
  println(s1.union(s2).toSequence()) // (10, 20, 30, 11)
  println(s1.intersection(s2).toSequence()) // (10)