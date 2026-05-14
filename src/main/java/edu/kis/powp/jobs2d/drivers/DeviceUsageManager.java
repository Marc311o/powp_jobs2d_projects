package edu.kis.powp.jobs2d.drivers;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages device usage state such as operational usage level and total distance.
 * Also acts as a publisher for device usage events.
 */
public class DeviceUsageManager implements DeviceUsagePublisher {
    private final double maxOperationalUsageLevel;
    private double operationalUsageLevel;
    private double totalUsage = 0.0;
    private boolean lowOperationalUsageNotified = false;

    private final List<DeviceUsageSubscriber> subscribers = new ArrayList<>();

    public DeviceUsageManager() {
        this(10000.0);
    }

    public DeviceUsageManager(double maxOperationalUsageLevel) {
        this.maxOperationalUsageLevel = maxOperationalUsageLevel;
        this.operationalUsageLevel = maxOperationalUsageLevel;
    }

    public synchronized void use(double distance) {
        operationalUsageLevel -= distance;
        totalUsage += distance;
        checkOperationalUsageLevel();
        notifyUsageUpdate(operationalUsageLevel, maxOperationalUsageLevel, totalUsage);
    }

    public synchronized boolean isOutOfOperationalUsage() {
        return operationalUsageLevel <= 0;
    }

    private void checkOperationalUsageLevel() {
        if (operationalUsageLevel < (maxOperationalUsageLevel * 0.1) && !lowOperationalUsageNotified) {
            notifySubscribers("LOW_OPERATIONAL_USAGE");
            lowOperationalUsageNotified = true;
        }
    }

    public synchronized double getOperationalUsageLevel() {
        return operationalUsageLevel;
    }

    public synchronized double getMaxOperationalUsageLevel() {
        return maxOperationalUsageLevel;
    }

    public synchronized double getTotalUsage() {
        return totalUsage;
    }

    public synchronized void refill() {
        this.operationalUsageLevel = maxOperationalUsageLevel;
        this.lowOperationalUsageNotified = false;
        notifyUsageUpdate(operationalUsageLevel, maxOperationalUsageLevel, totalUsage);
    }

    public synchronized void service() {
        this.totalUsage = 0.0;
        notifyUsageUpdate(operationalUsageLevel, maxOperationalUsageLevel, totalUsage);
    }

    @Override
    public synchronized void addSubscriber(DeviceUsageSubscriber subscriber) {
        subscribers.add(subscriber);
        subscriber.onUsageUpdate(operationalUsageLevel, maxOperationalUsageLevel, totalUsage);
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
    public synchronized void notifyUsageUpdate(double operationalUsageLevel, double maxOperationalUsageLevel, double totalUsage) {
        for (DeviceUsageSubscriber subscriber : subscribers) {
            subscriber.onUsageUpdate(operationalUsageLevel, maxOperationalUsageLevel, totalUsage);
        }
    }
}
