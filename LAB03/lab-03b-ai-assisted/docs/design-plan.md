# Connect Four Implementation Design Plan

## 1. Interface Analysis

The `ConnectFour` interface provides a minimal API for a Connect Four game implementation:

```java
package it.unibo;

public interface ConnectFour {
    enum Player {
        RED, YELLOW
    }
    void dropDisc(int column, char disc);
    boolean checkWin(Player player);
    boolean isBoardFull();
}
```

### Key Observations

- **Player enum**: Defines two players (RED, YELLOW)
- **dropDisc**: Places a disc in a column (void return, no validation feedback)
- **checkWin**: Checks if a specific player has won
- **isBoardFull**: Checks if the board is completely filled

### Critical Type Mismatch

The interface exhibits a significant inconsistency:
- `dropDisc(int column, char disc)` uses a `char` parameter
- `checkWin(Player player)` uses the `Player` enum

This suggests the implementation must handle a mapping between `char` and `Player`.

---

## 2. Assumptions

Based on standard Connect Four rules, we assume:

1. **Board dimensions**: 6 rows × 7 columns (standard Connect Four)
2. **Win condition**: 4 consecutive discs (horizontal, vertical, or diagonal)
3. **Gravity**: Discs fall to the lowest available row in a column
4. **Column indexing**: 0-based indexing (columns 0-6)
5. **Disc characters**: 
   - 'R' represents RED player
   - 'Y' represents YELLOW player
6. **Turn management**: Not enforced by the interface, but logical turns should alternate
7. **Game state**: The implementation must maintain the board state internally
8. **Invalid moves**: Since `dropDisc` returns void, invalid moves must be handled via exceptions or ignored

---

## 3. Ambiguities

### 3.1 Character-to-Player Mapping

**Ambiguity**: How should `char disc` map to `Player` enum?

**Proposed resolution**:
- 'R' → Player.RED
- 'Y' → Player.YELLOW
- Any other character → throw `IllegalArgumentException`

### 3.2 Invalid Column Handling

**Ambiguity**: What happens when `dropDisc` is called with an invalid column (< 0 or >= 7)?

**Proposed resolution**:
- Throw `IllegalArgumentException` with descriptive message
- Alternative: silently ignore (not recommended for debugging)

### 3.3 Full Column Handling

**Ambiguity**: What happens when `dropDisc` is called on a full column?

**Proposed resolution**:
- Throw `IllegalStateException` indicating the column is full
- Caller is responsible for checking column availability

### 3.4 Turn Validation

**Ambiguity**: Should the implementation enforce turn alternation?

**Proposed resolution**:
- **Option A** (recommended): Do NOT enforce turns at the interface level
  - Allows flexible testing and usage
  - Document that callers should manage turns
- **Option B**: Track current player and throw exception on incorrect turn
  - More game-like, but reduces flexibility

**Decision**: Option A for simplicity and testability

### 3.5 Game-Over State

**Ambiguity**: Can moves be made after a win?

**Proposed resolution**:
- Allow moves after a win (no game-over enforcement)
- Caller is responsible for checking win condition and stopping
- Simpler implementation, more flexible for testing

### 3.6 Win Detection Scope

**Ambiguity**: Does `checkWin(player)` check only the last move, or the entire board?

**Proposed resolution**:
- Scan the entire board for any winning configuration for the given player
- More expensive but simpler and more robust

---

## 4. Proposed Classes

### 4.1 `ConnectFourImpl` (implements `ConnectFour`)

**Responsibilities**:
- Maintain the game board state
- Implement `dropDisc`, `checkWin`, and `isBoardFull` methods
- Validate input parameters
- Map `char` to `Player`

**Fields**:
- `char[][] board` — 2D array representing the board (6 rows × 7 columns)
- `int ROWS = 6` — constant for board height
- `int COLS = 7` — constant for board width
- `int WIN_LENGTH = 4` — constant for winning sequence length
- `char EMPTY = '.'` — constant for empty cell

**Constructor**:
- Initialize board with all cells set to `EMPTY`

### 4.2 Helper Classes (Optional)

For a simple lab, no additional classes are strictly necessary. However, consider:

- **`Position`**: Value object for (row, col) coordinates
  - Useful for win detection algorithms
  - Can be a simple record (Java 16+) or inner class

---

## 5. Responsibilities

### ConnectFourImpl

