package it.unibo.prompt;

import it.unibo.model.Player;

public record BeginPrompt(Player player, String setting) implements StoryPrompt {
    @Override
    public String toPromptString() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
