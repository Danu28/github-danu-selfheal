/**
 * An exception class used to indicate healing-related issues or errors.
 */
package com.epam.healenium;

/**
 * An exception class used to indicate healing-related issues or errors.
 */
public class HealException extends RuntimeException {

    /**
     * Constructs a new HealException with the specified detail message.
     *
     * @param message The detail message explaining the exception.
     */
    public HealException(String message) {
        super(message);
    }

    /**
     * Constructs a new HealException with the specified detail message and cause.
     *
     * @param message The detail message explaining the exception.
     * @param cause   The cause of the exception.
     */
    public HealException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new HealException with the specified cause.
     *
     * @param cause The cause of the exception.
     */
    public HealException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new HealException with no detail message.
     */
    public HealException() {
    }
}
