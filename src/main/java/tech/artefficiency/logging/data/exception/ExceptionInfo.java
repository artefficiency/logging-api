package tech.artefficiency.logging.data.exception;

import tech.artefficiency.logging.api.StackMode;
import tech.artefficiency.logging.data.stack.StackInfo;
import tech.artefficiency.logging.tools.stack.StackHelper;

import java.util.function.Consumer;
import java.util.function.Function;

public record ExceptionInfo(Class<?> type, String message, StackInfo stack, ExceptionInfo causedBy) {

    public static Builder of(Throwable exception) {
        return new Builder(exception);
    }

    public final static class Builder {

        private Function<Throwable, ? extends Class<?>> classExtractor   = this::extractClass;
        private Function<Throwable, String>             messageExtractor = this::extractMessage;
        private Function<Throwable, StackInfo>          stackExtractor   = x -> extractStack(x, StackMode.FAIR);

        private final Throwable exception;

        private Builder(Throwable exception) {
            this.exception = exception;
        }

        public Builder noClass() {
            return this.<Class<?>>resetHandlerAndContinue(x -> classExtractor = x);
        }

        public Builder stackMode(StackMode mode) {

            if (mode != null && mode != StackMode.NONE) {
                stackExtractor = x -> extractStack(x, mode);
            } else {
                stackExtractor = x -> null;
            }

            return this;
        }

        public Builder noStack() {
            return this.<StackInfo>resetHandlerAndContinue(x -> stackExtractor = x);
        }

        public Builder noMessage() {
            return this.<String>resetHandlerAndContinue(x -> messageExtractor = x);
        }

        public ExceptionInfo build() {
            return buildFor(exception);
        }

        private ExceptionInfo buildFor(Throwable throwable) {
            if (throwable != null) {
                return new ExceptionInfo(
                        classExtractor.apply(throwable),
                        messageExtractor.apply(throwable),
                        stackExtractor.apply(throwable),
                        buildFor(throwable.getCause())
                );
            } else {
                return null;
            }
        }

        private Class<? extends Throwable> extractClass(Throwable exception) {
            return exception.getClass();
        }

        private String extractMessage(Throwable exception) {
            return exception.getMessage();
        }

        private StackInfo extractStack(Throwable exception, StackMode mode) {
            return StackHelper.toInfo(exception.getStackTrace()).mode(mode);
        }

        private <T> Builder resetHandlerAndContinue(Consumer<Function<Throwable, T>> assigner) {

            if (assigner != null) {
                assigner.accept(x -> null);
            }

            return this;
        }
    }
}
