package devices;

import java.util.Objects;

public class StandardDevice implements Device {
    private FailingPolicy failingPolicy;
    private boolean on = false;

    public StandardDevice(FailingPolicy failingPolicy) {
        this.failingPolicy = Objects.requireNonNull(failingPolicy);
    }
    //...

    @Override
    public void on() throws IllegalStateException {
        if (!this.failingPolicy.attemptOn()){
            throw new IllegalStateException();
        }
        this.on = true;
    }

    @Override
    public void off() {
        this.on = false;
    }

    @Override
    public boolean isOn() {
        return this.on;
    }

    @Override
    public void reset() {
        this.off();
        this.failingPolicy.reset();
    }

    @Override
    public String toString() {
        return "StandardDevice{" +
                "policy=" + failingPolicy.policyName() +
                ", on=" + on +
                '}';
    }
}
