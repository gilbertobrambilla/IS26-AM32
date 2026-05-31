package it.polimi.ingsw.am32.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * A singleton log manager is represented.
 */
public class LogManager {
    private static LogManager instance;
    private final List<LogObserver> observers;

    /**
     * The log manager is initialized.
     */
    private LogManager() {
        // observer list is initialized
        this.observers = new ArrayList<>();
    }

    /**
     * Singleton instance is retrieved.
     *
     * @return the log manager instance
     */
    public static synchronized LogManager getInstance() {
        if (instance == null) {
            // new instance is created
            instance = new LogManager();
        }
        // instance is returned
        return instance;
    }

    /**
     * Observer is registered to receive logs.
     *
     * @param observer the observer to be added
     */
    public synchronized void addObserver(LogObserver observer) {
        // observer is added
        observers.add(observer);
    }

    /**
     * Log is dispatched to all observers or printed as fallback.
     *
     * @param level the severity level of the log
     * @param message the complete log message including the component tag
     */
    public synchronized void log(LogLevel level, String message) {
        if (observers.isEmpty()) {
            // ansi color is determined based on severity
            String ansiColor = getAnsiColor(level);
            String ansiReset = "\033[0m";

            // fallback system out is used when no observers are present
            if (level == LogLevel.ERROR) {
                System.err.println(ansiColor + message + ansiReset);
            } else {
                System.out.println(ansiColor + message + ansiReset);
            }
            return;
        }

        // log is broadcast to all observers
        for (LogObserver observer : observers) {
            observer.onNewLog(level, message);
        }
    }

    /**
     * Log is dispatched with an attached exception stack trace.
     *
     * @param level the severity level of the log
     * @param message the context message of the error
     * @param throwable the exception to be logged
     */
    public synchronized void log(LogLevel level, String message, Throwable throwable) {
        // stack trace is extracted from the exception
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);

        // full message is constructed
        String fullMessage = message + "\n" + sw.toString();

        // original log method is invoked
        this.log(level, fullMessage);
    }

    /**
     * Ansi color code is retrieved based on log level.
     *
     * @param level the log severity level
     * @return the ansi escape string
     */
    public static String getAnsiColor(LogLevel level) {
        // color code is mapped
        switch (level) {
            case ERROR: return "\033[31m";      // red
            case WARNING: return "\033[33m";    // yellow
            case GAME_EVENT: return "\033[36m"; // cyan
            case INFO: default: return "\033[0m"; // default terminal color
        }
    }
}
