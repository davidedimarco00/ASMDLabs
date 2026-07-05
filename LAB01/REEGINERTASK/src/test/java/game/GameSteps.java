package game;

import io.cucumber.java.en.*;

public class GameSteps {
    private Logic logic;
    private int boardSize = 5;

    @Given("I have a game board of size {int}")
    public void iHaveAGameBoardOfSize(int size) {
        this.boardSize = size;
        this.logic = new LogicImpl(size);
    }

    @When("I hit position \\({int},{int}\\)")
    public void iHitPosition(int x, int y) {
        this.logic.hit(new Position(x, y));
    }

    @Then("the game should not be over")
    public void theGameShouldNotBeOver() {
        if (logic.isOver()) {
            throw new IllegalStateException("Game should not be over");
        }
    }
 
    @Then("the game should be over")
    public void theGameShouldBeOver() {
        if (!logic.isOver()) {
            throw new IllegalStateException("Game should be over");
        }
    }

    @Then("position \\({int},{int}\\) should have mark {int}")
    public void positionShouldHaveMark(int x, int y, int expected) {
        var actual = this.logic.getMark(new Position(x, y));
        if (actual.isEmpty() || actual.get() != expected) {
            throw new IllegalStateException("Expected mark " + expected + " at (" + x + "," + y + ") but was " + actual.orElse(null));
        }
    }

    @Then("position \\({int},{int}\\) should be empty")
    public void positionShouldBeEmpty(int x, int y) {
        var actual = this.logic.getMark(new Position(x, y));
        if (actual.isPresent()) {
            throw new IllegalStateException("Expected empty at (" + x + "," + y + ") but found mark " + actual.get());
        }
    }
}