| Method | Responsibility |
|--------|----------------|
| `dropDisc(int column, char disc)` | Validate column and disc; find lowest available row; place disc; throw exceptions on invalid input |
| `checkWin(Player player)` | Scan entire board for 4 consecutive discs of the given player (horizontal, vertical, diagonal) |
| `isBoardFull()` | Check if all cells in the top row are non-empty |
| `charToPlayer(char disc)` | Private helper: convert 'R'/'Y' to Player.RED/YELLOW |
| `playerToChar(Player player)` | Private helper: convert Player enum to 'R'/'Y' |

### Additional Private Methods

- `isValidColumn(int column)`: Check if column is in range [0, 6]
- `findLowestRow(int column)`: Find the lowest empty row in a column (or -1 if full)
- `checkDirection(int row, int col, int dRow, int dCol, char disc)`: Check for 4 consecutive discs in a specific direction

---

## 6. Game Invariants

The implementation must maintain these invariants:

1. **Board integrity**: The board always has exactly 6 rows and 7 columns
2. **Cell states**: Each cell contains either 'R', 'Y', or '.' (empty)
3. **Gravity**: No empty cells below occupied cells in any column
4. **Valid operations**: All public methods must handle invalid inputs gracefully

---

## 7. Edge Cases

### Input Validation

| Case | Expected Behavior |
|------|-------------------|
| `dropDisc(-1, 'R')` | Throw `IllegalArgumentException` ("Invalid column: -1") |
| `dropDisc(7, 'Y')` | Throw `IllegalArgumentException` ("Invalid column: 7") |
| `dropDisc(0, 'X')` | Throw `IllegalArgumentException` ("Invalid disc: X") |
| `dropDisc(0, 'R')` on full column | Throw `IllegalStateException` ("Column 0 is full") |

### Win Detection

| Case | Expected Behavior |
|------|-------------------|
| 4 horizontal discs | `checkWin(player)` returns `true` |
| 4 vertical discs | `checkWin(player)` returns `true` |
| 4 diagonal (/) discs | `checkWin(player)` returns `true` |
| 4 diagonal (\) discs | `checkWin(player)` returns `true` |
| 3 in a row | `checkWin(player)` returns `false` |
| 5 in a row | `checkWin(player)` returns `true` |
| Empty board | `checkWin(player)` returns `false` |

### Board State

| Case | Expected Behavior |
|------|-------------------|
| Empty board | `isBoardFull()` returns `false` |
| Partially filled | `isBoardFull()` returns `false` |
| All 42 cells filled | `isBoardFull()` returns `true` |
| Top row full, bottom rows empty | `isBoardFull()` returns `true` (since discs would fall) |

### Game Progression

| Case | Handling |
|------|----------|
| Move after win | Allowed (no enforcement) |
| Checking win before any move | Returns `false` |
| Multiple wins on board | `checkWin` returns `true` for each winning player |

---

## 8. Implementation Plan

### Phase 1: Basic Structure

1. Create `ConnectFourImpl` class implementing `ConnectFour`
2. Define constants (ROWS, COLS, WIN_LENGTH, EMPTY)
3. Initialize board in constructor
4. Implement `charToPlayer` and `playerToChar` helper methods

### Phase 2: Core Game Logic

1. Implement `dropDisc`:
   - Validate column range
   - Validate disc character
   - Find lowest available row using `findLowestRow`
   - Place disc or throw exception if column full

2. Implement `isBoardFull`:
   - Check if top row has any empty cells
   - Return true only if all cells in row 0 are non-empty

### Phase 3: Win Detection

1. Implement `checkWin`:
   - Convert Player to char using `playerToChar`
   - Scan all board positions
   - For each position with the player's disc, check 4 directions:
     - Horizontal (right)
     - Vertical (down)
     - Diagonal down-right
     - Diagonal down-left
   - Return true if any direction has 4 consecutive discs

2. Implement `checkDirection` helper:
   - Given starting position and direction vector (dRow, dCol)
   - Count consecutive matching discs
   - Return true if count >= WIN_LENGTH

### Phase 4: Polish

1. Add comprehensive JavaDoc comments
2. Document assumptions clearly
3. Ensure exception messages are descriptive
4. Consider adding a `toString()` method for debugging (not in interface, but useful)

---

## 9. Testing Strategy

### 9.1 Unit Test Categories

