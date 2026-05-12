package edu.kis.powp.jobs2d.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import edu.kis.powp.appbase.gui.WindowComponent;
import edu.kis.powp.jobs2d.drivers.DeviceUsageDriverDecorator;
import edu.kis.powp.jobs2d.drivers.DeviceUsageSubscriber;

public class DeviceManagementWindow extends JFrame implements WindowComponent, DeviceUsageSubscriber {

    private DeviceUsageDriverDecorator driver;
    private JProgressBar waterProgressBar;
    private JLabel usageLabel;

    private static final long serialVersionUID = 1L;

    public DeviceManagementWindow(DeviceUsageDriverDecorator driver) {
        this.driver = driver;
        this.setTitle("Device Management");
        this.setSize(400, 200);
        Container content = this.getContentPane();
        content.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;

        content.add(new JLabel("Water Level:"), c);

        waterProgressBar = new JProgressBar(0, 1000);
        waterProgressBar.setValue(1000);
        waterProgressBar.setStringPainted(true);
        content.add(waterProgressBar, c);

        usageLabel = new JLabel("Total Usage: 0.0");
        content.add(usageLabel, c);

        JButton btnRefill = new JButton("Refill");
        btnRefill.addActionListener((ActionEvent e) -> {
            if (this.driver != null) {
                this.driver.refill();
            }
        });
        content.add(btnRefill, c);

        JButton btnService = new JButton("Service");
        btnService.addActionListener((ActionEvent e) -> {
            if (this.driver != null) {
                this.driver.service();
            }
        });
        content.add(btnService, c);

        if (this.driver != null) {
            this.driver.addSubscriber(this);
        }
    }

    public void setDriver(DeviceUsageDriverDecorator newDriver) {
        if (this.driver != null) {
            this.driver.removeSubscriber(this);
        }
        this.driver = newDriver;
        if (this.driver != null) {
            this.driver.addSubscriber(this);
        }
    }

    @Override
    public void update(String message) {
        if ("LOW_WATER".equals(message)) {
            waterProgressBar.setForeground(Color.RED);
        }
    }

    @Override
    public void onUsageUpdate(double waterLevel, double maxWaterLevel, double totalUsage) {
        if (waterProgressBar.getMaximum() != (int) maxWaterLevel) {
            waterProgressBar.setMaximum((int) maxWaterLevel);
        }
        waterProgressBar.setValue((int) waterLevel);
        usageLabel.setText(String.format("Total Usage: %.2f", totalUsage));
        
        if (waterLevel >= (maxWaterLevel * 0.1)) {
            waterProgressBar.setForeground(null); // Reset to default
        } else {
            waterProgressBar.setForeground(Color.RED);
        }
    }

    @Override
    public void HideIfVisibleAndShowIfHidden() {
        if (this.isVisible()) {
            this.setVisible(false);
        } else {
            this.setVisible(true);
        }
    }
}
