package edu.kis.powp.jobs2d.command.gui;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import edu.kis.powp.appbase.gui.WindowComponent;
import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.command.catalog.CommandCatalog;
import edu.kis.powp.jobs2d.command.manager.CommandManager;
import edu.kis.powp.observer.Subscriber;

public class CommandCatalogWindow extends JFrame implements WindowComponent, Subscriber {

    private static final long serialVersionUID = 1L;

    private CommandManager commandManager;
    private CommandCatalog commandCatalog;

    private DefaultListModel<String> commandListModel;
    private JList<String> commandList;
    private Map<String, String> displayedCommandNames = new LinkedHashMap<>();

    public CommandCatalogWindow(CommandManager commandManager, CommandCatalog commandCatalog) {
        this.setTitle("Command Catalog");
        this.setSize(400, 400);

        this.commandManager = commandManager;
        this.commandCatalog = commandCatalog;

        Container content = this.getContentPane();
        content.setLayout(new GridBagLayout());

        commandListModel = new DefaultListModel<>();
        commandList = new JList<>(commandListModel);

        GridBagConstraints c = new GridBagConstraints();

        JScrollPane scrollPane = new JScrollPane(commandList);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        content.add(scrollPane, c);

        JButton btnLoadCommand = new JButton("Load");
        btnLoadCommand.addActionListener((ActionEvent e) -> this.loadSelectedCommand());
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 1;
        content.add(btnLoadCommand, c);

        JButton btnAddCommand = new JButton("Add");
        btnAddCommand.addActionListener((ActionEvent e) -> this.addCurrentCommand());
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 2;
        content.add(btnAddCommand, c);

        commandCatalog.getChangePublisher().addSubscriber(this);

        updateCommandList();
    }

    private void updateCommandList() {
        commandListModel.clear();
        displayedCommandNames.clear();

        for (String commandName : commandCatalog.getCommandNames()) {
            String displayedCommandName = getDisplayedCommandName(commandName);
            displayedCommandNames.put(displayedCommandName, commandName);
            commandListModel.addElement(displayedCommandName);
        }
    }

    private String getDisplayedCommandName(String commandName) {
        List<String> tags = commandCatalog.getCommandTags(commandName);

        if (tags.isEmpty()) {
            return commandName;
        }

        return commandName + " [" + String.join(", ", tags) + "]";
    }

    private void loadSelectedCommand() {
        String selectedCommandName = commandList.getSelectedValue();

        if (selectedCommandName == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Select command from catalog first.",
                    "No command selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String commandName = displayedCommandNames.get(selectedCommandName);
        DriverCommand command = commandCatalog.getCommand(commandName);

        if (command == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Cannot load command: " + commandName,
                    "Command loading error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        commandManager.setCurrentCommand(command);
    }

    private void addCurrentCommand() {
        DriverCommand currentCommand = commandManager.getCurrentCommand();

        if (currentCommand == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "There is no current command in Command Manager.",
                    "No command to add",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String defaultName = commandManager.getCurrentCommandString();

        if (defaultName == null || defaultName.trim().isEmpty()) {
            defaultName = currentCommand.toString();
        }

        String commandName = JOptionPane.showInputDialog(
                this,
                "Command name:",
                defaultName
        );

        if (commandName == null || commandName.trim().isEmpty()) {
            return;
        }

        commandName = commandName.trim();

        if (commandCatalog.containsCommand(commandName)) {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Command '" + commandName + "' already exists. Overwrite?",
                    "Command already exists",
                    JOptionPane.YES_NO_OPTION
            );

            if (result != JOptionPane.YES_OPTION) {
                return;
            }
        }

        List<String> tags = askForTags();
        commandCatalog.addCommand(commandName, currentCommand, tags);
    }

    private List<String> askForTags() {
        String tagsText = JOptionPane.showInputDialog(
                this,
                "Command tags separated by commas:",
                ""
        );

        List<String> tags = new ArrayList<>();

        if (tagsText == null || tagsText.trim().isEmpty()) {
            return tags;
        }

        for (String tag : tagsText.split(",")) {
            if (!tag.trim().isEmpty()) {
                tags.add(tag.trim());
            }
        }

        return tags;
    }

    @Override
    public void update() {
        updateCommandList();
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