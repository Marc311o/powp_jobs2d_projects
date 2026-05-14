package edu.kis.powp.jobs2d.drivers;

public interface DeviceUsageSubscriber {
    void update(String message);
    void onUsageUpdate(double operationalUsageLevel, double maxOperationalUsageLevel, double totalUsage);
}
