package tech.artefficiency.logging.data.stubs;

import com.google.common.base.Objects;
import org.apache.commons.lang3.NotImplementedException;

public class TestStackFrame  implements StackWalker.StackFrame {

    private final String className;
    private final String methodName;
    private final int    lineNumber;

    public TestStackFrame(String className, String methodName, int lineNumber) {
        this.className  = className;
        this.methodName = methodName;
        this.lineNumber = lineNumber;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public Class<?> getDeclaringClass() {
        throw new NotImplementedException("Test implementation");
    }

    @Override
    public int getByteCodeIndex() {
        throw new NotImplementedException("Test implementation");
    }

    @Override
    public String getFileName() {
        throw new NotImplementedException("Test implementation");
    }

    @Override
    public boolean isNativeMethod() {
        throw new NotImplementedException("Test implementation");
    }

    @Override
    public StackTraceElement toStackTraceElement() {
        throw new NotImplementedException("Test implementation");
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TestStackFrame that = (TestStackFrame) o;
        return lineNumber == that.lineNumber && Objects.equal(className, that.className) && Objects.equal(methodName, that.methodName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(className, methodName, lineNumber);
    }
}
