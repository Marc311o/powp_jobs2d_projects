package edu.kis.powp.jobs2d.features;

import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.drivers.DeviceUsageDriverDecorator;
import edu.kis.powp.jobs2d.drivers.DeviceUsageManager;
import edu.kis.powp.jobs2d.drivers.visitor.VisitableDriver;
import edu.kis.powp.jobs2d.gui.DeviceManagementWindow;

public class DeviceUsageFeature implements IFeature {

    private static DeviceManagementWindow deviceManagementWindow;
    private static DeviceUsageManager deviceUsageManager;

    @Override
    public void setup(Application application) {
        deviceUsageManager = new DeviceUsageManager();
        deviceManagementWindow = new DeviceManagementWindow(deviceUsageManager);
        
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
        return new DeviceUsageDriverDecorator(driver, deviceUsageManager);
    }

    public static DeviceUsageManager getDeviceUsageManager() {
        return deviceUsageManager;
    }

    @Override
    public String getName() {
        return "Device Usage";
    }

    public static DeviceManagementWindow getDeviceManagementWindow() {
        return deviceManagementWindow;
    }
}
