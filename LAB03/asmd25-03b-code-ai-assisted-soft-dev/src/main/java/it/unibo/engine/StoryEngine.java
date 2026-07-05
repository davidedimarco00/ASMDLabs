package it.unibo.engine;

import it.unibo.model.StoryResponse;
import it.unibo.prompt.StoryPrompt;

public interface StoryEngine {
    StoryResponse request(StoryPrompt prompt);
}
