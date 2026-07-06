package devices;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.Field;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class DeviceIntegrationTest {

    @Test
    @DisplayName("Device collaborates with a deterministic RandomFailing (integration)")
    void deviceWithDeterministicRandomFailing() throws Exception {
        RandomFailing policy = new RandomFailing();

        // deterministic sequence: false, false, true -> allows two on() then fail
        Random deterministic = new Random() {
            private int i = 0;
            @Override
            public boolean nextBoolean() {
                i++;
                if (i <= 2) return false;
                return true;
            }
        };

        // inject deterministic random
        Field rndField = RandomFailing.class.getDeclaredField("random");
        rndField.setAccessible(true);
        rndField.set(policy, deterministic);

        Device device = new StandardDevice(policy);

        // first two on() calls succeed
        device.on();
        assertTrue(device.isOn());
        device.off();
        assertFalse(device.isOn());

        device.on();
        assertTrue(device.isOn());
        device.off();
        assertFalse(device.isOn());

        // next on() should fail due to policy
        assertThrows(IllegalStateException.class, () -> device.on());

        // toString should expose policy name
        assertTrue(device.toString().contains("policy=random"));
    }
}
