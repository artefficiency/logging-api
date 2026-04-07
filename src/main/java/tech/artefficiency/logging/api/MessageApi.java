package tech.artefficiency.logging.api;

public interface MessageApi {

    interface ExceptionFormatter extends DefaultAdder {

        default ExceptionFormatter noStack() {
            return stackMode(StackMode.NONE);
        }

        default ExceptionFormatter fullStack() {
            return stackMode(StackMode.FULL);
        }

        ExceptionFormatter stackMode(StackMode mode);

        ExceptionFormatter noMessage();

        ExceptionFormatter noClass();
    }

    interface StackFormatter {

        default DefaultAdder putStack() {
            return putStack(StackMode.FAIR);
        }

        DefaultAdder putStack(StackMode mode);
    }

    interface DefaultAdder extends Adder {

        default void add() {
            add(null);
        }
    }

    interface Adder {

        void add(String pattern, Object... parameters);
    }
}
