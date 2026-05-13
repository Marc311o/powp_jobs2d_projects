package edu.kis.powp.jobs2d.events;

import edu.kis.powp.jobs2d.command.ImmutableCompoundCommand;
import edu.kis.powp.jobs2d.command.ImmutableCompoundCommandFactory;
import edu.kis.powp.jobs2d.command.manager.CommandManager;
import edu.kis.powp.jobs2d.features.CommandsFeature;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SelectLoadImmutableRectangleCommandOptionListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        ImmutableCompoundCommand command = ImmutableCompoundCommandFactory.getRectangle(0, 0, 100, 150);
        CommandManager manager = CommandsFeature.getDriverCommandManager();
        manager.setCurrentCommand(command);
    }
}