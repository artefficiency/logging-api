package tech.artefficiency.logging.api;

/**
 * Represents a logging entry point that produces structured log messages.
 *
 * <p>A {@code Log} is the primary entry point for creating log entries. It provides
 * methods to set the log level and optionally configure sampling strategies to
 * control log volume.</p>
 *
 * <h2>Domain Interface Example</h2>
 *
 * <p>Define a custom message interface for type-safe structured fields:</p>
 * <pre>{@code
 * public interface OrderLog extends Message {
 *     OrderLog orderId(String id);
 *     OrderLog amount(double amount);
 * }
 *
 * Log<OrderLog> log = DomainLog.within(OrderLog.class).forCurrentClass();
 *
 * log.info().orderId("12345").amount(99.99).add("Order placed");
 * }</pre>
 *
 * <h2>FieldsApi Example</h2>
 *
 * <p>Use {@link FieldsApi} for dynamic field names:</p>
 * <pre>{@code
 * Log<FieldsApi> log = FieldsLog.forClass(MyService.class);
 *
 * log.info()
 *     .field("orderId").set("12345")
 *     .field("amount").set(99.99)
 *     .add("Order placed");
 * }</pre>
 *
 * <h2>Logger Creation</h2>
 *
 * <p>Use {@link tech.artefficiency.logging.DomainLog} for domain interfaces:</p>
 * <ul>
 *   <li>{@code DomainLog.within(MessageInterface.class)} - Specify the message type</li>
 *   <li>{@code .forClass(Class)} - Logger for a specific class</li>
 *   <li>{@code .forCurrentClass()} - Logger for the calling class</li>
 * </ul>
 *
 * <p>Use {@link tech.artefficiency.logging.FieldsLog} for generic field logging:</p>
 * <ul>
 *   <li>{@code FieldsLog.forClass(Class)} - Logger for a specific class</li>
 *   <li>{@code FieldsLog.forCurrentClass()} - Logger for the calling class</li>
 * </ul>
 *
 * @param <Entry> the message type that will be returned by level setter methods
 * @see LogApi.LevelSetter
 * @see LogApi.Sampler
 * @see FieldsApi
 * @see tech.artefficiency.logging.DomainLog
 * @see tech.artefficiency.logging.FieldsLog
 */
public interface Log<Entry> extends LogApi.Sampler<Entry> {
}
