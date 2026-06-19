package it.polimi.ingsw.am32.client.view.gui.utils;

import it.polimi.ingsw.am32.log.LogLevel;
import it.polimi.ingsw.am32.log.LogManager;

/**
 * A small logging facade for GUI classes is represented.
 */
public final class GuiLogger {

    /**
     * Instantiation is prevented for the utility class.
     */
    private GuiLogger() {}

    /**
     * An informational message is logged.
     *
     * @param message the message to log
     */
    public static void info(String message) {
        LogManager.getInstance().log(LogLevel.INFO, message);
    }

    /**
     * A warning message is logged.
     *
     * @param message the message to log
     */
    public static void warning(String message) {
        LogManager.getInstance().log(LogLevel.WARNING, message);
    }

    /**
     * An error message is logged.
     *
     * @param message the message to log
     */
    public static void error(String message) {
        LogManager.getInstance().log(LogLevel.ERROR, message);
    }

    /**
     * An error message and throwable are logged.
     *
     * @param message the message to log
     * @param throwable the throwable to log
     */
    public static void error(String message, Throwable throwable) {
        LogManager.getInstance().log(LogLevel.ERROR, message, throwable);
    }
}
