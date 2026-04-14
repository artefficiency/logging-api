package tech.artefficiency.logging.api;

/**
 * Provides sub-interfaces for message formatting operations.
 *
 * <p>This interface organizes message-related operations into focused contracts:</p>
 * <ul>
 *   <li>{@link ExceptionFormatter} - Configure exception logging</li>
 *   <li>{@link StackFormatter} - Add stack trace information</li>
 *   <li>{@link DefaultAdder} - Convenience methods for adding messages</li>
 *   <li>{@link Adder} - Core message addition</li>
 * </ul>
 *
 * @see Message
 */
public interface MessageApi {

    /**
     * Configures how an exception is formatted in the log entry.
     *
     * <p>After attaching an exception with {@link Message#exception(Throwable)},
     * use this interface to control stack trace inclusion, message suppression,
     * and ultimately add the log message.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * try {
     *     processOrder(orderId);
     * } catch (Exception e) {
     *     log.error()
     *         .exception(e)
     *         .stackMode(StackMode.FAIR)
     *         .add("Failed to process order: {}", orderId);
     * }
     * }</pre>
     *
     * @see Message#exception(Throwable)
     * @see StackMode
     */
    interface ExceptionFormatter extends DefaultAdder {

        /**
         * Disables stack trace generation for this exception.
         *
         * <p>This is useful when the exception details are sufficient and the
         * stack trace would add unnecessary noise or size to the log.</p>
         *
         * <pre>{@code
         * log.warn().exception(e).noStack().add("Warning without stack trace");
         * }</pre>
         *
         * @return this formatter for method chaining
         */
        default ExceptionFormatter noStack() {
            return stackMode(StackMode.NONE);
        }

        /**
         * Enables full stack trace generation for this exception.
         *
         * <p>This includes the complete call stack at the point where the
         * exception was created.</p>
         *
         * <pre>{@code
         * log.error().exception(e).fullStack().add("Critical error with full stack");
         * }</pre>
         *
         * @return this formatter for method chaining
         */
        default ExceptionFormatter fullStack() {
            return stackMode(StackMode.FULL);
        }

        /**
         * Sets the stack trace generation mode.
         *
         * @param mode the stack mode to use ({@link StackMode#NONE}, {@link StackMode#FAIR}, or {@link StackMode#FULL})
         * @return this formatter for method chaining
         * @throws NullPointerException if mode is null
         */
        ExceptionFormatter stackMode(StackMode mode);

        /**
         * Excludes the exception message from the log output.
         *
         * <p>This is useful when the message pattern already conveys the error
         * information and the exception's message would be redundant.</p>
         *
         * @return this formatter for method chaining
         */
        ExceptionFormatter noMessage();

        /**
         * Excludes the exception class name from the log output.
         *
         * <p>This reduces log size by omitting the fully qualified class name
         * while still including the message and stack trace.</p>
         *
         * @return this formatter for method chaining
         */
        ExceptionFormatter noClass();
    }

    /**
     * Allows adding stack trace information to a log entry.
     *
     * <p>Stack traces capture the call hierarchy at the point of logging, useful
     * for debugging to understand the execution path that led to the log entry.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * // Add stack trace with default mode (FAIR)
     * log.info().putStack().add("Checkpoint reached");
     *
     * // Add full stack trace
     * log.debug().putStack(StackMode.FULL).add("Debug checkpoint");
     *
     * // No stack trace
     * log.info().putStack(StackMode.NONE).add("Simple checkpoint");
     * }</pre>
     *
     * @see StackMode
     */
    interface StackFormatter {

        /**
         * Adds a stack trace using the default (FAIR) mode.
         *
         * @return a {@link DefaultAdder} to continue building the log entry
         */
        default DefaultAdder putStack() {
            return putStack(StackMode.FAIR);
        }

        /**
         * Adds a stack trace using the specified mode.
         *
         * @param mode the stack trace mode to use
         * @return a {@link DefaultAdder} to continue building the log entry
         * @throws NullPointerException if mode is null
         */
        DefaultAdder putStack(StackMode mode);
    }

    /**
     * Extends {@link Adder} with convenience methods for adding messages.
     *
     * <p>The default {@link #add()} method allows committing the log entry
     * without an explicit message, which is useful when all information is
     * captured in structured fields.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * // Log with message
     * log.info().add("Order {} shipped to {}", orderId, address);
     *
     * // Log without message (structured fields only)
     * log.info().add();
     *
     * // Log with exception and message
     * log.error().exception(e).add("Operation failed");
     * }</pre>
     */
    interface DefaultAdder extends Adder {

        /**
         * Commits the log entry without a message.
         *
         * <p>This is useful when the log entry contains only structured fields
         * and no additional text message is needed.</p>
         *
         * <pre>{@code
         * log.info()
         *     .userId(userId)
         *     .action("LOGIN")
         *     .add();  // No message, fields are sufficient
         * }</pre>
         */
        default void add() {
            add(null);
        }
    }

    /**
     * Defines the contract for adding a message to a log entry.
     *
     * <p>The message can include placeholders that will be formatted with
     * the provided parameters. The placeholder style depends on the configuration:</p>
     * <ul>
     *   <li><strong>SLF4J style</strong>: {@code "{}"} placeholders</li>
     *   <li><strong>String.format style</strong>: {@code "%s", "%d"} placeholders</li>
     * </ul>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * // With message pattern and parameters
     * log.info().add("User {} logged in from {}", username, ipAddress);
     *
     * // With null pattern and only parameters
     * log.info().add(null, username, ipAddress);
     *
     * // With empty pattern
     * log.debug().add("");  // Logs only structured fields
     *
     * // With no pattern (null)
     * log.debug().add(null);  // No message, only structured fields
     * }</pre>
     *
     * @see tech.artefficiency.logging.configuration.Configuration.Entry.PatternStyle
     */
    interface Adder {

        /**
         * Adds parameters without an explicit message pattern.
         *
         * <p>This is a convenience method that treats the parameters as values
         * to be logged directly, without a message template. Useful for quick
         * logging of values without formatting:</p>
         *
         * <pre>{@code
         * log.info().add("simple value");
         * log.info().add(42, "text", someObject);
         * }</pre>
         *
         * @param parameters the values to log directly
         */
        default void add(Object... parameters) {
            add(null, parameters);
        }

        /**
         * Adds a formatted message to the log entry.
         *
         * @param pattern    the message pattern with placeholders, or null for no message
         * @param parameters the values to insert into the placeholders
         */
        void add(String pattern, Object... parameters);
    }
}
