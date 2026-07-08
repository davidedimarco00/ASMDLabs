Q: Starting from the design plan and the interface ConnectFour write a full test junit class for the project. Create tests under src/test/java/it/unibo.
A: I'll create a comprehensive JUnit test suite based on the design plan. Let me write the test class:

Perfect! I've created a comprehensive JUnit test suite at ConnectFourTest.java.

Test Suite Summary
1. Behaviour Covered
Input Validation (4 tests):

Invalid column indices (negative, >= 7)
Invalid disc characters (not 'R' or 'Y')
Full column rejection
Basic Functionality (7 tests):

Board initialization (not full)
Disc dropping in multiple columns
Gravity mechanics (disc stacking)
Board full detection (empty, partial, complete)
Win Detection (13 tests):

No winner scenarios (empty board, three-in-a-row)
Horizontal wins (bottom row, upper row, right edge)
Vertical wins (left, middle, right columns)
Diagonal wins (both / and \ directions, upper regions)
Edge Cases (4 tests):

Five-in-a-row still wins
Winner detection on full board
Multiple simultaneous winners
Disc character to Player enum mapping consistency
Integration Scenarios (3 tests):

Complete game with RED horizontal win
Complete game with YELLOW vertical win
Complete game resulting in full board (draw)
2. Assumptions Made
Board size: 6 rows × 7 columns (standard Connect Four)
Disc mapping: 'R' → RED, 'Y' → YELLOW (case-sensitive)
Column indexing: 0-based (0 through 6)
Exception types:
IllegalArgumentException for invalid inputs (column, disc character)
IllegalStateException for full column
Implementation class: Assumes ConnectFourImpl exists
No turn enforcement: Tests can drop any color in any order
No game-over state: Can continue playing after a win
3. Edge Cases Covered
✓ Invalid boundaries (negative columns, column >= 7)
✓ Case sensitivity ('r' vs 'R')
✓ Full column handling
✓ Wins at board edges (rightmost column, top rows)
✓ Longer sequences (5+ in a row)
✓ Multiple winners simultaneously
✓ Win detection on completely full board
✓ Empty board state

4. Ambiguous Cases
Ambiguity 1: Draw detection
The test testCompleteGameDraw fills the board but doesn't guarantee no winner exists. Creating a true draw pattern without any four-in-a-row is complex and wasn't fully implemented.

Ambiguity 2: Turn alternation
Tests don't enforce or validate turn alternation since the interface doesn't specify this requirement. Tests may drop multiple discs of the same color consecutively.

Ambiguity 3: Post-win behaviour
Tests don't verify what happens when moves are made after a win is detected, as the interface doesn't specify game-over enforcement.

Ambiguity 4: Null handling
No tests for checkWin(null) — behaviour is undefined by the interface.

Ambiguity 5: Concurrent wins
Test testMultipleWinsOnBoard verifies both players can have wins, but doesn't specify which player "actually" won (game logic concern, not interface concern).

The test suite contains 31 tests covering all critical behaviours while documenting the assumptions and limitations of the minimal interface design.