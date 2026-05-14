package edu.kis.powp.jobs2d.drivers;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages device usage state such as water level and total distance.
 * Also acts as a publisher for device usage events.
 */
public class DeviceUsageManager implements DeviceUsagePublisher {
    private final double maxWaterLevel;
    private double waterLevel;
    private double totalUsage = 0.0;
    private boolean lowWaterNotified = false;

    private final List<DeviceUsageSubscriber> subscribers = new ArrayList<>();

    public DeviceUsageManager() {
        this(10000.0);
    }

    public DeviceUsageManager(double maxWaterLevel) {
        this.maxWaterLevel = maxWaterLevel;
        this.waterLevel = maxWaterLevel;
    }

    public synchronized void use(double distance) {
        waterLevel -= distance;
        totalUsage += distance;
        checkWaterLevel();
        notifyUsageUpdate(waterLevel, maxWaterLevel, totalUsage);
    }

    public synchronized boolean isOutOfWater() {
        return waterLevel <= 0;
    }

    private void checkWaterLevel() {
        if (waterLevel < (maxWaterLevel * 0.1) && !lowWaterNotified) {
            notifySubscribers("LOW_WATER");
            lowWaterNotified = true;
        }
    }

    public synchronized double getWaterLevel() {
        return waterLevel;
    }

    public synchronized double getMaxWaterLevel() {
        return maxWaterLevel;
    }

    public synchronized double getTotalUsage() {
        return totalUsage;
    }

    public synchronized void refill() {
        this.waterLevel = maxWaterLevel;
        this.lowWaterNotified = false;
        notifyUsageUpdate(waterLevel, maxWaterLevel, totalUsage);
    }

    public synchronized void service() {
        this.totalUsage = 0.0;
        notifyUsageUpdate(waterLevel, maxWaterLevel, totalUsage);
    }

    @Override
    public synchronized void addSubscriber(DeviceUsageSubscriber subscriber) {
        subscribers.add(subscriber);
        subscriber.onUsageUpdate(waterLevel, maxWaterLevel, totalUsage);
    }

    @Override
    public synchronized void removeSubscriber(DeviceUsageSubscriber subscriber) {
        subscribers.remove(subscriber);
    }

    @Override
    public synchronized void notifySubscribers(String message) {
        for (DeviceUsageSubscriber subscriber : subscribers) {
            subscriber.update(message);
        }
    }

    @Override
    public synchronized void notifyUsageUpdate(double waterLevel, double maxWaterLevel, double totalUsage) {
        for (DeviceUsageSubscriber subscriber : subscribers) {
            subscriber.onUsageUpdate(waterLevel, maxWaterLevel, totalUsage);
        }
    }
}
