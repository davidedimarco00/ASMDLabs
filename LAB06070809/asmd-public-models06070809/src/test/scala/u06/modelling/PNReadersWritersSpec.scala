package u06.modelling

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import u06.examples.PNReadersWriters.*

class PNReadersWritersSpec extends AnyFunSuite:

  // ========== BASIC STRUCTURAL TESTS ==========

  test("Initial marking should have K processes and 1 mutex"):
    val init = initialMarking(3)
    init(P1) shouldBe 3
    init(P5) shouldBe 1
    init(P2) shouldBe 0
    init(P3) shouldBe 0
    init(P4) shouldBe 0

  test("Initial marking should allow exactly K next states"):
    // From initial state, only t1 can fire K times (one per process)
    val init = initialMarking(3)
    val nexts = pnRW(3).next(init)
    nexts.size shouldBe 1 // Only one type of transition (t1)
    nexts.head shouldBe MSet.ofList(List(P1, P1, P2, P5))

  // ========== MUTUAL EXCLUSION PROPERTY ==========

  test("DEFINING PROPERTY: Mutual exclusion - no two writers can be active"):
    val init = initialMarking(2)

    // Check all reachable states up to depth 10
    val allPaths = pnRW(2).paths(init, 10)
    allPaths.foreach: path =>
      path.foreach: marking =>
        marking(P4) should be <= 1 // At most 1 writer

  test("DEFINING PROPERTY: Writer excludes readers (via inhibition)"):
    val init = initialMarking(2)

    val allPaths = pnRW(2).paths(init, 10)
    allPaths.foreach: path =>
      path.foreach: marking =>
        if hasWriters(marking) && hasReaders(marking) then
          // When writer is active (P4 > 0), readers in P3 cannot complete
          // The inhibition ^^^ MSet(P4) prevents transition t4 (P3 -> P6)
          val nexts = pnRW(2).next(marking)

          // None of the next states should have MORE completed readers
          // if reached in a SINGLE transition while writer was active
          nexts.foreach: next =>
            // If P6 increased, it means t4 fired, which shouldn't happen
            // when P4 > 0 in the source marking
            if next(P6) > marking(P6) then
              // This would mean t4 fired despite inhibition - should not happen
              fail(s"Reader completed despite writer active: $marking -> $next")

  test("DEFINING PROPERTY: Readers can complete when no writer is active"):
    val init = initialMarking(2)

    // Find states with readers but no writers
    val allPaths = pnRW(2).paths(init, 8)
    val statesWithReadersNoWriters = allPaths
      .flatMap(_.filter(m => hasReaders(m) && !hasWriters(m)))
      .toSet

    statesWithReadersNoWriters should not be empty

    // From these states, readers should be able to complete (P3 -> P6)
    statesWithReadersNoWriters.foreach: marking =>
      val nexts = pnRW(2).next(marking)
      // At least one transition should allow reader to complete
      val canComplete = nexts.exists(next => next(P6) > marking(P6))
      canComplete shouldBe true

  // ========== CONCURRENT READERS PROPERTY ==========

  test("DEFINING PROPERTY: Multiple readers can read concurrently"):
    val init = initialMarking(3)

    // Find a path where multiple readers are active
    val pathsWithMultiReaders = pnRW(3).paths(init, 8)
      .filter(_.exists(m => readerCount(m) >= 2))

    pathsWithMultiReaders should not be empty

  test("Concurrent readers scenario: 2 readers active simultaneously"):
    // Manually construct scenario
    val marking = MSet.ofList(List(P1, P3, P3, P5)) // 2 readers active

    readerCount(marking) shouldBe 2
    hasWriters(marking) shouldBe false
    mutexAvailable(marking) shouldBe true

    // Both readers should be able to progress
    val nexts = pnRW(3).next(marking)
    nexts should not be empty

  // ========== RESOURCE CONSERVATION ==========

  test("DEFINING PROPERTY: Total process count is conserved"):
    val k = 3
    val init = initialMarking(k)

    pnRW(k).paths(init, 8).foreach: path =>
      path.foreach: marking =>
        val totalProcesses = marking(P1) + marking(P2) + marking(P3) +
                            marking(P4) + marking(P6) + marking(P7)
        totalProcesses shouldBe k

  test("DEFINING PROPERTY: Mutex token is conserved"):
    val init = initialMarking(2)

    pnRW(2).paths(init, 8).foreach: path =>
      path.foreach: marking =>
        // Mutex is either in P5 or held by writer in P4
        marking(P5) + marking(P4) shouldBe 1

  // ========== LIVENESS PROPERTIES ==========

  test("From initial state, reading is eventually possible"):
    val init = initialMarking(2)

    // Should reach state with at least one reader
    val pathsWithReaders = pnRW(2).paths(init, 6)
      .filter(_.exists(m => readerCount(m) > 0))

    pathsWithReaders should not be empty

  test("From initial state, writing is eventually possible"):
    val init = initialMarking(2)

    val pathsWithWriters = pnRW(2).paths(init, 6)
      .filter(_.exists(m => hasWriters(m)))

    pathsWithWriters should not be empty

  // ========== NO-LOOPS VERSION TESTS ==========

  test("NO-LOOPS: System terminates when all processes complete"):
    val init = initialMarking(2)

    // In no-loops version, should reach normal form
    val completePaths = pnRWNoLoops(2).completePathsUpToDepth(init, 15)

    completePaths should not be empty

    // All complete paths should end with processes in P6 or P7
    completePaths.foreach: path =>
      val finalMarking = path.last
      finalMarking(P1) shouldBe 0 // No processes in pool
      finalMarking(P2) shouldBe 0 // No processes choosing
      finalMarking(P3) shouldBe 0 // No active readers
      finalMarking(P4) shouldBe 0 // No active writers
      // All in P6, P7, or both
      finalMarking(P6) + finalMarking(P7) shouldBe 2

  test("NO-LOOPS: Easier to verify termination property"):
    val init = initialMarking(1)

    // With 1 process, should have simple termination
    val sys = pnRWNoLoops(1)

    // Should reach normal form where process is in P6 or P7
    val ends = sys.completePathsUpToDepth(init, 10).map(_.last)

    ends.foreach: marking =>
      sys.normalForm(marking) shouldBe true
      marking(P6) + marking(P7) shouldBe 1

  // ========== SPECIFIC SCENARIO TESTS ==========

  test("Scenario: Reader path is possible"):
    val init = initialMarking(1)

    // Expected: P1,P5 -> P2,P5 -> P3,P5 -> P6,P5 -> P1,P5
    val expectedPath = List(
      MSet.ofList(List(P1, P5)),
      MSet.ofList(List(P2, P5)),
      MSet.ofList(List(P3, P5)),
      MSet.ofList(List(P6, P5)),
      MSet.ofList(List(P1, P5))
    )

    // Check this path is valid
    pnRW(1).paths(init, 5) should contain (expectedPath)

  test("Scenario: Writer path is possible"):
    val init = initialMarking(1)

    // Expected: P1,P5 -> P2,P5 -> P4 (mutex taken) -> P7,P5 -> P1,P5
    val expectedPath = List(
      MSet.ofList(List(P1, P5)),
      MSet.ofList(List(P2, P5)),
      MSet.ofList(List(P4)),        // Writer has mutex
      MSet.ofList(List(P7, P5)),
      MSet.ofList(List(P1, P5))
    )

    pnRW(1).paths(init, 5) should contain (expectedPath)

  test("Scenario: Two readers concurrent then writer"):
    val init = initialMarking(3)

    // Should find paths with pattern: 2 readers active, then writer
    val interestingPaths = pnRW(3).paths(init, 12).filter: path =>
      val hasStageWith2Readers = path.exists(m => readerCount(m) == 2)
      val hasStageWithWriter = path.exists(m => hasWriters(m))
      hasStageWith2Readers && hasStageWithWriter

    interestingPaths should not be empty

  // ========== PROPERTY-BASED TESTS ==========

  test("Property: No deadlock in reachable states"):
    val init = initialMarking(2)

    // Every non-final state should have at least one transition
    pnRW(2).paths(init, 8).foreach: path =>
      path.dropRight(1).foreach: marking => // All except last
        pnRW(2).next(marking) should not be empty

  test("Property: Writer cannot proceed without mutex"):
    val init = initialMarking(2)

    pnRW(2).paths(init, 10).foreach: path =>
      path.sliding(2).foreach:
        case List(m1, m2) =>
          // If writer becomes active in m2, mutex must have been consumed
          if m2(P4) > m1(P4) then
            m1(P5) shouldBe 1 // Mutex was available
            m2(P5) shouldBe 0 // Mutex is now taken
        case _ => // ignore

  test("Property: Readers can proceed without consuming mutex"):
    val init = initialMarking(2)

    pnRW(2).paths(init, 10).foreach: path =>
      path.sliding(2).foreach:
        case List(m1, m2) =>
          // If reader becomes active in m2, mutex should still be there
          if m2(P3) > m1(P3) then
            m1(P5) shouldBe m2(P5) // Mutex unchanged
        case _ => // ignore


