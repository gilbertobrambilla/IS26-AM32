package it.polimi.ingsw.am32.network.exceptions;

/**
 * A custom runtime exception for network-related errors is represented.
 */
public class NetworkException extends RuntimeException {

    /**
     * A new network exception with the specified detail message is constructed.
     *
     * @param message the detail message
     */
    public NetworkException(String message) {
        super(message);
    }

    /**
     * A new network exception with the specified detail message and cause is constructed.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
