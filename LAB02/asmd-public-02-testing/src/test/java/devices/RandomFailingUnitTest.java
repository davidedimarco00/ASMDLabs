package devices;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.Field;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class RandomFailingUnitTest {

    @Test
    @DisplayName("RandomFailing deterministic sequence via injected Random")
    void deterministicSequenceUsingInjectedRandom() throws Exception {
        RandomFailing policy = new RandomFailing();

        // create deterministic Random that returns false, false, true, ...
        Random deterministic = new Random() {
            private int i = 0;
            @Override
            public boolean nextBoolean() {
                i++;
                if (i <= 2) return false; // first two attempts succeed
                return true; // third and subsequent cause failure
            }
        };

        // Inject deterministic random into RandomFailing via reflection
        Field rndField = RandomFailing.class.getDeclaredField("random");
        rndField.setAccessible(true);
        rndField.set(policy, deterministic);

        // First two calls should return true (not failed yet)
        assertTrue(policy.attemptOn());
        assertTrue(policy.attemptOn());
        // Third call should observe failure (random returned true -> failed becomes true)
        assertFalse(policy.attemptOn());
        // Subsequent calls remain false (once failed, always failed)
        assertFalse(policy.attemptOn());

        // Reset should restore ability to succeed
        policy.reset();
        assertTrue(policy.attemptOn());
    }
}
