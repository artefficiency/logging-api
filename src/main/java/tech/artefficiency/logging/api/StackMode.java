package tech.artefficiency.logging.api;

/**
 * Defines how stack traces are captured and included in log entries.
 *
 * <p>Stack modes control the amount of stack trace information captured when
 * logging exceptions or explicitly requesting stack traces. Different modes
 * provide different trade-offs between information detail and log volume.</p>
 *
 * <h2>Mode Comparison</h2>
 * <table>
 *   <tr>
 *     <th>Mode</th>
 *     <th>Stack Frames</th>
 *     <th>Use Case</th>
 *   </tr>
 *   <tr>
 *     <td>{@link #NONE}</td>
 *     <td>None</td>
 *     <td>Minimal log size, exception message sufficient</td>
 *   </tr>
 *   <tr>
 *     <td>{@link #FAIR}</td>
 *     <td>Reasonable depth</td>
 *     <td>Balanced detail and size, default recommendation</td>
 *   </tr>
 *   <tr>
 *     <td>{@link #FULL}</td>
 *     <td>Complete stack</td>
 *     <td>Maximum detail, debugging production issues</td>
 *   </tr>
 * </table>
 *
 * <h2>Usage Example</h2>
 *
 * <h3>Exception Logging</h3>
 * <pre>{@code
 * try {
 *     riskyOperation();
 * } catch (Exception e) {
 *     // Default (FAIR) stack trace
 *     log.error().exception(e).add("Operation failed");
 *
 *     // No stack trace
 *     log.warn().exception(e).noStack().add("Minor issue");
 *
 *     // Full stack trace
 *     log.error().exception(e).fullStack().add("Critical error");
 * }
 * }</pre>
 *
 * <h3>Explicit Stack Traces</h3>
 * <pre>{@code
 * // Add stack trace to any log entry
 * log.info().putStack().add("Checkpoint reached");
 * log.debug().putStack(StackMode.FULL).add("Detailed checkpoint");
 * log.trace().putStack(StackMode.NONE).add("Simple checkpoint");
 * }</pre>
 *
 * <h2>Performance Considerations</h2>
 *
 * <p>Stack trace capture can be expensive, especially in high-throughput
 * scenarios. Consider:</p>
 * <ul>
 *   <li>Use {@link #NONE} in production for non-critical paths</li>
 *   <li>Use {@link #FAIR} as the default for most applications</li>
 *   <li>Use {@link #FULL} selectively for debugging specific issues</li>
 * </ul>
 *
 * @see Message#exception(Throwable)
 * @see MessageApi.StackFormatter
 */
public enum StackMode {

    /**
     * No stack trace is captured or included.
     *
     * <p>This mode produces the smallest log entries but provides no
     * information about where the exception or log entry originated.</p>
     *
     * <p><strong>Best for:</strong></p>
     * <ul>
     *   <li>High-throughput production logging</li>
     *   <li>Non-critical warnings</li>
     *   <li>When exception message is self-explanatory</li>
     *   <li>Reducing log storage costs</li>
     * </ul>
     *
     * <p>Example:</p>
     * <pre>{@code
     * // Logs:
     * //    09:13:20.637 [main] Configuration warning: key not found
     * //    Exception: class org.exceptions.NotFoundException:key not found
     * // No stack trace included
     * log.warn().exception(e).noStack().add("Configuration warning: {}", e.getMessage());
     * }</pre>
     *
     * @see MessageApi.ExceptionFormatter#noStack()
     */
    NONE,

    /**
     * A fair/reasonable amount of stack trace is captured.
     *
     * <p>This is the recommended default mode that balances detail with
     * log volume. It captures enough stack frames to understand the
     * call hierarchy while avoiding excessive output.</p>
     *
     * <p><strong>Best for:</strong></p>
     * <ul>
     *   <li>Default exception logging</li>
     *   <li>General-purpose debugging</li>
     *   <li>Production error tracking</li>
     *   <li>When you want helpful context without verbosity</li>
     * </ul>
     *
     * <p>Example output:</p>
     * <pre>{@code
     * // Example FAIR stack trace (typically 5 frames):
     * //   at com.example.Service.processOrder(Service.java:45)
     * //   at com.example.Controller.handleOrder(Controller.java:23)
     * //   at com.example.Api.orderEndpoint(Api.java:67)
     * //   ...7 more
     * log.error().exception(e).stackMode(StackMode.FAIR).add("Order failed");
     * }</pre>
     *
     * @see MessageApi.ExceptionFormatter#stackMode(StackMode)
     */
    FAIR,

    /**
     * The complete stack trace is captured.
     *
     * <p>This mode captures the entire call stack from the exception
     * origin to the current thread, including all frames. This provides
     * maximum debugging information but can produce very large log entries.</p>
     *
     * <p><strong>Best for:</strong></p>
     * <ul>
     *   <li>Debugging complex call chains</li>
     *   <li>Analyzing recursive or deeply nested calls</li>
     *   <li>Investigating production issues where full context is needed</li>
     *   <li>Capturing lambda/closure-heavy call stacks</li>
     * </ul>
     *
     * <p><strong>Caution:</strong> In applications with deep call stacks
     * or many lambda expressions, this can produce thousands of frames
     * and significantly increase log volume.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     * // Capture entire stack for complex debugging
     * log.error().exception(e).fullStack().add("Critical error - full analysis needed");
     * }</pre>
     *
     * @see MessageApi.ExceptionFormatter#fullStack()
     */
    FULL
}
