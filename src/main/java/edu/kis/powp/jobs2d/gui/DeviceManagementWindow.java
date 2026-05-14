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
    private JProgressBar headUsageProgressBar;
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

        content.add(new JLabel("Head Usage Level:"), c);

        headUsageProgressBar = new JProgressBar(0, 10000);
        headUsageProgressBar.setValue(10000);
        headUsageProgressBar.setStringPainted(true);
        content.add(headUsageProgressBar, c);

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
        if ("LOW_HEAD_USAGE".equals(message)) {
            headUsageProgressBar.setForeground(Color.RED);
        }
    }

    @Override
    public void onUsageUpdate(double headUsageLevel, double maxHeadUsageLevel, double totalUsage) {
        if (headUsageProgressBar.getMaximum() != (int) maxHeadUsageLevel) {
            headUsageProgressBar.setMaximum((int) maxHeadUsageLevel);
        }
        headUsageProgressBar.setValue((int) headUsageLevel);
        usageLabel.setText(String.format("Total Usage: %.2f", totalUsage));
        
        if (headUsageLevel >= (maxHeadUsageLevel * 0.1)) {
            headUsageProgressBar.setForeground(null); // Reset to default
        } else {
            headUsageProgressBar.setForeground(Color.RED);
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
