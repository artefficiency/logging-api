package tech.artefficiency.logging.data.stack;

import com.google.common.base.Objects;
import tech.artefficiency.logging.api.StackMode;

import java.util.Arrays;

public record StackInfo(StackMode mode, StackWalker.StackFrame[] elements, int size) {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StackInfo stackInfo = (StackInfo) o;
        return size == stackInfo.size && mode == stackInfo.mode && Arrays.equals(elements, stackInfo.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mode, elements, size);
    }
}
