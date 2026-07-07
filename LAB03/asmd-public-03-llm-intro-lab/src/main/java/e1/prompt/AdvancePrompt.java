package e1.prompt;

import e1.model.Player;

public record AdvancePrompt(
    Player player,
    String previousQuestion,
    String chosenAction
) implements StoryPrompt {

    private static final String TEMPLATE = """
            You are a narrative game master continuing an interactive story adventure.
            The player '%s' (health: %d, attack power: %d) just performed the following action:
            
            Previous Question: %s
            Player's Action: %s
            
            Based on the player's action, continue the story. Generate a response as a JSON object with exactly these fields:
            {
              "narrative": "A description of what happens as a result of the player's action.",
              "question": "An engaging question asking what the player wants to do next.",
              "choices": ["Choice 1", "Choice 2", "Choice 3", "Choice 4"],
              "updatedPlayer": {
                "name": "%s",
                "health": %d,
                "attackPower": %d
              },
              "gameOver": false
            }
            
            Important:
            - The narrative should describe the consequences of the player's action.
            - Update the player's stats (health, attackPower) based on what happened in the story.
            - Provide exactly 4 interesting and distinct choices for the next action.
            - Only set gameOver to true if the player's health reaches 0 or below, or if a winning condition is met.
            - The updatedPlayer name should remain the same as the current player's name.
            - Return ONLY the JSON object, no additional text.""";

    @Override
    public String toPromptString() {
        return String.format(TEMPLATE, player.name(), player.health(), player.attackPower(),
                             previousQuestion, chosenAction, player.name(), player.health(), player.attackPower());
    }
}
