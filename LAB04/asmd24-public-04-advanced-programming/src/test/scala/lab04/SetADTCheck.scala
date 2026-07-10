package scala.lab04

import org.scalacheck.Prop.{forAll, propBoolean}
import org.scalacheck.{Arbitrary, Gen, Properties}

import scala.lab04.SetADTs.{BasicSetADT, SetADT, TreeSetADT}

abstract class SetADTCheck(name: String) extends Properties(name):
  val setADT: SetADT
  import setADT.*

  // generating a small Int
  def smallInt(): Gen[Int] = Gen.choose(0, 10)
  // generating a Set of Int with approximate size (modulo clashes)
  def setGen[A: Arbitrary](size: Int): Gen[Set[A]] =
    if size == 0
      then Gen.const(empty())
    else for
      a <- Arbitrary.arbitrary[A]
      s <- setGen(size - 1)
    yield s.add(a)
  // a given instance to generate sets with small size
  given arb: Arbitrary[Set[Int]] = Arbitrary:
    for
      i <- smallInt()
      s <- setGen[Int](i)
    yield s

  property("commutativity of union") =
    forAll: (s1: Set[Int], s2: Set[Int]) =>
      (s1 || s2) === (s2 || s1)

  /**
    * axioms defining contains based on empty/add:
    * contains(empty, x) = false
    * contains(add(x,s), y) = (x == y) || contains(s, y)
  */

  property("axioms for contains") =
     forAll: (s: Set[Int], x: Int, y:Int) =>
        s.add(x).contains(y) == (x == y) || s.contains(y)
   &&
     forAll: (x: Int) =>
        !empty().contains(x)

/**
 * axioms defining union and remove:
 * union(empty, s) = s
 * union(add(x, s2), s) = add(x, union(s2, s)
 *
 *
 * remove(x, empty) = empty
 * remove(x, add(x, s)) = remove(x, s)
 * remove(x, add(y, s)) = add(y, remove(x, s)) if x!=y
 *
 * and so on: write axioms and correspondingly implement checks
 */

  property("axioms for union") =
    forAll: (s1: Set[Int], s2: Set[Int], x: Int) =>
      (s1 || empty()) === s1 &&
      (empty() || s2) === s2 &&
      (s1.add(x) || s2) === (s1 || s2).add(x)

  property("axioms for remove") =
  // remove(x, empty) = empty
    forAll: (x: Int) =>
      empty[Int]().remove(x) === empty()
  &&
  // remove(x, add(x, s)) = remove(x, s)
  forAll: (s: Set[Int], x: Int) =>
    s.add(x).remove(x) === s.remove(x)
  &&
    // remove(x, add(y, s)) = add(y, remove(x, s))  if x != y
    forAll: (s: Set[Int], x: Int, y: Int) =>
      (x != y) ==> (s.add(y).remove(x) === s.remove(x).add(y))

  // ALGEBRAIC PROPERTIES

  // Associativity of union: (A ∪ B) ∪ C = A ∪ (B ∪ C)
  property("associativity of union") =
    forAll: (s1: Set[Int], s2: Set[Int], s3: Set[Int]) =>
      ((s1 || s2) || s3) === (s1 || (s2 || s3))

  // Associativity of intersection: (A ∩ B) ∩ C = A ∩ (B ∩ C)
  property("associativity of intersection") =
    forAll: (s1: Set[Int], s2: Set[Int], s3: Set[Int]) =>
      ((s1 && s2) && s3) === (s1 && (s2 && s3))

  // Commutativity of intersection: A ∩ B = B ∩ A
  property("commutativity of intersection") =
    forAll: (s1: Set[Int], s2: Set[Int]) =>
      (s1 && s2) === (s2 && s1)

  // Idempotence of union: A ∪ A = A
  property("idempotence of union") =
    forAll: (s: Set[Int]) =>
      (s || s) === s

  // Idempotence of intersection: A ∩ A = A
  property("idempotence of intersection") =
    forAll: (s: Set[Int]) =>
      (s && s) === s

  // Identity for union: A ∪ ∅ = A
  property("identity for union") =
    forAll: (s: Set[Int]) =>
      (s || empty()) === s

  // CROSS-PROPERTY TESTS (relationships between union and intersection)

  // Distributivity of union over intersection: A ∪ (B ∩ C) = (A ∪ B) ∩ (A ∪ C)
  property("distributivity of union over intersection") =
    forAll: (s1: Set[Int], s2: Set[Int], s3: Set[Int]) =>
      (s1 || (s2 && s3)) === ((s1 || s2) && (s1 || s3))

  // Distributivity of intersection over union: A ∩ (B ∪ C) = (A ∩ B) ∪ (A ∩ C)
  property("distributivity of intersection over union") =
    forAll: (s1: Set[Int], s2: Set[Int], s3: Set[Int]) =>
      (s1 && (s2 || s3)) === ((s1 && s2) || (s1 && s3))

  // Absorption laws: A ∪ (A ∩ B) = A
  property("absorption law 1") =
    forAll: (s1: Set[Int], s2: Set[Int]) =>
      (s1 || (s1 && s2)) === s1

  // Absorption laws: A ∩ (A ∪ B) = A
  property("absorption law 2") =
    forAll: (s1: Set[Int], s2: Set[Int]) =>
      (s1 && (s1 || s2)) === s1

  // Subset relationship between sets
  property("subset relationship") =
    forAll: (s1: Set[Int], s2: Set[Int]) =>
      val intersection = s1 && s2
      val union = s1 || s2
      // intersection is always subset of both operands
      ((intersection || s1) === s1) && ((intersection || s2) === s2) &&
      // both operands are subsets of union
      ((s1 && union) === s1) && ((s2 && union) === s2)

  // Union contains both sets: A ⊆ (A ∪ B) and B ⊆ (A ∪ B)
  property("union contains both operands") =
    forAll: (s1: Set[Int], s2: Set[Int]) =>
      val union_set = s1 || s2
      (s1 && union_set) === s1 && (s2 && union_set) === s2
      
  



object BasicSetADTCheck extends SetADTCheck("SequenceBased Set"):
  val setADT: SetADT = BasicSetADT

  @main def visuallyCheckArbitrarySets =
    Range(0,20).foreach(i => println(summon[Arbitrary[setADT.Set[Int]]].arbitrary.sample))

object TreeSetADTCheck extends SetADTCheck("TreeBased Set"):
  val setADT: SetADT = TreeSetADT

  @main def visuallyCheckArbitrarySetsTree =
    Range(0,20).foreach(i => println(summon[Arbitrary[setADT.Set[Int]]].arbitrary.sample.map(_.toSequence())))
