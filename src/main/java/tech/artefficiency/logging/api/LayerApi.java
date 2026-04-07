package tech.artefficiency.logging.api;

import tech.artefficiency.logging.tools.stack.StackHelper;

public interface LayerApi {

    StackHelper STACK = new StackHelper().withKnown(NameSetter.class);

    interface Token {
        String RESULT_PATTERN = "%s";
    }

    interface NameSetter {

        default Starter methodLayer() {
            return layer(STACK.getFirstUnknownFrame().getMethodName());
        }

        Starter layer(String name);
    }

    interface Starter {

        default Reporter start() {
            return start(null, (Object[]) null);
        }

        default Reporter start(Object... parameters) {
            return start(null, parameters);
        }

        Reporter start(String pattern, Object... parameters);
    }

    interface Reporter extends AutoCloseable {

        default <R> R report(R result) {
            return report(null, result);
        }

        default <R> R reportError(R result) {
            return report(Level.ERROR, result);
        }

        default <R> R reportWarn(R result) {
            return report(Level.WARN, result);
        }

        default <R> R reportInfo(R result) {
            return report(Level.INFO, result);
        }

        default <R> R reportDebug(R result) {
            return report(Level.DEBUG, result);
        }

        default <R> R reportTrace(R result) {
            return report(Level.TRACE, result);
        }

        default <R> R report(Level level, R result) {
            report(level, Token.RESULT_PATTERN, result);
            return result;
        }

        default void report(String pattern, Object... args) {
            report(null, pattern, args);
        }

        default void reportError(String pattern, Object... args) {
            report(Level.ERROR, pattern, args);
        }

        default void reportWarn(String pattern, Object... args) {
            report(Level.WARN, pattern, args);
        }

        default void reportInfo(String pattern, Object... args) {
            report(Level.INFO, pattern, args);
        }

        default void reportDebug(String pattern, Object... args) {
            report(Level.DEBUG, pattern, args);
        }

        default void reportTrace(String pattern, Object... args) {
            report(Level.TRACE, pattern, args);
        }

        void report(Level level, String pattern, Object... args);

        void skip();

        @Override
        void close();
    }
}
