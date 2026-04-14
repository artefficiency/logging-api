package tech.artefficiency.logging.api;

/**
 * Represents a log message with support for structured fields, exceptions, stack traces, and layers.
 *
 * <p>Message is the core interface for building log entries. It combines:</p>
 * <ul>
 *   <li>{@link LayerApi.NameSetter} - Create nested layers/spans with duration tracking</li>
 *   <li>{@link MessageApi.StackFormatter} - Add stack trace information</li>
 *   <li>{@link MessageApi.Adder} - Set the final message text</li>
 * </ul>
 *
 * <h2>Domain Interface Approach</h2>
 *
 * <p>Extend {@code Message} to define type-safe structured fields:</p>
 *
 * <pre>{@code
 * public interface OrderLog extends Message {
 *     OrderLog orderId(String id);
 *     OrderLog customerId(String customerId);
 *     OrderLog totalAmount(double amount);
 * }
 *
 * Log<OrderLog> log = DomainLog.within(OrderLog.class).forCurrentClass();
 *
 * log.info()
 *     .orderId("ORD-12345")
 *     .customerId("CUST-001")
 *     .totalAmount(99.99)
 *     .add("Order processed");
 * }</pre>
 *
 * <h2>FieldsApi Approach</h2>
 *
 * <p>Use {@link FieldsApi} for dynamic field names:</p>
 *
 * <pre>{@code
 * Log<FieldsApi> log = FieldsLog.forClass(MyService.class);
 *
 * log.info()
 *     .field("orderId").set("ORD-12345")
 *     .field("customerId").set("CUST-001")
 *     .field("totalAmount").set(99.99)
 *     .add("Order processed");
 * }</pre>
 *
 * <h2>Exception Handling</h2>
 *
 * <p>Use {@link #exception(Throwable)} to log exceptions with optional stack traces:</p>
 *
 * <pre>{@code
 * log.error().exception(e).add("Operation failed");
 * log.warn().exception(e).noStack().add("Warning with suppressed stack");
 * }</pre>
 *
 * <h2>Layer/span Tracking</h2>
 *
 * <p>Use {@link LayerApi.NameSetter#layer(String)} with try-with-resources
 * for automatic duration tracking:</p>
 *
 * <pre>{@code
 * try (var l1 = log.info().layer("l1").start()) {
 *     doWork();
 *     try (var l2 = log.info().layer("l2").start()) {
 *         doMoreWork();
 *     }
 * }
 * }</pre>
 *
 * <p>Produces indented output with duration markers:</p>
 * <pre>{@code
 * 09:34:25 {+} l1:
 * 09:34:25    {+} l2:
 * 09:34:25    {-} l2: [PT0.005S]
 * 09:34:25 {-} l1: [PT0.010S]
 * }</pre>
 *
 * @see LayerApi
 * @see MessageApi
 * @see FieldsApi
 */
public interface Message extends LayerApi.NameSetter, MessageApi.StackFormatter, MessageApi.Adder {

    /**
     * Attaches an exception to the log entry.
     *
     * <p>This method begins the exception formatting chain, allowing configuration
     * of how the exception and its stack trace are included in the log entry.</p>
     *
     * <h3>Basic Usage</h3>
     * <pre>{@code
     * try {
     *     riskyOperation();
     * } catch (Exception e) {
     *     log.error().exception(e).add("Operation failed");
     * }
     * }</pre>
     *
     * <h3>Configuring Stack Trace</h3>
     * <pre>{@code
     * // Log without stack trace (reduces log size)
     * log.warn().exception(e).noStack().add("Minor issue");
     *
     * // Log with full stack trace
     * log.error().exception(e).fullStack().add("Critical error");
     *
     * // Log with custom stack mode
     * log.error().exception(e).stackMode(StackMode.FAIR).add("Error with fair stack");
     *
     * // Exclude the exception message
     * log.error().exception(e).noMessage().add("Error occurred");
     *
     * // Exclude the class name from stack trace
     * log.error().exception(e).noClass().add("Error without class info");
     * }</pre>
     *
     * @param exception the exception to attach to the log entry
     * @return an {@link MessageApi.ExceptionFormatter} for configuring exception formatting
     * @throws NullPointerException if exception is null
     */
    MessageApi.ExceptionFormatter exception(Throwable exception);
}
