package it.polimi.ingsw.am32.log;

/**
 * A listener for log events is represented.
 */
public interface LogObserver {
    /**
     * Log message is received from the manager.
     *
     * @param level the severity level of the log
     * @param message the complete log message including the component tag
     */
    void onNewLog(LogLevel level, String message);
}
