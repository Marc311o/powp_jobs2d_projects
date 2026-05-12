package edu.kis.powp.jobs2d;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.kis.powp.jobs2d.command.gui.*;

import edu.kis.legacy.drawer.panel.DrawPanelController;
import edu.kis.legacy.drawer.shape.LineFactory;
import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.command.gui.CommandManagerWindow;
import edu.kis.powp.jobs2d.command.gui.CommandManagerWindowCommandChangeObserver;
import edu.kis.powp.jobs2d.drivers.RealTimeDriver;
import edu.kis.powp.jobs2d.drivers.RecordingDriver;
import edu.kis.powp.jobs2d.drivers.adapter.LineDriverAdapter;
import edu.kis.powp.jobs2d.drivers.logger.TrackingLoggerDriver;
import edu.kis.powp.jobs2d.drivers.packet_composite.CompositeDriver;
import edu.kis.powp.jobs2d.drivers.transformations.*;
import edu.kis.powp.jobs2d.drivers.visitor.FullNameGetterVisitor;
import edu.kis.powp.jobs2d.drivers.visitor.VisitableDriver;
import edu.kis.powp.jobs2d.events.*;
import edu.kis.powp.jobs2d.features.*;
import edu.kis.powp.jobs2d.events.SelectLoadRecordedMacroOptionListener;
import edu.kis.powp.jobs2d.events.SelectClearPanelOptionListener;
import edu.kis.powp.jobs2d.events.SelectToggleRecordingOptionListener;
import edu.kis.powp.jobs2d.events.SelectClearRecordingOptionListener;
import edu.kis.powp.jobs2d.command.gui.CommandCatalogWindow;

public class TestJobs2dApp {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Setup test concerning preset figures in context.
     *
     * @param application Application context.
     */
    private static void setupPresetTests(Application application) {
        SelectTestFigureOptionListener selectTestFigureOptionListener = new SelectTestFigureOptionListener(
                DriverFeature.getDriverManager());
        SelectTestFigure2OptionListener selectTestFigure2OptionListener = new SelectTestFigure2OptionListener(
                DriverFeature.getDriverManager());

        application.addTest("Figure Joe 1", selectTestFigureOptionListener);
        application.addTest("Figure Joe 2", selectTestFigure2OptionListener);
    }

    /**
     * Setup test using driver commands in context.
     *
     * @param application Application context.
     */
    private static void setupCommandTests(Application application) {
        application.addTest("Load secret command", new SelectLoadSecretCommandOptionListener());
        application.addTest("Load immutable rectangle command", new SelectLoadImmutableRectangleCommandOptionListener());

        application.addTest("Load kite command", new SelectLoadKiteCommandOptionListener());
        application.addTest("Load recorded macro", new SelectLoadRecordedMacroOptionListener());

        application.addTest("Clear panel", new SelectClearPanelOptionListener());
        application.addTest("Run command", new SelectRunCurrentCommandOptionListener(DriverFeature.getDriverManager()));
        application.addTest("Count current command", new SelectCountCommandsOptionListener());

        application.addTest("Check current command bounds", new SelectCheckCanvasBoundsOptionListener());
        application.addTest("Transform current command: Scale 2x",
                new SelectTransformCommandOptionListener(new ScaleTransformer(2.0, 2.0), "Scale 2x"));
        application.addTest("Transform current command: Scale 0.5x",
                new SelectTransformCommandOptionListener(new ScaleTransformer(0.5, 0.5), "Scale 0.5x"));
        application.addTest("Transform current command: Rotate 45 degrees",
                new SelectTransformCommandOptionListener(new RotateTransformer(45.0), "Rotate 45 degrees"));
        application.addTest("Transform current command: Flip Y",
                new SelectTransformCommandOptionListener(new FlipTransformer(false, true), "Flip Y"));
        application.addTest("FullNameGetter visitor test",
                new SelectFullNameGetterVisitorTestListener(new FullNameGetterVisitor()));

        application.addTest("Deep copy visitor test", new SelectDeepCopyVisitorTestListener());

        RecordingDriver rec = RecordingFeature.getRecordingDriver();
        boolean initial = rec.isRecordingEnabled();

        application.addComponentMenuElementWithCheckBox(
                DriverFeature.class,
                "Recording",
                new SelectToggleRecordingOptionListener(rec),
                initial
        );

        application.addComponentMenuElement(
                DriverFeature.class,
                "Clear recording",
                new SelectClearRecordingOptionListener()
        );
    }

