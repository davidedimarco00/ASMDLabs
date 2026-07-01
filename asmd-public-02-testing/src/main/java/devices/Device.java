package devices;

public interface Device {
    void on() throws IllegalStateException;
    void off();
    boolean isOn();
    void reset();
}
