package tech.artefficiency.logging.api;

import tech.artefficiency.logging.tools.stack.StackHelper;

/**
 * Provides interfaces for creating nested logging layers (spans) with duration tracking.
 *
 * <p>Layers serve two main purposes:</p>
 * <ol>
 *   <li><strong>Duration tracking</strong> - Measure and report operation elapsed time</li>
 *   <li><strong>Visual offsets</strong> - Indent nested operations for easier log analysis</li>
 * </ol>
 *
 * <h2>Visual Offset Structure</h2>
 *
 * <p>Nested layers produce indented output with markers to visualize call hierarchy:</p>
 * <ul>
 *   <li>{@code {+}} - Layer entry marker</li>
 *   <li>{@code {-}} - Layer exit marker with duration</li>
 * </ul>
 *
 * <pre>{@code
 * try (var l1 = log.info().layer("l1").start()) {
 *     try (var l2 = log.info().layer("l2").start()) {
 *         try (var l3 = log.info().layer("l3").start()) {
 *             try (var l4 = log.info().layer("l4").start()) {
 *                 l4.report("success");
 *             }
 *         }
 *     }
 * }
 * }</pre>
 *
 * <p>Produces:</p>
 * <pre>{@code
 * 09:34:25.105 {+} l1:
 * 09:34:25.105    {+} l2:
 * 09:34:25.105       {+} l3:
 * 09:34:25.107           l4: success
 * 09:34:25.107       {-} l3: [PT0.004S]
 * 09:34:25.107    {-} l2: [PT0.005S]
 * 09:34:25.107 {-} l1: [PT0.006S]
 * }</pre>
 *
 * <h2>Layer Flow</h2>
 * <ol>
 *   <li>{@link NameSetter} - Specify the layer name</li>
 *   <li>{@link Starter} - Begin the layer timing</li>
 *   <li>{@link Reporter} - Report results and optionally end the layer</li>
 * </ol>
 *
 * @see Message#layer(String)
 */
public interface LayerApi {

    /**
     * Stack helper configured to ignore framework classes.
     */
    StackHelper STACK = new StackHelper().withKnown(NameSetter.class);

    /**
     * Provides methods for setting the layer name.
     *
     * <p>A layer represents a named operation span that can track duration.
     * Layers can be named explicitly with a string or automatically using
     * the current method name.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * // Explicit layer name
     * log.info().layer("processOrder").start();
     *
     * // Automatic layer name from method
     * log.info().methodLayer().start();
     * // If called from processOrder(), creates layer "processOrder"
     * }</pre>
     */
    interface NameSetter {

        /**
         * Creates a layer named after the current method.
         *
         * <p>This is useful for creating automatic layer names based on
         * the calling method, reducing boilerplate when logging method
         * entry/exit.</p>
         *
         * <pre>{@code
         * public void processOrder(Order order) {
         *     // Layer name will be "processOrder"
         *     try (var layer = log.info().methodLayer().start()){
         *         // ... do work ...
         *         layer.report("success");
         *     }
         * }
         * }</pre>
         *
         * @return a {@link Starter} for the auto-named layer
         */
        default Starter methodLayer() {
            return layer(STACK.getFirstUnknownFrame().getMethodName());
        }

        /**
         * Creates a layer with the specified name.
         *
         * @param name the name of the layer (e.g., "database-query", "http-request")
         * @return a {@link Starter} for the named layer
         * @throws NullPointerException if name is null
         */
        Starter layer(String name);
    }

    /**
     * Allows starting a layer and optionally providing initial parameters.
     *
     * <p>Starting a layer begins the duration timer. Parameters can be provided
     * at start time to capture input state.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * // Start with no parameters
     * log.info().layer("query").start();
     *
     * // Start with parameters
     * log.info().layer("query").start(sql, params);
     *
     * // Start with message pattern and parameters
     * log.info().layer("query").start("Executing: {}", sql);
     * }</pre>
     */
    interface Starter {

        /**
         * Starts the layer with no initial parameters.
         *
         * @return a {@link Reporter} to continue building the layer
         */
        default Reporter start() {
            return start(null, (Object[]) null);
        }

