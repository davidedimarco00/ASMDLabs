Feature: Game logic
  The game should add marks, start moving when hitting a neighbour, and end when a mark exits the board.

  Background:
    Given I have a game board of size 5

  Scenario: Placing multiple marks assigns increasing numbers
    When I hit position (1,1)
    And I hit position (3,3)
    Then position (1,1) should have mark 1
    And position (3,3) should have mark 2
    And the game should not be over

  Scenario: Movement shifts marks on neighbour hit
    When I hit position (2,2)
    And I hit position (2,2)
    Then position (3,1) should have mark 1
    And position (2,2) should be empty
    And the game should not be over

  Scenario: Hitting non-neighbour does not trigger movement
    When I hit position (0,0)
    And I hit position (4,4)
    Then position (0,0) should have mark 1
    And position (4,4) should have mark 2
    And the game should not be over

  Scenario: Movement can end the game on small board
    Given I have a game board of size 2
    When I hit position (1,1)
    And I hit position (1,1)
    Then the game should be over
