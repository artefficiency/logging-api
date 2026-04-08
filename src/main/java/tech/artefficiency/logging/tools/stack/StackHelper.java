package tech.artefficiency.logging.tools.stack;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.NotImplementedException;
import tech.artefficiency.logging.api.StackMode;
import tech.artefficiency.logging.data.stack.StackInfo;
import tech.artefficiency.logging.tools.Builder;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public class StackHelper extends Builder<StackHelper> implements StackHelperApi {

    public static class Default {
        public static final int LIMIT = 5;
    }

    private final Set<Class<?>> known = Sets.newHashSet(StackHelper.class);

    public StackHelper withKnown(Class<?> knownClass) {
        return setAndContinue(knownClass, this.known::add);
    }

    public StackTraceElement getFirstUnknown() {
        return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(s -> s.filter(x -> !known.contains(x.getDeclaringClass())).findFirst()
                        .map(StackWalker.StackFrame::toStackTraceElement)
                        .orElse(null));
    }

    public StackWalker.StackFrame getFirstUnknownFrame() {
        return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(s -> s.filter(x -> !known.contains(x.getDeclaringClass())).findFirst()
                        .orElse(null));
    }

    public StackWalker.StackFrame[] getUnknown() {
        return get(stream -> stream.map(StackTraceElementAdapter::new).toArray(StackWalker.StackFrame[]::new));
    }

    private <T> T get(Function<Stream<StackTraceElement>, T> extractor) {
        return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(s -> extractor.apply(s
                                                   .filter(x -> !known.contains(x.getDeclaringClass()))
                                                   .map(StackWalker.StackFrame::toStackTraceElement)));
    }

    public static StackWalker.StackFrame[] asFrames(StackTraceElement[] elements) {
        return Optional.ofNullable(elements)
                .map(e ->
                             Arrays.stream(e)
                                     .map(StackTraceElementAdapter::new)
                                     .toArray(StackWalker.StackFrame[]::new))
                .orElse(null);
    }

    public static ModeSetter<StackInfo> toInfo(StackTraceElement[] stack) {
        return toInfo(asFrames(stack));
    }

    public static ModeSetter<StackInfo> toInfo(StackWalker.StackFrame[] stack) {
        return mode -> toInfo(stack, mode);
    }

    private static StackInfo toInfo(StackWalker.StackFrame[] frames, StackMode mode) {

        if (frames == null || mode == null) {
            return null;
        }

        var elements = trimStack(frames, mode)
                .toArray(StackWalker.StackFrame[]::new);

        return new StackInfo(mode, elements, frames.length);
    }

    public static ModeSetter<Stream<StackWalker.StackFrame>> trim(StackWalker.StackFrame[] stack) {
        return mode -> trimStack(stack, mode);
    }

    private static Stream<StackWalker.StackFrame> trimStack(StackWalker.StackFrame[] frames, StackMode mode) {

        var result = Stream.<StackWalker.StackFrame>of();

        if (frames != null &&
            mode != null &&
            mode != StackMode.NONE) {

            result = Arrays.stream(frames);

            if (mode == StackMode.FAIR) {
                result = result.limit(Default.LIMIT);
            }
        }

        return result;
    }

    private record StackTraceElementAdapter(StackTraceElement element) implements StackWalker.StackFrame {

        @Override
        public String getClassName() {
            return element.getClassName();
        }

        @Override
        public String getMethodName() {
            return element.getMethodName();
        }

        @Override
        public Class<?> getDeclaringClass() {
            throw new NotImplementedException("Not implemented for wrapper class");
        }

        @Override
        public int getByteCodeIndex() {
            return 0;
        }

        @Override
        public String getFileName() {
            throw new NotImplementedException("Not implemented for wrapper class");
        }

        @Override
        public int getLineNumber() {
            return element.getLineNumber();
        }

        @Override
        public boolean isNativeMethod() {
            return element.isNativeMethod();
        }

        @Override
        public StackTraceElement toStackTraceElement() {
            return element;
        }

        @Override
        public String toString() {
            return element.toString();
        }
    }
}