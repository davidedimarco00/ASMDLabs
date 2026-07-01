package it.unibo.model;

import java.util.List;

public record StoryResponse(
    String narrative,
    String question,
    List<String> choices,
    Player updatedPlayer,
    boolean gameOver
) {}
