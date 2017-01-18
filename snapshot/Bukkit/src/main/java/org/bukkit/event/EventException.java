package org.bukkit.event;

/**
 * A wrapper used to propagate exceptions thrown from {@link EventBody}s
 */
public class EventException extends Exception {
    private static final long serialVersionUID = 3532808232324183999L;
    private final Event event;

    /**
     * Constructs a new EventException based on the given Exception
     *
     * @param cause Exception that triggered this Exception
     */
    public EventException(Throwable cause, Event event, String message) {
        super(message, cause);
        this.event = event;
    }

    /**
     * Constructs a new EventException based on the given Exception
     *
     * @param throwable Exception that triggered this Exception
     */
    public EventException(Throwable throwable, Event event) {
        this(throwable, event, null);
    }

    /**
     * Constructs a new EventException based on the given Exception
     *
     * @param throwable Exception that triggered this Exception
     */
    public EventException(Throwable throwable) {
        this(throwable, (Event) null);
    }

    /**
     * Constructs a new EventException
     */
    public EventException() {
        this((Throwable) null);
    }

    /**
     * Constructs a new EventException with the given message
     *
     * @param cause The exception that caused this
     * @param message The message
     */
    public EventException(Throwable cause, String message) {
        this(cause, null, message);
    }

    /**
     * Constructs a new EventException with the given message
     *
     * @param message The message
     */
    public EventException(String message) {
        this(null, message);
    }

    /**
     * @return the {@link Event} that generated this exception
     */
    public Event getEvent() {
        return event;
    }
}
