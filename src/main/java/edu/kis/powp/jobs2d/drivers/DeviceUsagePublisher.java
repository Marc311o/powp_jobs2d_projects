package edu.kis.powp.jobs2d.drivers;

public interface DeviceUsagePublisher {
    void addSubscriber(DeviceUsageSubscriber subscriber);
    void removeSubscriber(DeviceUsageSubscriber subscriber);
    void notifySubscribers(String message);
    void notifyUsageUpdate(double waterLevel, double maxWaterLevel, double totalUsage);
}
