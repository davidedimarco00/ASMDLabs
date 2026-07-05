Feature: Game logic
  The game should add marks, start moving when hitting a neighbour, and end when a mark exits the board.

  Scenario: Board starts not over
    Given I have a game board of size 5
    Then the game should not be over

  Scenario: Placing a first mark does not end the game
    Given I have a game board of size 5
    When I hit position (2,2)
    Then the game should not be over

  Scenario: Trigger movement by hitting a neighbouring cell
    Given I have a game board of size 5
    When I hit position (2,2)
    And I hit position (2,2)
    Then the game should not be over

  Scenario: Game ends when a mark moves off the board
    Given I have a game board of size 2
    When I hit position (1,1)
    And I hit position (1,1)
    Then the game should be over
