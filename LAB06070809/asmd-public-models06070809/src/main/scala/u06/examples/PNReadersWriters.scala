package u06.examples

export u06.modelling.PetriNet
import u06.utils.MSet

object PNReadersWriters:

  enum Place:
    case P1, P2, P3, P4, P5, P6, P7

  export Place.*
  export u06.modelling.PetriNet.*
  export u06.modelling.SystemAnalysis.*
  export u06.utils.MSet

  /**
   * Readers/Writers Petri Net with K initial processes
   *
   * Semantics:
   * - P1: Pool of available processes (K tokens)
   * - P2: Process ready to choose read/write
   * - P3: Process reading
   * - P4: Process writing
   * - P5: Mutex controller (1 token - ensures writer exclusion)
   * - P6: Reader completed
   * - P7: Writer completed
   *
   * Key properties:
   * - Multiple readers can read concurrently
   * - Writers have mutual exclusion (controlled by P5)
   * - Processes loop back to P1 after completion
   */
  def pnRW(k: Int = 3) = PetriNet[Place](
    // t1: Process takes from pool
    MSet(P1) ~~> MSet(P2),

    // t2: Choose to read (multiple readers allowed)
    MSet(P2) ~~> MSet(P3),

    // t3: Choose to write (requires mutex)
    MSet(P2, P5) ~~> MSet(P4),

    // t4: Reader finishes (inhibited by writers)
    MSet(P3) ~~> MSet(P6) ^^^ MSet(P4),

    // t5: Writer finishes (releases mutex)
    MSet(P4) ~~> MSet(P5, P7),

    // t6: Reader returns to pool
    MSet(P6) ~~> MSet(P1),

    // t7: Writer returns to pool
    MSet(P7) ~~> MSet(P1)
  ).toSystem

  /**
   * Version WITHOUT loops (no P6->P1, P7->P1 arcs)
   * Useful for testing termination and validity
   */
  def pnRWNoLoops(k: Int = 3) = PetriNet[Place](
    MSet(P1) ~~> MSet(P2),
    MSet(P2) ~~> MSet(P3),
    MSet(P2, P5) ~~> MSet(P4),
    MSet(P3) ~~> MSet(P6) ^^^ MSet(P4),
    MSet(P4) ~~> MSet(P5, P7)
    // No loops back to P1
  ).toSystem

  // Helper: initial marking with K processes and 1 mutex
  def initialMarking(k: Int): MSet[Place] =
    MSet.ofList(List.fill(k)(P1) :+ P5)

  // Helper: check if marking has writers active
  def hasWriters(m: MSet[Place]): Boolean = m(P4) > 0

  // Helper: check if marking has readers active
  def hasReaders(m: MSet[Place]): Boolean = m(P3) > 0

  // Helper: count active readers
  def readerCount(m: MSet[Place]): Int = m(P3)

  // Helper: check if mutex is available
  def mutexAvailable(m: MSet[Place]): Boolean = m(P5) > 0

@main def mainPNReadersWriters(): Unit =
  import PNReadersWriters.*

  val initial = initialMarking(2)
  println(s"Initial marking: $initial")
  println(s"Possible next states: ${pnRW(2).next(initial)}")

  // Show some paths
  println("\nSample paths (length 5):")
  pnRW(2).paths(initial, 5).take(10).foreach(println)

  // Demonstrate mutual exclusion
  println("\nDemonstrating mutual exclusion:")
  val pathsWith2Procs = pnRW(2).paths(initial, 10)
  val statesWithWriters = pathsWith2Procs.flatMap(_.filter(hasWriters))
  println(s"States with writers (first 5):")
  statesWithWriters.take(5).foreach(m => println(s"  $m - Writers: ${m(P4)}, Readers: ${readerCount(m)}"))

  // Demonstrate concurrent readers
  println("\nDemonstrating concurrent readers:")
  val initial3 = initialMarking(3)
  val pathsWith3Procs = pnRW(3).paths(initial3, 10)
  val statesWithMultiReaders = pathsWith3Procs.flatMap(_.filter(m => readerCount(m) >= 2))
  println(s"States with 2+ readers (first 5):")
  statesWithMultiReaders.take(5).foreach(m => println(s"  $m - Readers: ${readerCount(m)}"))

  // Show no-loops version termination
  println("\nNo-loops version - complete paths:")
  val completePaths = pnRWNoLoops(2).completePathsUpToDepth(initial, 15)
  println(s"Found ${completePaths.size} complete paths")
  completePaths.take(3).foreach: path =>
    println(s"\nPath length ${path.size}:")
    path.foreach(m => println(s"  $m"))
