package edu.kis.powp.jobs2d.features;

import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.drivers.DriverManager;
import edu.kis.powp.jobs2d.events.MouseInteractionListener;

import javax.swing.JPanel;


/**
 * Feature responsible for translating mouse clicks into driver operations.
 * Left mouse button draws a line to the clicked position and moves the device head.
 * Right mouse button moves the device head without drawing.
 */
public class MouseInteractionFeature implements IFeature {
    /**
     * Ensures the mouse listener is registered only once.
     */
    private static boolean initialized = false;

    /**
     * Initializes mouse interaction for the application.
     * @param application the application context
     */
    @Override
    public void setup(Application application) {
        if (initialized) {
            return;
        }
        initialized = true;

        DriverManager driverManager = DriverFeature.getDriverManager();
        JPanel panel = application.getFreePanel();

        panel.addMouseListener(
                new MouseInteractionListener(driverManager, panel)
        );
    }

    /**
     * Returns the name of this feature.
     * @return feature name
     */
    @Override
    public String getName() {
        return "Mouse Interaction";
    }
}