    /**
     * Setup driver manager, and set default VisitableDriver for application.
     *
     * @param application Application context.
     */
    private static void setupDrivers(Application application) {
        VisitableDriver TrackingLoggerDriver = new TrackingLoggerDriver();
        DriverFeature.addDriver("Tracking Logger driver", TrackingLoggerDriver);

        DrawPanelController drawerController = DrawerFeature.getDrawerController();
        VisitableDriver driver = new LineDriverAdapter(drawerController, LineFactory.getBasicLine(), "basic");
        DriverFeature.addDriver("Line Simulator", driver);
        DriverFeature.getDriverManager().setCurrentDriver(driver);

        driver = new LineDriverAdapter(drawerController, LineFactory.getSpecialLine(), "special");
        DriverFeature.addDriver("Special line Simulator", driver);
        DriverFeature.updateDriverInfo();

        CompositeDriver basicCompositeDriver = new CompositeDriver("Basic & Log Composite Driver");
        basicCompositeDriver.addDriver(TrackingLoggerDriver);
        basicCompositeDriver.addDriver(driver);
        DriverFeature.addDriver(basicCompositeDriver.toString(), basicCompositeDriver);

        CoordinateTransformer scale = new ScaleTransformer(2.0, 2.0);
        VisitableDriver scaledDriver = new TransformingDriver(driver, scale, "Transform: Scaled 2x");
        DriverFeature.addDriver(scaledDriver.toString(), scaledDriver);

        CoordinateTransformer scaleDown = new ScaleTransformer(0.5, 0.5);
        VisitableDriver scaledDownDriver = new TransformingDriver(driver, scaleDown, "Transform: Scaled 0.5x");
        DriverFeature.addDriver(scaledDownDriver.toString(), scaledDownDriver);

        CoordinateTransformer flip = new FlipTransformer(false, true);
        VisitableDriver flippedDriver = new TransformingDriver(driver, flip, "Transform: Flipped Y");
        DriverFeature.addDriver(flippedDriver.toString(), flippedDriver);

        CoordinateTransformer rotate = new RotateTransformer(45.0);
        VisitableDriver rotatedDriver = new TransformingDriver(driver, rotate, "Transform: Rotated 45 degrees");
        DriverFeature.addDriver(rotatedDriver.toString(), rotatedDriver);

        VisitableDriver scaledAndRotatedDriver = new TransformingDriver(scaledDriver, rotate, "Transform: Scaled 2x & Rotated 45");
        DriverFeature.addDriver(scaledAndRotatedDriver.toString(), scaledAndRotatedDriver);

        CompositeDriver chaosCompositeDriver = new CompositeDriver("Chaos Composite Driver");
        chaosCompositeDriver.addDriver(driver);
        chaosCompositeDriver.addDriver(TrackingLoggerDriver);
        chaosCompositeDriver.addDriver(scaledDownDriver);
        DriverFeature.addDriver(chaosCompositeDriver.toString(), chaosCompositeDriver);

        driver = new LineDriverAdapter(drawerController, LineFactory.getBasicLine(), "basic");
        VisitableDriver animatedDriver = new RealTimeDriver(driver, 10, 10, "Real-Time Driver 1x speed");
        DriverFeature.addDriver(animatedDriver.toString(), animatedDriver);

        animatedDriver = new RealTimeDriver(driver, 5, 5, "Real-Time Driver 2x speed");
        DriverFeature.addDriver(animatedDriver.toString(), animatedDriver);

        animatedDriver = new RealTimeDriver(driver, 1, 1, "Real-Time Driver 10x speed");
        DriverFeature.addDriver(animatedDriver.toString(), animatedDriver);
    }

