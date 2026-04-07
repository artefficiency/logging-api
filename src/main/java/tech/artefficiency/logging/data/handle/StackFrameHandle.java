package tech.artefficiency.logging.data.handle;

import com.google.common.base.Objects;

public record StackFrameHandle(StackWalker.StackFrame element) implements Handle {

    @Override
    public String className() {
        return element.getClassName();
    }

    @Override
    public String methodName() {
        return element.getMethodName();
    }

    @Override
    public int lineNumber() {
        return element.getLineNumber();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StackFrameHandle that = (StackFrameHandle) o;
        return Objects.equal(className(), that.className()) &&
               Objects.equal(methodName(), that.methodName()) &&
               lineNumber() == that.lineNumber();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(className(), methodName(), lineNumber());
    }
}
