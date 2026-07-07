package e1.prompt;

import e1.model.Player;

public record BeginPrompt(Player player, String setting) implements StoryPrompt {
    private static final String TEMPLATE = """
            You are a narrative game master creating an engaging story adventure.
            The player '%s' with health %d and attack power %d is starting a new adventure.
            
            Setting: %s
            
            Generate a response as a JSON object with exactly these fields:
            {
              "narrative": "A vivid opening scene description of the adventure beginning.",
              "question": "An engaging question asking what the player wants to do.",
              "choices": ["Choice 1", "Choice 2", "Choice 3", "Choice 4"],
              "updatedPlayer": {
                "name": "%s",
                "health": %d,
                "attackPower": %d
              },
              "gameOver": false
            }
            
            Important:
            - The narrative should be immersive and set the scene.
            - The question should prompt the player for action.
            - Provide exactly 4 interesting and distinct choices.
            - The updatedPlayer should reflect the current state (unchanged at the start).
            - gameOver must be false.
            - Return ONLY the JSON object, no additional text.""";

    @Override
    public String toPromptString() {
        return String.format(TEMPLATE, player.name(), player.health(), player.attackPower(), 
                             setting, player.name(), player.health(), player.attackPower());
    }
}