        /**
         * Starts the layer with parameters for the implicit message.
         *
         * <p>The parameters will be formatted as comma-separated string.</p>
         *
         * @param parameters the parameters to include in the layer start
         * @return a {@link Reporter} to continue building the layer
         */
        default Reporter start(Object... parameters) {
            return start(null, parameters);
        }

        /**
         * Starts the layer with a message pattern and parameters.
         *
         * @param pattern    the message pattern, or null for no message
         * @param parameters the parameters for the pattern
         * @return a {@link Reporter} to continue building the layer
         */
        Reporter start(String pattern, Object... parameters);
    }

    /**
     * Allows reporting results, ending the layer, and releasing resources.
     *
     * <p>The Reporter provides methods for:</p>
     * <ul>
     *   <li>Reporting results at various log levels</li>
     *   <li>Reporting with custom messages</li>
     *   <li>Skipping the layer entirely</li>
     * </ul>
     *
     * <p><strong>Important:</strong> This interface extends {@link AutoCloseable}.
     * The {@link #close()} method <strong>must</strong> be called to properly
     * end the layer. Always use try-with-resources to prevent log structure corruption.</p>
     *
     * <h3>Correct Usage</h3>
     * <pre>{@code
     * // Always use try-with-resources
     * try (var ignored = log.info().layer("operation").start()) {
     *     // ... perform operation ...
     * }
     * }</pre>
     *
     * <h3>Reporting Results</h3>
     * <pre>{@code
     * try (var layer = log.info().layer("operation").start()) {
     *     // Report result and close
     *     return layer.report(computeResult());
     * }
     *
     * try (var layer = log.info().layer("operation").start()) {
     *     // Report with message
     *     layer.reportInfo("Total processed: {}", processedCount);
     * }
     * }</pre>
     *
     * <h3>Error Reporting</h3>
     * <pre>{@code
     * try (var ignored = log.info().layer("operation").start()) {
     *     performOperation();
     * } catch (Exception e) {
     *     log.error().exception(e).add("Operation failed");
     *     throw e;
     * }
     * }</pre>
     */
    interface Reporter extends AutoCloseable {

        /**
         * Reports the result of the layer operation.
         *
         * <p>The result is formatted using the default pattern and logged
         * without an explicit level (inherits from parent).</p>
         *
         * @param <R>    the type of the result
         * @param result the result to report
         * @return the original result (for chaining)
         */
        default <R> R report(R result) {
            return report(null, result);
        }

        /**
         * Reports an error result at ERROR level.
         *
         * @param <R>    the type of the result
         * @param result the result to report
         * @return the original result (for chaining)
         */
        default <R> R reportError(R result) {
            return report(Level.ERROR, result);
        }

        /**
         * Reports a warning result at WARN level.
         *
         * @param <R>    the type of the result
         * @param result the result to report
         * @return the original result (for chaining)
         */
        default <R> R reportWarn(R result) {
            return report(Level.WARN, result);
        }

        /**
         * Reports an informational result at INFO level.
         *
         * @param <R>    the type of the result
         * @param result the result to report
         * @return the original result (for chaining)
         */
        default <R> R reportInfo(R result) {
            return report(Level.INFO, result);
        }

        /**
         * Reports a debug result at DEBUG level.
         *
         * @param <R>    the type of the result
         * @param result the result to report
         * @return the original result (for chaining)
         */
        default <R> R reportDebug(R result) {
            return report(Level.DEBUG, result);
        }

        /**
         * Reports a trace result at TRACE level.
         *
         * @param <R>    the type of the result
         * @param result the result to report
         * @return the original result (for chaining)
         */
        default <R> R reportTrace(R result) {
            return report(Level.TRACE, result);
        }

        /**
         * Reports a result at the specified level.
         *
         * @param <R>    the type of the result
         * @param level  the log level for the report
         * @param result the result to report
         * @return the original result (for chaining)
         */
        default <R> R report(Level level, R result) {
            report(level, null, result);
            return result;
        }

