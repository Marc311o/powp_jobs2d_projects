package edu.kis.powp.jobs2d.features;

import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.command.manager.CommandManager;
import edu.kis.powp.jobs2d.command.manager.LoggerCommandChangeObserver;
import edu.kis.powp.jobs2d.command.io.CommandImporterFactory;
import edu.kis.powp.jobs2d.command.io.JsonCommandImporterProvider;
import edu.kis.powp.jobs2d.command.catalog.CommandCatalog;
import edu.kis.powp.jobs2d.command.CompoundCommandFactory;
import edu.kis.powp.jobs2d.command.ImmutableCompoundCommandFactory;

public class CommandsFeature implements IFeature {

    private static CommandManager commandManager;
    private static final CommandCatalog commandCatalog = setUpCommandCatalog();

    @Override
    public void setup(Application application) {
        setupCommandManager();
    }

    @Override
    public String getName() {
        return "Commands";
    }

    public static void setupCommandManager() {
        commandManager = new CommandManager();

        LoggerCommandChangeObserver loggerObserver = new LoggerCommandChangeObserver();
        commandManager.getChangePublisher().addSubscriber(loggerObserver);

        CommandImporterFactory.registerProvider(new JsonCommandImporterProvider());
    }

    /**
     * Create command catalog and add commands to it.
     *
     * @return created command catalog.
     */
    private static CommandCatalog setUpCommandCatalog() {
        CommandCatalog commandCatalog = new CommandCatalog();

        commandCatalog.addCommand("TopSecretCommand", CompoundCommandFactory.createTopSecretCommand());
        commandCatalog.addCommand("KiteCommand", CompoundCommandFactory.createKiteCommand());
        commandCatalog.addCommand("Immutable Rectangle",
                ImmutableCompoundCommandFactory.getRectangle(0, 0, 100, 150));

        return commandCatalog;
    }

    /**
     * Get manager of application driver command.
     * 
     * @return plotterCommandManager.
     */
    public static CommandManager getDriverCommandManager() {
        return commandManager;
    }

    /**
     * Get command catalog.
     *
     * @return created command catalog.
     */
    public static CommandCatalog getCommandCatalog() {
        return commandCatalog;
    }
}
