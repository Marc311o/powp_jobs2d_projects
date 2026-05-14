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
import edu.kis.powp.jobs2d.drivers.DeviceUsageManager;
import edu.kis.powp.jobs2d.drivers.DeviceUsageSubscriber;

public class DeviceManagementWindow extends JFrame implements WindowComponent, DeviceUsageSubscriber {

    private DeviceUsageManager deviceUsageManager;
    private JProgressBar waterProgressBar;
    private JLabel usageLabel;

    private static final long serialVersionUID = 1L;

    public DeviceManagementWindow(DeviceUsageManager deviceUsageManager) {
        this.deviceUsageManager = deviceUsageManager;
        this.setTitle("Device Management");
        this.setSize(400, 200);
        Container content = this.getContentPane();
        content.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;

        content.add(new JLabel("Water Level:"), c);

        waterProgressBar = new JProgressBar(0, 10000);
        waterProgressBar.setValue(10000);
        waterProgressBar.setStringPainted(true);
        content.add(waterProgressBar, c);

        usageLabel = new JLabel("Total Usage: 0.0");
        content.add(usageLabel, c);

        JButton btnRefill = new JButton("Refill");
        btnRefill.addActionListener((ActionEvent e) -> {
            if (this.deviceUsageManager != null) {
                this.deviceUsageManager.refill();
            }
        });
        content.add(btnRefill, c);

        JButton btnService = new JButton("Service");
        btnService.addActionListener((ActionEvent e) -> {
            if (this.deviceUsageManager != null) {
                this.deviceUsageManager.service();
            }
        });
        content.add(btnService, c);

        if (this.deviceUsageManager != null) {
            this.deviceUsageManager.addSubscriber(this);
        }
    }

    public void setDeviceUsageManager(DeviceUsageManager newDeviceUsageManager) {
        if (this.deviceUsageManager != null) {
            this.deviceUsageManager.removeSubscriber(this);
        }
        this.deviceUsageManager = newDeviceUsageManager;
        if (this.deviceUsageManager != null) {
            this.deviceUsageManager.addSubscriber(this);
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