    private static void setupWindows(Application application) {

        CommandManagerWindow commandManager = new CommandManagerWindow(CommandsFeature.getDriverCommandManager());
        application.addWindowComponent("Command Manager", commandManager);

        CommandCatalogWindow commandCatalogWindow = new CommandCatalogWindow(
                CommandsFeature.getDriverCommandManager(),
                CommandsFeature.getCommandCatalog()
        );
        application.addWindowComponent("Command Catalog", commandCatalogWindow);

        CommandManagerWindowCommandChangeObserver windowObserver = new CommandManagerWindowCommandChangeObserver(
                commandManager);
        CommandsFeature.getDriverCommandManager().getChangePublisher().addSubscriber(windowObserver);



        DrawPanelController previewDrawController = new DrawPanelController();

        CommandPreviewWindow commandPreview =
                new CommandPreviewWindow(previewDrawController);

        application.addWindowComponent("Command Preview", commandPreview);

        VisitableDriver basicDriver =
                new LineDriverAdapter(previewDrawController, LineFactory.getBasicLine(), "basic");

        CoordinateTransformer scaleDown = new ScaleTransformer(0.5, 0.5);

        VisitableDriver scaledDownDriver =
                new TransformingDriver(basicDriver, scaleDown, "Preview Transform: Scaled 0.5x");

        CommandPreviewService previewService =
                new CommandPreviewService(previewDrawController, scaledDownDriver);

        CommandPreviewObserver previewObserver =
                new CommandPreviewObserver(
                        CommandsFeature.getDriverCommandManager(),
                        previewService
                );

        CommandsFeature.getDriverCommandManager()
                .getChangePublisher()
                .addSubscriber(previewObserver);
    }

    /**
     * Setup menu for adjusting logging settings.
     *
     * @param application Application context.
     */
    private static void setupLogger(Application application) {

        application.addComponentMenu(Logger.class, "Logger", 0);
        application.addComponentMenuElement(Logger.class, "Clear log",
                (ActionEvent e) -> application.flushLoggerOutput());
        application.addComponentMenuElement(Logger.class, "Fine level", (ActionEvent e) -> logger.setLevel(Level.FINE));
        application.addComponentMenuElement(Logger.class, "Info level", (ActionEvent e) -> logger.setLevel(Level.INFO));
        application.addComponentMenuElement(Logger.class, "Warning level",
                (ActionEvent e) -> logger.setLevel(Level.WARNING));
        application.addComponentMenuElement(Logger.class, "Severe level",
                (ActionEvent e) -> logger.setLevel(Level.SEVERE));
        application.addComponentMenuElement(Logger.class, "OFF logging", (ActionEvent e) -> logger.setLevel(Level.OFF));
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                Application app = new Application("Jobs 2D");

                // Przykład użycia automatycznego zarządzania funkcjami (features management)
                // Zarejestruj funkcje, które mają być automatycznie skonfigurowane
                FeaturesManager.registerFeature(new DrawerFeature());
                FeaturesManager.registerFeature(new CommandsFeature());
                FeaturesManager.registerFeature(new DriverFeature());
                FeaturesManager.registerFeature(new CanvasFeature());

                // Automatycznie skonfiguruj wszystkie zarejestrowane funkcje
                // To zastępuje ręczne wywołania setup dla każdej funkcji
                FeaturesManager.setupAllFeatures(app);

                setupDrivers(app);
                RecordingFeature.setup(DriverFeature.getDriverManager());
                setupPresetTests(app);
                setupCommandTests(app);
                setupLogger(app);
                setupWindows(app);

                app.setVisibility(true);
            }
        });
    }
}
