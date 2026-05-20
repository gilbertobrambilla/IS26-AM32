package it.polimi.ingsw.am32.client.view.gui.utils;

import it.polimi.ingsw.am32.log.LogLevel;
import it.polimi.ingsw.am32.log.LogManager;

/**
 * Small logging facade for GUI classes.
 */
public final class GuiLogger {

    private GuiLogger() {}

    public static void info(String message) {
        LogManager.getInstance().log(LogLevel.INFO, message);
    }

    public static void warning(String message) {
        LogManager.getInstance().log(LogLevel.WARNING, message);
    }

    public static void error(String message) {
        LogManager.getInstance().log(LogLevel.ERROR, message);
    }

    public static void error(String message, Throwable throwable) {
        LogManager.getInstance().log(LogLevel.ERROR, message, throwable);
    }
}
