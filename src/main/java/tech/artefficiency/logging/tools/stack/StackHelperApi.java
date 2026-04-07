package tech.artefficiency.logging.tools.stack;

import tech.artefficiency.logging.api.StackMode;

public interface StackHelperApi {

    StackHelperApi withKnown(Class<?> knownClass);

    StackWalker.StackFrame getFirstUnknownFrame();

    StackWalker.StackFrame[] getUnknown();

    interface ModeSetter<R> {
        R mode(StackMode mode);
    }
}
