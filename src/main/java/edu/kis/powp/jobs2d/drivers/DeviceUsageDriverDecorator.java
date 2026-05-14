package edu.kis.powp.jobs2d.drivers;

import edu.kis.powp.jobs2d.drivers.visitor.DriverVisitor;
import edu.kis.powp.jobs2d.drivers.visitor.VisitableDriver;

/**
 * Driver decorator that tracks device usage such as head usage level and total distance.
 */
public class DeviceUsageDriverDecorator implements VisitableDriver {
    private final VisitableDriver innerDriver;
    private final DeviceUsageManager manager;
    private int currentX = 0;
    private int currentY = 0;

    public DeviceUsageDriverDecorator(VisitableDriver innerDriver, DeviceUsageManager manager) {
        this.innerDriver = innerDriver;
        this.manager = manager;
    }

    @Override
    public synchronized void setPosition(int x, int y) {
        this.currentX = x;
        this.currentY = y;
        innerDriver.setPosition(x, y);
    }

    @Override
    public synchronized void operateTo(int x, int y) {
        if (manager.isOutOfHeadUsage()) {
            manager.notifySubscribers("OUT_OF_HEAD_USAGE");
            return;
        }
        double distance = Math.hypot(x - currentX, y - currentY);
        manager.use(distance);
        
        this.currentX = x;
        this.currentY = y;

        innerDriver.operateTo(x, y);
    }

    public DeviceUsageManager getManager() {
        return manager;
    }

    public VisitableDriver getInnerDriver() {
        return innerDriver;
    }

    @Override
    public synchronized String toString() {
        return innerDriver.toString() + " [Device Usage]";
    }

    @Override
    public void accept(DriverVisitor visitor) {
        innerDriver.accept(visitor);
    }
}
