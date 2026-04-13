package tech.artefficiency.logging.data.handle;

import com.google.common.base.Objects;
import tech.artefficiency.logging.exceptions.ArgumentNullException;

import java.util.Optional;

public record StackFrameHandle(StackWalker.StackFrame element) implements Handle {

    public StackFrameHandle {
        Optional.ofNullable(element)
                .orElseThrow(() -> new ArgumentNullException("element"));
    }

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
