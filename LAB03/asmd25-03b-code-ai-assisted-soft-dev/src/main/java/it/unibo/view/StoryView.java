package it.unibo.view;

import it.unibo.model.Player;

import java.util.List;

public interface StoryView {
    void showBeat(String narrative, Player player, String question, List<String> choices);
    void showGameOver(Player player);
    int readChoice(int numChoices);
}
