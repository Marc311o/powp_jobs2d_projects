package edu.kis.powp.jobs2d.drivers;

import edu.kis.powp.jobs2d.drivers.visitor.DriverVisitor;
import edu.kis.powp.jobs2d.drivers.visitor.VisitableDriver;

import java.util.ArrayList;
import java.util.List;

/**
 * Driver decorator that tracks device usage such as water level and total distance.
 * Also acts as a publisher for device usage events.
 */
public class DeviceUsageDriverDecorator implements VisitableDriver, DeviceUsagePublisher {
    private final VisitableDriver innerDriver;
    private final double maxWaterLevel;
    private double waterLevel;
    private double totalUsage = 0.0;
    private int currentX = 0;
    private int currentY = 0;
    private boolean lowWaterNotified = false;

    private final List<DeviceUsageSubscriber> subscribers = new ArrayList<>();

    public DeviceUsageDriverDecorator(VisitableDriver innerDriver) {
        this(innerDriver, 1000.0);
    }

    public DeviceUsageDriverDecorator(VisitableDriver innerDriver, double maxWaterLevel) {
        this.innerDriver = innerDriver;
        this.maxWaterLevel = maxWaterLevel;
        this.waterLevel = maxWaterLevel;
    }

    @Override
    public synchronized void setPosition(int x, int y) {
        this.currentX = x;
        this.currentY = y;
        innerDriver.setPosition(x, y);
        notifyUsageUpdate(waterLevel, maxWaterLevel, totalUsage);
    }

    @Override
    public synchronized void operateTo(int x, int y) {
        if(waterLevel <= 0){
            notifySubscribers("OUT_OF_WATER");
            return;
        }
        double distance = Math.hypot(x - currentX, y - currentY);
        waterLevel -= distance;
        totalUsage += distance;
        this.currentX = x;
        this.currentY = y;

        checkWaterLevel();
        notifyUsageUpdate(waterLevel, maxWaterLevel, totalUsage);

        innerDriver.operateTo(x, y);
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

    public VisitableDriver getInnerDriver() {
        return innerDriver;
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

    @Override
    public synchronized String toString() {
        return innerDriver.toString() + " [Device Usage]";
    }

    @Override
    public void accept(DriverVisitor visitor) {
        visitor.visit(this);
    }
}
