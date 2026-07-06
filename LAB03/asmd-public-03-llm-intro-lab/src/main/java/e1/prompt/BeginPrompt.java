package e1.prompt;

import e1.model.Player;

public record BeginPrompt(Player player, String setting) implements StoryPrompt {
    private static final String TEMPLATE = "TODO";

    @Override
    public String toPromptString() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
