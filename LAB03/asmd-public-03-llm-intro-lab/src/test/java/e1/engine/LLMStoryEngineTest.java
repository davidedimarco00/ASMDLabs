package e1.engine;

import dev.langchain4j.model.chat.ChatModel;
import e1.model.Player;
import e1.model.StoryResponse;
import e1.prompt.StoryPrompt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link LLMStoryEngine}.
 *
 * Tests verify that the engine correctly handles the retry logic and JSON decoding
 * for various scenarios: happy path, retry and recovery, exhausted retries, and malformed JSON.
 */
@ExtendWith(MockitoExtension.class)
class LLMStoryEngineTest {

    private static final String VALID_JSON_RESPONSE = """
            {
              "narrative": "You enter a dark forest.",
              "question": "What do you do?",
              "choices": ["Go left", "Go right", "Climb tree", "Rest"],
              "updatedPlayer": {
                "name": "Hero",
                "health": 100,
                "attackPower": 15
              },
              "gameOver": false
            }
            """;

    private static final String GARBAGE_RESPONSE = "This is not JSON at all!";

    private static final String MALFORMED_JSON = """
            {
              "narrative": "You enter a dark forest.",
              "question": "What do you do?",
              "choices": ["Go left", "Go right"],
              "updatedPlayer": {
                "name": "Hero",
                "health": 100,
            }
            """;

    @Mock
    private ChatModel mockChatModel;

    @Mock
    private StoryPrompt mockPrompt;

    private LLMStoryEngine engine;
    private JsonCodec codec;

    @BeforeEach
    void setUp() {
        codec = new JsonCodec();
        engine = new LLMStoryEngine(mockChatModel, codec, 3);

        when(mockPrompt.toPromptString()).thenReturn("Test prompt");
    }

    /**
     * Scenario 1: Happy path — the model returns valid JSON on the first attempt.
     * The engine should parse it correctly and return a StoryResponse without retrying.
     */
    @Test
    void testHappyPath_ValidJsonOnFirstAttempt() {
        when(mockChatModel.chat(anyString())).thenReturn(VALID_JSON_RESPONSE);

        StoryResponse response = engine.request(mockPrompt);

        assertNotNull(response);
        assertEquals("You enter a dark forest.", response.narrative());
        assertEquals("What do you do?", response.question());
        assertEquals(4, response.choices().size());
        assertEquals("Hero", response.updatedPlayer().name());
        assertEquals(100, response.updatedPlayer().health());
        assertEquals(15, response.updatedPlayer().attackPower());
        assertFalse(response.gameOver());

        // Verify the model was called exactly once
        verify(mockChatModel, times(1)).chat(anyString());
    }

    /**
     * Scenario 2: Retry and recover — the model returns garbage on the first attempt(s)
     * but valid JSON on a later attempt. The engine should retry and eventually succeed.
     */
    @Test
    void testRetryAndRecover_ValidJsonAfterGarbage() {
        // First two attempts fail with garbage, third succeeds with valid JSON
        when(mockChatModel.chat(anyString()))
                .thenReturn(GARBAGE_RESPONSE)
                .thenReturn(GARBAGE_RESPONSE)
                .thenReturn(VALID_JSON_RESPONSE);

        StoryResponse response = engine.request(mockPrompt);

        assertNotNull(response);
        assertEquals("You enter a dark forest.", response.narrative());
        assertEquals("What do you do?", response.question());

        // Verify the model was called 3 times (2 failures + 1 success)
        verify(mockChatModel, times(3)).chat(anyString());
    }

    /**
     * Scenario 3: All retries exhausted — the model never returns valid JSON.
     * The engine should throw IllegalStateException after exhausting all retries.
     */
    @Test
    void testAllRetriesExhausted_NeverValidJson() {
        // All attempts fail with garbage
        when(mockChatModel.chat(anyString())).thenReturn(GARBAGE_RESPONSE);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> engine.request(mockPrompt)
        );

        assertEquals("Failed to get a valid response after 3 attempts", exception.getMessage());

        // Verify the model was called exactly 3 times (all retries used)
        verify(mockChatModel, times(3)).chat(anyString());
    }

    /**
     * Scenario 4: Malformed JSON — the model returns syntactically invalid JSON.
     * The engine should treat it as a failed attempt and retry.
     */
    @Test
    void testMalformedJson_RetriesOnInvalidJson() {
        // First two attempts fail with malformed JSON, third succeeds with valid JSON
        when(mockChatModel.chat(anyString()))
                .thenReturn(MALFORMED_JSON)
                .thenReturn(MALFORMED_JSON)
                .thenReturn(VALID_JSON_RESPONSE);

        StoryResponse response = engine.request(mockPrompt);

        assertNotNull(response);
        assertEquals("You enter a dark forest.", response.narrative());

        // Verify the model was called 3 times (2 failures + 1 success)
        verify(mockChatModel, times(3)).chat(anyString());
    }

    /**
     * Additional test: Verify that the engine passes the correct prompt string to the model.
     */
    @Test
    void testPromptStringPassedToModel() {
        String expectedPrompt = "Generate a story";
        when(mockPrompt.toPromptString()).thenReturn(expectedPrompt);
        when(mockChatModel.chat(expectedPrompt)).thenReturn(VALID_JSON_RESPONSE);

        engine.request(mockPrompt);

        // Verify that the model was called with the exact prompt string
        verify(mockChatModel).chat(expectedPrompt);
    }

    /**
     * Additional test: Verify that the engine correctly parses all fields of StoryResponse.
     */
    @Test
    void testAllFieldsParsedCorrectly() {
        String customJson = """
                {
                  "narrative": "Custom narrative",
                  "question": "Custom question",
                  "choices": ["Choice A", "Choice B", "Choice C"],
                  "updatedPlayer": {
                    "name": "TestHero",
                    "health": 50,
                    "attackPower": 25
                  },
                  "gameOver": true
                }
                """;

        when(mockChatModel.chat(anyString())).thenReturn(customJson);

        StoryResponse response = engine.request(mockPrompt);

        assertEquals("Custom narrative", response.narrative());
        assertEquals("Custom question", response.question());
        assertEquals(List.of("Choice A", "Choice B", "Choice C"), response.choices());
        assertEquals("TestHero", response.updatedPlayer().name());
        assertEquals(50, response.updatedPlayer().health());
        assertEquals(25, response.updatedPlayer().attackPower());
        assertTrue(response.gameOver());
    }

    /**
     * Additional test: Verify retry limit is respected (custom max retries).
     */
    @Test
    void testCustomMaxRetriesRespected() {
        LLMStoryEngine engineWithTwoRetries = new LLMStoryEngine(mockChatModel, codec, 2);
        when(mockChatModel.chat(anyString())).thenReturn(GARBAGE_RESPONSE);

        assertThrows(IllegalStateException.class, () -> engineWithTwoRetries.request(mockPrompt));

        // Verify the model was called exactly 2 times (respecting custom max retries)
        verify(mockChatModel, times(2)).chat(anyString());
    }

    /**
     * Additional test: Verify constructor with default max retries (3).
     */
    @Test
    void testDefaultConstructorUsesThreeRetries() {
        LLMStoryEngine engineDefault = new LLMStoryEngine(mockChatModel);
        when(mockChatModel.chat(anyString())).thenReturn(GARBAGE_RESPONSE);

        assertThrows(IllegalStateException.class, () -> engineDefault.request(mockPrompt));

        // Verify the model was called exactly 3 times (default max retries)
        verify(mockChatModel, times(3)).chat(anyString());
    }
}

