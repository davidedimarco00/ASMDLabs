package it.unibo.model;

import it.unibo.engine.StoryEngine;
import it.unibo.prompt.AdvancePrompt;
import it.unibo.prompt.BeginPrompt;
import it.unibo.prompt.StoryPrompt;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class StoryImpl implements Story {

    private final StoryEngine engine;
    private Player player;
    private StoryResponse currentBeat;

    public StoryImpl(StoryEngine engine, Player initialPlayer, String setting) {
        this.engine = Objects.requireNonNull(engine);
        this.player = Objects.requireNonNull(initialPlayer);
        if (setting == null || setting.trim().isEmpty()) {
            throw new IllegalArgumentException("Setting cannot be null or empty");
        }
        this.currentBeat = engine.request(new BeginPrompt(initialPlayer, setting));
        this.player = Objects.requireNonNull(currentBeat.updatedPlayer());
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public String narrative() {
        return currentBeat.narrative();
    }

    @Override
    public String currentQuestion() {
        return currentBeat.question();
    }

    @Override
    public List<String> choices() {
        return currentBeat.gameOver()
            ? Collections.emptyList()
            : Collections.unmodifiableList(currentBeat.choices());
    }

    @Override public boolean isGameOver() { return currentBeat.gameOver(); }

    @Override
    public void makeDecision(int decision) {
        if (isGameOver()) throw new IllegalStateException("The story is over.");
        final List<String> available = currentBeat.choices();
        if (decision < 0 || decision >= available.size()) {
            throw new IllegalArgumentException(
                "Invalid choice: " + decision + " (valid: 0.." + (available.size() - 1) + ")"
            );
        }
        final String chosen = available.get(decision);
        final StoryPrompt prompt = new AdvancePrompt(player, currentBeat.question(), chosen);
        currentBeat = engine.request(prompt);
        player = currentBeat.updatedPlayer();
    }
}