        /**
         * Reports a message with the inherited level.
         *
         * @param pattern the message pattern
         * @param args    the pattern arguments
         */
        default void report(String pattern, Object... args) {
            report(null, pattern, args);
        }

        /**
         * Reports an error message.
         *
         * @param pattern the message pattern
         * @param args    the pattern arguments
         */
        default void reportError(String pattern, Object... args) {
            report(Level.ERROR, pattern, args);
        }

        /**
         * Reports a warning message.
         *
         * @param pattern the message pattern
         * @param args    the pattern arguments
         */
        default void reportWarn(String pattern, Object... args) {
            report(Level.WARN, pattern, args);
        }

        /**
         * Reports an informational message.
         *
         * @param pattern the message pattern
         * @param args    the pattern arguments
         */
        default void reportInfo(String pattern, Object... args) {
            report(Level.INFO, pattern, args);
        }

        /**
         * Reports a debug message.
         *
         * @param pattern the message pattern
         * @param args    the pattern arguments
         */
        default void reportDebug(String pattern, Object... args) {
            report(Level.DEBUG, pattern, args);
        }

        /**
         * Reports a trace message.
         *
         * @param pattern the message pattern
         * @param args    the pattern arguments
         */
        default void reportTrace(String pattern, Object... args) {
            report(Level.TRACE, pattern, args);
        }

        /**
         * Reports a message at the specified level.
         *
         * @param level   the log level, or null to use inherited level
         * @param pattern the message pattern, or null for no message
         * @param args    the pattern arguments
         */
        void report(Level level, String pattern, Object... args);

        /**
         * Skips this layer without logging anything.
         *
         * <p>If no log entries (layers or plain messages) were recorded after
         * {@link Starter#start()}, calling {@code skip()} suppresses the entire layer:
         * neither the entry marker {@code {+}} nor the exit marker {@code {-}}
         * will be printed.</p>
         *
         * <p>If any log entry was written after {@link Starter#start()}, calling
         * {@code skip()} has no effect and the layer will be logged normally
         * with both {@code {+}} and {@code {-}} markers.</p>
         *
         * <pre>{@code
         * // Nothing printed - no logs after start()
         * try (var l = log.info().layer("optional").start()) {
         *     if (shouldSkip()) {
         *         l.skip();
         *     }
         * }
         *
         * // Layer logged normally - plain message was written
         * try (var l = log.info().layer("operation").start()) {
         *     log.info().add("Plain message");
         *     l.skip(); // No effect, layer still logged
         * }
         * // Output:
         * // 10:02:33 {+} operation:
         * // 10:02:33    Plain message
         * // 10:02:33 {-} operation: [PT0.001S]
         * }</pre>
         */
        void skip();

        /**
         * Ends the layer and logs its completion.
         *
         * <p><strong>Important:</strong> This method MUST be called to properly
         * close the layer and maintain log structure integrity. Failing to call
         * {@code close()} will corrupt the nested layer structure.</p>
         *
         * <h3>Required: Use try-with-resources</h3>
         *
         * <p>Always use the try-with-resources pattern to ensure the layer
         * is properly closed even if an exception occurs:</p>
         *
         * <pre>{@code
         * try (Reporter ignored = log.info().layer("operation").start()) {
         *     // ... perform operation ...
         * } // Layer automatically closed and logged
         * }</pre>
         *
         * <h3>Anti-patterns</h3>
         *
         * <p>Do NOT do this - it will corrupt the log structure:</p>
         * <pre>{@code
         * // WRONG - layer not closed
         * log.info().layer("operation").start();
         * doWork();
         *
         * // WRONG - exception can skip close()
         * Reporter reporter = log.info().layer("operation").start();
         * doWork();
         * reporter.report(); // Exception here = corrupted structure
         *
         * // CORRECT
         * try (Reporter ignored = log.info().layer("operation").start()) {
         *     doWork();
         * }
         * }</pre>
         *
         * <p>When closed without explicit reporting, the layer is logged
         * at INFO level with its duration.</p>
         *
         * @see AutoCloseable
         */
        @Override
        void close();
    }
}
