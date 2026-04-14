package tech.artefficiency.logging.api;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * Defines the contract for log level setting and sampling strategies.
 *
 * <p>This interface provides a fluent API for setting log levels and configuring
 * sampling behaviors that control when log entries are actually recorded.</p>
 *
 * <h2>Sampling Strategies</h2>
 *
 * <p>Sampling allows controlling log volume by only recording a subset of log
 * entries based on various criteria:</p>
 *
 * <ul>
 *   <li><strong>Hit-based sampling</strong>: Log every N occurrences</li>
 *   <li><strong>Time-based sampling</strong>: Log once per time interval</li>
 *   <li><strong>Predicate-based sampling</strong>: Custom sampling logic</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Log every 100th occurrence
 * log.every(100).info().add("This is logged infrequently");
 *
 * // Log once per minute
 * log.every(Duration.ofMinutes(1)).warn().add("Periodic warning");
 *
 * // Custom sampling logic
 * log.every(() -> someCondition).debug().add("Conditional debug");
 * }</pre>
 *
 * @see Log
 */
public interface LogApi {

    /**
     * Provides methods for configuring sampling strategies before setting a log level.
     *
     * <p>Sampling allows controlling log volume by selectively logging entries based
     * on hit count, time intervals, or custom predicates.</p>
     *
     * <h3>Sampling with Hit Count</h3>
     * <p>Use {@link #every(int)} to log every Nth occurrence:</p>
     * <pre>{@code
     * log.every(10).info().add("This logs every 10th time");
     * }</pre>
     *
     * <h3>Sampling with Timeout</h3>
     * <p>Use {@link #every(Duration)} to log at most once per interval:</p>
     * <pre>{@code
     * log.every(Duration.ofSeconds(30)).debug().add("Debug log throttled to 30s");
     * }</pre>
     *
     * <h3>Custom Sampling</h3>
     * <p>Use {@link #every(Supplier)} for custom sampling logic:</p>
     * <pre>{@code
     * log.every(() -> expensiveCheck()).trace().add("Traced after expensive check");
     * }</pre>
     *
     * @param <Entry> the message type returned when a log level is set
     */
    interface Sampler<Entry> extends LevelSetter<Entry> {

        /**
         * Configures sampling to log every Nth occurrence.
         *
         * <p>For example, with {@code hits = 100}, only every 100th call will
         * actually produce a log entry. The others will return a dummy proxy
         * that silently discards all operations.</p>
         *
         * @param hits the number of occurrences between each logged entry
         * @return a level setter configured with hit-based sampling
         * @throws IllegalArgumentException if hits is less than 1
         */
        LevelSetter<Entry> every(int hits);

        /**
         * Configures sampling to log at most once per time interval.
         *
         * <p>For example, with a 1-minute timeout, only the first log entry
         * within each minute will be recorded. Subsequent calls within the
         * same minute will be silently discarded.</p>
         *
         * @param timeout the minimum time between log entries
         * @return a level setter configured with time-based sampling
         * @throws NullPointerException if timeout is null
         */
        LevelSetter<Entry> every(Duration timeout);

        /**
         * Configures sampling using a custom predicate.
         *
         * <p>The predicate is evaluated for each log attempt. If it returns
         * {@code true}, the entry is logged; otherwise, a dummy proxy is returned.</p>
         *
         * <p>This is useful for implementing complex sampling strategies:</p>
         * <pre>{@code
         * // Log only during business hours
         * log.every(() -> LocalTime.now().getHour() < 17).info().add("Business hours only");
         *
         * // Log based on request sampling
         * log.every(() -> request.isSampled()).debug().add("Sampled request");
         * }</pre>
         *
         * @param predicate a supplier that returns true to log, false to skip
         * @return a level setter configured with predicate-based sampling
         * @throws NullPointerException if predicate is null
         */
        LevelSetter<Entry> every(Supplier<Boolean> predicate);
    }

    /**
     * Provides methods for setting the log level.
     *
     * <p>The log level determines the severity of the log entry and is typically
     * used to filter which entries are recorded based on the configured logging
     * level threshold.</p>
     *
     * <h3>Log Levels</h3>
     * <ul>
     *   <li>{@link Level#ERROR} - Error conditions (highest severity)</li>
     *   <li>{@link Level#WARN}  - Warning conditions</li>
     *   <li>{@link Level#INFO}  - Informational messages</li>
     *   <li>{@link Level#DEBUG} - Debug-level messages</li>
     *   <li>{@link Level#TRACE} - Trace-level messages (lowest severity)</li>
     * </ul>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * // Using convenience methods
     * log.info().add("Information message");
     * log.warn().add("Warning message");
     * log.error().add("Error message");
     *
     * // Using explicit level
     * log.level(Level.DEBUG).add("Debug message");
     * }</pre>
     *
     * @param <Entry> the message type that will be returned when setting the level
     */
    interface LevelSetter<Entry> {

        /**
         * Sets the log level to ERROR and returns the message builder.
         *
         * <p>ERROR level is used for error conditions that need immediate attention.
         * Examples include failed operations, exceptions, or critical failures.</p>
         *
         * @return the message builder configured for ERROR level
         */
        default Entry error() {
            return level(Level.ERROR);
        }

        /**
         * Sets the log level to WARN and returns the message builder.
         *
         * <p>WARN level is used for warning conditions that may indicate potential
         * problems. Examples include deprecated API usage, configuration issues,
         * or recovered errors.</p>
         *
         * @return the message builder configured for WARN level
         */
        default Entry warn() {
            return level(Level.WARN);
        }

        /**
         * Sets the log level to INFO and returns the message builder.
         *
         * <p>INFO level is used for informational messages that highlight the
         * progress of the application. Examples include service startup,
         * significant business events, or periodic status updates.</p>
         *
         * @return the message builder configured for INFO level
         */
        default Entry info() {
            return level(Level.INFO);
        }

        /**
         * Sets the log level to DEBUG and returns the message builder.
         *
         * <p>DEBUG level is used for detailed information useful during development
         * and debugging. Examples include variable values, method entry/exit,
         * and intermediate results.</p>
         *
         * @return the message builder configured for DEBUG level
         */
        default Entry debug() {
            return level(Level.DEBUG);
        }

        /**
         * Sets the log level to TRACE and returns the message builder.
         *
         * <p>TRACE level is used for the most detailed information, typically
         * including trace-level debugging that may generate significant output.
         * Examples include method parameter details, loop iterations, or
         * comprehensive state dumps.</p>
         *
         * @return the message builder configured for TRACE level
         */
        default Entry trace() {
            return level(Level.TRACE);
        }

        /**
         * Sets the log level to the specified value and returns the message builder.
         *
         * @param level the log level to set
         * @return the message builder configured for the specified level
         * @throws NullPointerException if level is null
         */
        Entry level(Level level);
    }
}
