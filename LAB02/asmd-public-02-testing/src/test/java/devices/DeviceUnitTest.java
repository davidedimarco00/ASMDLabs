package devices;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DeviceUnitTest {

    @Mock
    private FailingPolicy stubFailingPolicy;

    private Device device;

    @BeforeEach
    void setup() {
        // MockitoExtension initialises @Mock
    }

    @Test
    @DisplayName("Device is initially off (isolated)")
    void deviceInitiallyOff() {
        device = new StandardDevice(stubFailingPolicy);
        assertFalse(device.isOn());
    }

    @Test
    @DisplayName("Device switches on when policy allows (isolated)")
    void deviceSwitchOnWithStub() {
        when(stubFailingPolicy.attemptOn()).thenReturn(true);
        device = new StandardDevice(stubFailingPolicy);
        device.on();
        assertTrue(device.isOn());
        verify(stubFailingPolicy, times(1)).attemptOn();
    }

    @Test
    @DisplayName("Device throws when policy fails (isolated)")
    void deviceThrowsWhenPolicyFails() {
        when(stubFailingPolicy.attemptOn()).thenReturn(false);
        when(stubFailingPolicy.policyName()).thenReturn("stub");
        device = new StandardDevice(stubFailingPolicy);
        assertThrows(IllegalStateException.class, () -> device.on());
        assertEquals("StandardDevice{policy=stub, on=false}", device.toString());
        verify(stubFailingPolicy, times(1)).attemptOn();
    }

    @Test
    @DisplayName("Reset delegates to policy.reset (isolated)")
    void resetDelegatesToPolicy() {
        when(stubFailingPolicy.attemptOn()).thenReturn(true);
        device = new StandardDevice(stubFailingPolicy);
        device.on();
        assertTrue(device.isOn());
        device.reset();
        assertFalse(device.isOn());
        verify(stubFailingPolicy, times(1)).reset();
    }
}
