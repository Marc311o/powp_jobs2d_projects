package edu.kis.powp.jobs2d.drivers;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages device usage state such as head usage level and total distance.
 * Also acts as a publisher for device usage events.
 */
public class DeviceUsageManager implements DeviceUsagePublisher {
    private final double maxHeadUsageLevel;
    private double headUsageLevel;
    private double totalUsage = 0.0;
    private boolean lowHeadUsageNotified = false;

    private final List<DeviceUsageSubscriber> subscribers = new ArrayList<>();

    public DeviceUsageManager() {
        this(10000.0);
    }

    public DeviceUsageManager(double maxHeadUsageLevel) {
        this.maxHeadUsageLevel = maxHeadUsageLevel;
        this.headUsageLevel = maxHeadUsageLevel;
    }

    public synchronized void use(double distance) {
        headUsageLevel -= distance;
        totalUsage += distance;
        checkHeadUsageLevel();
        notifyUsageUpdate(headUsageLevel, maxHeadUsageLevel, totalUsage);
    }

    public synchronized boolean isOutOfHeadUsage() {
        return headUsageLevel <= 0;
    }

    private void checkHeadUsageLevel() {
        if (headUsageLevel < (maxHeadUsageLevel * 0.1) && !lowHeadUsageNotified) {
            notifySubscribers("LOW_HEAD_USAGE");
            lowHeadUsageNotified = true;
        }
    }

    public synchronized double getHeadUsageLevel() {
        return headUsageLevel;
    }

    public synchronized double getMaxHeadUsageLevel() {
        return maxHeadUsageLevel;
    }

    public synchronized double getTotalUsage() {
        return totalUsage;
    }

    public synchronized void refill() {
        this.headUsageLevel = maxHeadUsageLevel;
        this.lowHeadUsageNotified = false;
        notifyUsageUpdate(headUsageLevel, maxHeadUsageLevel, totalUsage);
    }

    public synchronized void service() {
        this.totalUsage = 0.0;
        notifyUsageUpdate(headUsageLevel, maxHeadUsageLevel, totalUsage);
    }

    @Override
    public synchronized void addSubscriber(DeviceUsageSubscriber subscriber) {
        subscribers.add(subscriber);
        subscriber.onUsageUpdate(headUsageLevel, maxHeadUsageLevel, totalUsage);
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
    public synchronized void notifyUsageUpdate(double headUsageLevel, double maxHeadUsageLevel, double totalUsage) {
        for (DeviceUsageSubscriber subscriber : subscribers) {
            subscriber.onUsageUpdate(headUsageLevel, maxHeadUsageLevel, totalUsage);
        }
    }
}
