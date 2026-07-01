package it.unibo;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import it.unibo.engine.LLMStoryEngine;
import it.unibo.model.Player;
import it.unibo.model.Story;
import it.unibo.model.StoryImpl;
import it.unibo.view.ConsoleStoryView;
import it.unibo.view.StoryView;
public final class StoryApp {

    private StoryApp() { }

    public static void main(String[] args) {
        final ChatModel model = OllamaChatModel.builder()
            .baseUrl("localhost:11434")
            .modelName("qwen3.5:0.8b")
            .temperature(1.0)
            .build();
        final ChatModel chatModelWithGemini = GoogleAiGeminiChatModel.builder()
            .apiKey(System.getenv("GEMINI_AI_KEY"))
            .modelName("gemini-3.1-flash-lite-preview")
            .build();
        final Story story = new StoryImpl(
            new LLMStoryEngine(chatModelWithGemini),
            new Player("Aria", 100, 15),
            "A mysterious dungeon beneath an ancient castle"
        );
        final StoryView view = new ConsoleStoryView();
        gameLoop(story, view);
    }

    static void gameLoop(Story story, StoryView view) {
        while (!story.isGameOver()) {
            view.showBeat(
                story.narrative(),
                story.getPlayer(),
                story.currentQuestion(),
                story.choices()
            );
            final int choice = view.readChoice(story.choices().size());
            story.makeDecision(choice);
        }
        view.showBeat(story.narrative(), story.getPlayer(), story.currentQuestion(), story.choices());
        view.showGameOver(story.getPlayer());
    }
}
