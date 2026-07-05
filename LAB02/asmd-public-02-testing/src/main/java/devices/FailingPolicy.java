package devices;

public interface FailingPolicy {
    boolean attemptOn();
    void reset();
    String policyName();
}
