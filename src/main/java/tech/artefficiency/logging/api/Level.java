package tech.artefficiency.logging.api;

/**
 * Defines the severity levels for log entries.
 *
 * <p>Log levels are used to categorize log entries by importance, allowing
 * filtering of logs based on the configured threshold. Entries below the
 * configured level are typically suppressed.</p>
 *
 * <h2>Level Hierarchy</h2>
 * <p>Levels are ordered from highest to lowest severity:</p>
 * <ol>
 *   <li><strong>ERROR</strong> (40) - Error conditions requiring immediate attention</li>
 *   <li><strong>WARN</strong> (30) - Warning conditions indicating potential problems</li>
 *   <li><strong>INFO</strong> (20) - Informational messages about normal operations</li>
 *   <li><strong>DEBUG</strong> (10) - Detailed information for debugging</li>
 *   <li><strong>TRACE</strong> (0)  - Finest granularity, trace-level details</li>
 * </ol>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Using convenience methods
 * log.trace().add("Detailed trace");
 * log.debug().add("Debug information");
 * log.info().add("Informational message");
 * log.warn().add("Warning condition");
 * log.error().add("Error occurred");
 *
 * // Using explicit level
 * log.level(Level.DEBUG).add("Explicit level");
 * }</pre>
 *
 * <h2>Filtering</h2>
 * <p>When the logging system is configured with a minimum level, entries
 * below that level are suppressed:</p>
 * <pre>{@code
 * // If minimum level is INFO (20):
 * log.trace().add("Suppressed");  // Not logged (0 < 20)
 * log.debug().add("Suppressed");  // Not logged (10 < 20)
 * log.info().add("Logged");      // Logged (20 >= 20)
 * log.warn().add("Logged");       // Logged (30 >= 20)
 * log.error().add("Logged");       // Logged (40 >= 20)
 * }</pre>
 *
 * @see LogApi.LevelSetter
 */
public enum Level {
    /**
     * Error level for error conditions that should be addressed immediately.
     *
     * <p>Use ERROR for:</p>
     * <ul>
     *   <li>Failed operations that prevent a feature from working</li>
     *   <li>Uncaught exceptions</li>
     *   <li>Data corruption or integrity issues</li>
     *   <li>Configuration errors</li>
     * </ul>
     *
     * <p>Example:</p>
     * <pre>{@code
     * log.error().exception(e).add("Failed to connect to database: {}", url);
     * }</pre>
     */
    ERROR(40),

    /**
     * Warning level for potentially harmful situations that deserve attention.
     *
     * <p>Use WARN for:</p>
     * <ul>
     *   <li>Deprecated API usage</li>
     *   <li>Recoverable errors</li>
     *   <li>Unexpected but non-fatal conditions</li>
     *   <li>Performance degradation</li>
     * </ul>
     *
     * <p>Example:</p>
     * <pre>{@code
     * log.warn().add("Using deprecated method, please upgrade to {}", newMethod);
     * }</pre>
     */
    WARN(30),

    /**
     * Info level for general informational messages about application progress.
     *
     * <p>Use INFO for:</p>
     * <ul>
     *   <li>Service startup/shutdown</li>
     *   <li>Significant business events</li>
     *   <li>Periodic status updates</li>
     *   <li>Request/response logging</li>
     * </ul>
     *
     * <p>Example:</p>
     * <pre>{@code
     * log.info().add("Server started on port {}", port);
     * log.info().add("Order {} shipped to {}", orderId, address);
     * }</pre>
     */
    INFO(20),

    /**
     * Debug level for detailed information useful during development.
     *
     * <p>Use DEBUG for:</p>
     * <ul>
     *   <li>Variable values</li>
     *   <li>Method entry/exit</li>
     *   <li>Intermediate results</li>
     *   <li>Algorithm steps</li>
     * </ul>
     *
     * <p>Example:</p>
     * <pre>{@code
     * log.debug().add("Query result: {}", queryResult);
     * log.debug().add("Cache hit: {}", cacheHit);
     * }</pre>
     */
    DEBUG(10),

    /**
     * Trace level for the finest granularity of logging.
     *
     * <p>Use TRACE for:</p>
     * <ul>
     *   <li>Method parameter values</li>
     *   <li>Loop iterations</li>
     *   <li>State transitions</li>
     *   <li>Comprehensive debugging output</li>
     * </ul>
     *
     * <p>Example:</p>
     * <pre>{@code
     * log.trace().add("Entering method with params: {} {} {}", arg1, arg2, arg3);
     * log.trace().add("Loop iteration: i={}", i);
     * }</pre>
     */
    TRACE(0);

    private final int value;

    /**
     * Creates a Level with the specified numeric value.
     *
     * @param value the numeric severity value
     */
    Level(int value) {
        this.value = value;
    }

    /**
     * Numeric value representing the severity of this level.
     *
     * <p>Higher values indicate higher severity. This numeric value is used
     * for comparing levels and determining if a log entry should be recorded
     * based on the configured minimum level.</p>
     */
    public int value() {
        return value;
    }
}
