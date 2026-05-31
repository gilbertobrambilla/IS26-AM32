package it.polimi.ingsw.am32.network.exceptions;

/**
 * Custom runtime exception thrown for network-related errors.
 */
public class NetworkException extends RuntimeException {

    /**
     * Constructs a new network exception with the specified detail message.
     *
     * @param message the detail message
     */
    public NetworkException(String message) {
        super(message);
    }

    /**
     * Constructs a new network exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
