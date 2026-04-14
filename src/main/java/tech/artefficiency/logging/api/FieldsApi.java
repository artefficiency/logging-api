package tech.artefficiency.logging.api;

import java.util.function.Supplier;

/**
 * Extends {@link Message} with explicit field-setting capabilities.
 *
 * <p>While domain message interfaces (extending {@link Message}) automatically
 * capture method calls as fields, {@code FieldsApi} provides an explicit API
 * for setting fields with custom names. This is useful when:</p>
 *
 * <ul>
 *   <li>The field name should differ from any method name</li>
 *   <li>Field names are dynamic or computed at runtime</li>
 *   <li>A more generic logging interface is needed</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 *
 * <h3>Creating a Fields-based Logger</h3>
 * <pre>{@code
 * Log<FieldsApi> log = FieldsLog.forCurrentClass();
 * }</pre>
 *
 * <h3>Setting Fields Explicitly</h3>
 * <pre>{@code
 * log.info()
 *     .field("userId").set(userId)
 *     .field("action").set("LOGIN")
 *     .field("timestamp").set(System.currentTimeMillis())
 *     .add("User logged in");
 * }</pre>
 *
 * <h3>Using Suppliers for Lazy Evaluation</h3>
 * <pre>{@code
 * log.info()
 *     .field("expensiveValue").set(() -> computeExpensiveValue())
 *     .field("currentTime").set(() -> Instant.now().toString())
 *     .add("Operation completed");
 * }</pre>
 *
 * <p>Using {@link Supplier} is recommended for values that are expensive to
 * compute, as the supplier is only invoked if the log entry will actually
 * be recorded.</p>
 *
 * @see Message
 * @see tech.artefficiency.logging.data.entries.DomainMessage DomainMessage
 */
public interface FieldsApi extends Message {

    /**
     * Begins setting a field with the specified name.
     *
     * @param name the name of the field
     * @return a {@link ValueSetter} for providing the field value
     * @throws NullPointerException if name is null
     */
    ValueSetter field(String name);

    /**
     * Allows setting the value of a field.
     *
     * <p>After obtaining a {@code ValueSetter} from {@link #field(String)},
     * use either:</p>
     * <ul>
     *   <li>{@link #set(Object)} - Direct value (evaluated immediately)</li>
     *   <li>{@link #set(Supplier)} - Lazy evaluation (only if logged)</li>
     * </ul>
     *
     * @see #field(String)
     */
    interface ValueSetter {

        /**
         * Sets the field value directly.
         *
         * <p>The value is evaluated immediately when this method is called.
         * Use this for simple values that are cheap to compute.</p>
         *
         * <pre>{@code
         * log.info()
         *     .field("count").set(42)
         *     .field("name").set("example")
         *     .add("Processed items");
         * }</pre>
         *
         * @param value the value to set
         * @return the parent {@link FieldsApi} for method chaining
         */
        default FieldsApi set(Object value) {
            return set(() -> value);
        }

        /**
         * Sets the field value using a supplier for lazy evaluation.
         *
         * <p>The supplier is only invoked if the log entry will actually
         * be recorded. This is useful for:</p>
         * <ul>
         *   <li>Expensive computations</li>
         *   <li>Values with side effects</li>
         *   <li>Values that may not be available at setup time</li>
         * </ul>
         *
         * <pre>{@code
         * log.info()
         *     .field("stackTrace").set(() -> ExceptionUtils.getStackTrace(e))
         *     .field("config").set(() -> Config.getCurrent().toString())
         *     .add("Operation details");
         * }</pre>
         *
         * @param value a supplier that provides the field value
         * @return the parent {@link FieldsApi} for method chaining
         * @throws NullPointerException if value is null
         */
        FieldsApi set(Supplier<Object> value);
    }
}
