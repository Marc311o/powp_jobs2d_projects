package edu.kis.powp.jobs2d.drivers;

public interface DeviceUsageSubscriber {
    void update(String message);
    void onUsageUpdate(double headUsageLevel, double maxHeadUsageLevel, double totalUsage);
}
