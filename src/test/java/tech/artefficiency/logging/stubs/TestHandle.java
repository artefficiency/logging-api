package tech.artefficiency.logging.stubs;

import tech.artefficiency.logging.data.handle.Handle;

public record TestHandle(String className, String methodName, int lineNumber) implements Handle {
}
