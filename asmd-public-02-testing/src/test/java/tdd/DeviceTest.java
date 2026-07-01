package tdd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DeviceTest {

    private Device device;

    @BeforeEach
    void init(){
        this.device = new DeviceImpl();
    }

    @Test
    void initiallyOff(){
        assertFalse(this.device.isOn());
    }

    @Test
    void canBeSwitchedOn(){
        this.device.switchOn();
        assertTrue(this.device.isOn());
    }

    @Test
    void canBeSwitchedOnAndOff(){
        this.device.switchOn();
        this.device.switchOff();
        assertFalse(this.device.isOn());
    }

    @Disabled
    @Test
    void cantSwitchOnIfAlreadyOn(){
        this.device.switchOn();
        assertThrows(IllegalStateException.class, () -> this.device.switchOn());
    }
}
