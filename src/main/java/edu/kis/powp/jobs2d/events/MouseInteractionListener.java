package edu.kis.powp.jobs2d.events;

import edu.kis.powp.jobs2d.drivers.DriverManager;

import javax.swing.JPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Listener handles mouse clicks and passes actions to the driver.
 * Left click draws, right click moves the device head without drawing.
 */
public class MouseInteractionListener extends MouseAdapter {

    /**
     * Driver access.
     */
    private final DriverManager driverManager;

    /**
     * Drawing panel.
     */
    private final JPanel panel;

    /**
     * @param driverManager driver provider.
     * @param panel drawing surface.
     */
    public MouseInteractionListener(DriverManager driverManager, JPanel panel) {
        this.driverManager = driverManager;
        this.panel = panel;
    }

    /**
     * Reacts to mouse clicks.
     * @param e the event to be processed.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (driverManager.getCurrentDriver() == null) {
            return;
        }

        int x = e.getX() - panel.getWidth() / 2;
        int y = e.getY() - panel.getHeight() / 2;

        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                driverManager.getCurrentDriver().operateTo(x, y);
                break;

            case MouseEvent.BUTTON3:
                driverManager.getCurrentDriver().setPosition(x, y);
                break;

            default:
                break;
        }
    }
}