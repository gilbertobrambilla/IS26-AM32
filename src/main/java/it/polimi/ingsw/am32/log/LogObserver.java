package it.polimi.ingsw.am32.log;

/**
 * Interface to be implemented by components listening for log events.
 */
public interface LogObserver {
    /**
     * Log message is received from the manager.
     *
     * @param level The severity level of the log.
     * @param message The complete log message including the component tag.
     */
    void onNewLog(LogLevel level, String message);
}
