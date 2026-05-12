package edu.kis.powp.jobs2d.features;

import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.drivers.DeviceUsageDriverDecorator;
import edu.kis.powp.jobs2d.drivers.visitor.VisitableDriver;
import edu.kis.powp.jobs2d.gui.DeviceManagementWindow;

public class DeviceUsageFeature implements IFeature {

    private static DeviceManagementWindow deviceManagementWindow;
    private static DeviceUsageDriverDecorator currentDecorator;

    @Override
    public void setup(Application application) {
        application.addComponentMenu(DeviceUsageFeature.class, "Device Usage");
        application.addComponentMenuElement(DeviceUsageFeature.class, "Open Device Manager", 
                (e) -> {
                    if (deviceManagementWindow != null) {
                        deviceManagementWindow.setVisible(true);
                    }
                });
    }

    /**
     * Decorates the given driver with DeviceUsageDriverDecorator and connects it to the window.
     * @param driver driver to decorate
     * @return decorated driver
     */
    public static VisitableDriver decorateDriver(VisitableDriver driver) {
        return decorateDriver(driver, 1000.0);
    }

    /**
     * Decorates the given driver with DeviceUsageDriverDecorator with custom capacity.
     * @param driver driver to decorate
     * @param maxWaterLevel custom capacity
     * @return decorated driver
     */
    public static VisitableDriver decorateDriver(VisitableDriver driver, double maxWaterLevel) {
        currentDecorator = new DeviceUsageDriverDecorator(driver, maxWaterLevel);
        if (deviceManagementWindow == null) {
            deviceManagementWindow = new DeviceManagementWindow(currentDecorator);
        } else {
            deviceManagementWindow.setDriver(currentDecorator); 
        }
        return currentDecorator;
    }

    @Override
    public String getName() {
        return "Device Usage";
    }

    public static DeviceManagementWindow getDeviceManagementWindow() {
        return deviceManagementWindow;
    }
}