#### A. Input Validation Tests

```
testDropDiscInvalidColumnNegative()
testDropDiscInvalidColumnTooHigh()
testDropDiscInvalidCharacter()
testDropDiscOnFullColumn()
```

#### B. Basic Functionality Tests

```
testDropDiscEmptyBoard()
testDropDiscMultipleDiscs()
testDropDiscGravity() // Discs stack correctly
testIsBoardFullOnEmptyBoard()
testIsBoardFullPartiallyFilled()
testIsBoardFullCompletelyFilled()
```

#### C. Win Detection Tests - Horizontal

```
testCheckWinHorizontal()
testCheckWinHorizontalMultipleRows()
testNoWinThreeInRow()
```

#### D. Win Detection Tests - Vertical

```
testCheckWinVertical()
testCheckWinVerticalMultipleColumns()
```

#### E. Win Detection Tests - Diagonal

```
testCheckWinDiagonalDownRight()
testCheckWinDiagonalDownLeft()
testCheckWinDiagonalUpRight()
testCheckWinDiagonalUpLeft()
```

#### F. Edge Cases

```
testCheckWinEmptyBoard()
testCheckWinNoWinner()
testCheckWinAfterBoardFull()
testMultipleWinsOnBoard()
testFiveInRowStillWins()
```

#### G. Integration Tests

```
testCompleteGameRedWins()
testCompleteGameYellowWins()
testCompleteGameDraw()
```

### 9.2 Test Data Strategy

- Use **parameterized tests** for similar scenarios (e.g., all diagonal directions)
- Create **helper methods** to set up board states:
  - `setupBoard(String... rows)` — create board from string representation
  - `fillColumn(int column, int count, char disc)` — fill column with discs
- Use **clear naming** that describes the scenario being tested

### 9.3 Assertion Strategy

- Use JUnit 5 (`org.junit.jupiter.api.*`)
- Use `assertThrows` for exception validation
- Use `assertTrue/assertFalse` for win/full board checks
- Consider custom assertion messages for clarity

### 9.4 Test Independence

- Each test should create a fresh `ConnectFourImpl` instance
- No shared state between tests
- Tests should be runnable in any order

---

## 10. Design Rationale

### Simplicity Over Features

This design prioritizes simplicity because:
- It's a university lab project (limited scope)
- The interface is minimal (no turn management, no game state queries)
- Easy to understand and maintain
- Suitable for demonstrating OO principles and testing

### Flexibility Over Enforcement

Design decisions favor flexibility:
- No turn enforcement → easier to test specific scenarios
- No game-over state → can test win detection independently
- Allow moves after win → simpler state management

### Exception-Based Error Handling

Using exceptions for invalid inputs because:
- `dropDisc` returns void (no other way to signal errors)
- Fail-fast approach helps catch bugs early
- Clear error messages improve debugging

### Trade-offs

| Choice | Pro | Con |
|--------|-----|-----|
| Scan entire board for win | Simple, no state tracking | Less efficient |
| No turn tracking | Flexible, testable | Less realistic |
| Exception on invalid move | Clear error handling | No graceful degradation |
| char-Player mapping | Matches interface | Type inconsistency preserved |

---

## 11. Open Questions for Implementation

1. **Character case sensitivity**: Should 'r' be treated the same as 'R'?
   - **Recommendation**: No, strict case matching ('R' and 'Y' only)

2. **Board visualization**: Should we add a `toString()` method?
   - **Recommendation**: Yes, extremely useful for debugging, not part of interface

3. **Performance optimization**: Should we optimize win detection to check only around the last move?
   - **Recommendation**: No, keep it simple; full board scan is acceptable for 6×7 board

4. **Null handling**: What if `checkWin(null)` is called?
   - **Recommendation**: Throw `NullPointerException` with message or let it fail naturally

---

## Summary

This design provides a straightforward implementation path for the `ConnectFour` interface while clearly documenting all assumptions and ambiguities. The proposed `ConnectFourImpl` class will be simple, testable, and appropriate for a university lab context.

The key design principles are:
- **Clarity**: Simple, readable code
- **Robustness**: Proper input validation with exceptions
- **Testability**: No hidden state, deterministic behavior
- **Documentation**: All assumptions clearly stated

The most significant design challenge—the char/Player type mismatch—is handled with explicit mapping methods and clear documentation for maintainers.
