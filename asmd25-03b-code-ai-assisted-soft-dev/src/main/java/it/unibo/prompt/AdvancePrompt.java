package it.unibo.prompt;

import it.unibo.model.Player;

public record AdvancePrompt(
    Player player,
    String previousQuestion,
    String chosenAction
) implements StoryPrompt {

    private static final String TEMPLATE = "TODO";

    @Override
    public String toPromptString() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
