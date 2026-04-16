package tech.artefficiency.logging.tools.stack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tech.artefficiency.logging.api.StackMode;
import tech.artefficiency.logging.data.stack.StackInfo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static tech.artefficiency.logging.data.tools.StackUtils.frameFor;
import static tech.artefficiency.logging.data.tools.StackUtils.stackOfSize;

public class StackHelperTest {

    @Nested
    class TopStackFrameResolution {

        @BeforeEach
        void initializeTest() {
            target = new StackHelper();
        }

        private StackHelper            target;
        private StackWalker.StackFrame expectedFrame;
        private StackWalker.StackFrame resultFrame;

        @Test
        void getFirstUnknown_default_returnsCurrentMethod() {
            given:
            {
                expectedFrame = frameFor(TopStackFrameResolution.class, "getFirstUnknown_default_returnsCurrentMethod");
            }
            when:
            {
                resultFrame = target.getFirstUnknownFrame();
            }
            then:
            {
                validate(expectedFrame, resultFrame);
            }
        }

        @Test
        void getFirstUnknown_shallow_returnsExpectedMethod() {
            given:
            {
                expectedFrame = frameFor(ShallowWrap.class, "get");
            }
            when:
            {
                resultFrame = new ShallowWrap().getShallow();
            }
            then:
            {
                validate(expectedFrame, resultFrame);
            }
        }

        @Test
        void getFirstUnknown_shallowWithKnownShallowWrap_returnsCurrentMethod() {
            given:
            {
                target.withKnown(ShallowWrap.class);
                expectedFrame = frameFor(TopStackFrameResolution.class, "getFirstUnknown_shallowWithKnownShallowWrap_returnsCurrentMethod");
            }
            when:
            {
                resultFrame = new ShallowWrap().getShallow();
            }
            then:
            {
                validate(expectedFrame, resultFrame);
            }
        }

        @Test
        void getFirstUnknown_deep_returnsExpectedMethod() {
            given:
            {
                expectedFrame = frameFor(ShallowWrap.class, "get");
            }
            when:
            {
                resultFrame = new ShallowWrap().getDeep();
            }
            then:
            {
                validate(expectedFrame, resultFrame);
            }
        }

        @Test
        void getFirstUnknown_deepWithKnownShallowWrap_returnsDeepWrapMethod() {
            given:
            {
                target.withKnown(ShallowWrap.class);
                expectedFrame = frameFor(ShallowWrap.DeepWrap.class, "getElement");
            }
            when:
            {
                resultFrame = new ShallowWrap().getDeep();
            }
            then:
            {
                validate(expectedFrame, resultFrame);
            }
        }

        @Test
        void getFirstUnknown_deepWithKnownShallowWrapAndDeepWrap_returnsCurrentMethod() {
            given:
            {
                target.withKnown(ShallowWrap.class).withKnown(ShallowWrap.DeepWrap.class);
                expectedFrame = frameFor(TopStackFrameResolution.class, "getFirstUnknown_deepWithKnownShallowWrapAndDeepWrap_returnsCurrentMethod");
            }
            when:
            {
                resultFrame = new ShallowWrap().getDeep();
            }
            then:
            {
                validate(expectedFrame, resultFrame);
            }
        }

        private final class ShallowWrap {

            private final DeepWrap deep = new DeepWrap();

            StackWalker.StackFrame getShallow() {
                return get();
            }

            private StackWalker.StackFrame get() {
                return target.getFirstUnknownFrame();
            }

            StackWalker.StackFrame getDeep() {
                return deep.getElement();
            }

            private final class DeepWrap {
                StackWalker.StackFrame getElement() {
                    return get();
                }
            }
        }
    }

    @Nested
    class TrimStackFunctionality {

        private StackWalker.StackFrame[] expectedStack;
        private StackWalker.StackFrame[] sourceStack;
        private StackWalker.StackFrame[] resultStack;

        @Test
        void trim_null_returnsEmptyStack() {
            given:
            {
                sourceStack   = null;
                expectedStack = stackOfSize(0);
            }
            when:
            {
                resultStack = StackHelper
                        .trim(sourceStack)
                        .mode(StackMode.FAIR)
                        .toArray(StackWalker.StackFrame[]::new);
            }
            then:
            {
                validate(expectedStack, resultStack);
            }
        }

        @Test
        void trim_notEmptyStackWithNONE_returnsEmptyStack() {
            given:
            {
                sourceStack    = stackOfSize(3);
                expectedStack  = stackOfSize(0);
            }
            when:
            {
                resultStack = StackHelper
                        .trim(sourceStack)
                        .mode(StackMode.NONE)
                        .toArray(StackWalker.StackFrame[]::new);
            }
            then:
            {
                validate(expectedStack, resultStack);
            }
        }

        @Test
        void trim_stackLessThenDefaultLimitWithFAIR_returnsSourceStack() {
            given:
            {
                sourceStack   = stackOfSize(StackHelper.Default.LIMIT - 1);
                expectedStack = sourceStack;
            }
            when:
            {
                resultStack = StackHelper
                        .trim(sourceStack)
                        .mode(StackMode.FAIR)
                        .toArray(StackWalker.StackFrame[]::new);
            }
            then:
            {
                validate(expectedStack, resultStack);
            }
        }

        @Test
        void trim_stackGreaterThenDefaultLimitWithFAIR_returnsTrimmedStack() {
            given:
            {
                sourceStack   = stackOfSize(StackHelper.Default.LIMIT + 1);
                expectedStack = stackOfSize(StackHelper.Default.LIMIT);
            }
            when:
            {
                resultStack = StackHelper
                        .trim(sourceStack)
                        .mode(StackMode.FAIR)
                        .toArray(StackWalker.StackFrame[]::new);
            }
            then:
            {
                validate(expectedStack, resultStack);
            }
        }

        @Test
        void trim_stackGreaterThenDefaultLimitWithFULL_returnsSourceStack() {
            given:
            {
                sourceStack   = stackOfSize(StackHelper.Default.LIMIT + 1);
                expectedStack = sourceStack;
            }
            when:
            {
                resultStack = StackHelper
                        .trim(sourceStack)
                        .mode(StackMode.FULL)
                        .toArray(StackWalker.StackFrame[]::new);
            }
            then:
            {
                validate(expectedStack, resultStack);
            }
        }
    }

    @Nested
    class ConvertStackToInfo {

        private StackWalker.StackFrame[] sourceStack;
        private StackInfo               expectedIfo;
        private StackInfo              resultIfo;

        @Test
        void toInfo_null_returnsNull() {
            when:
            {
                resultIfo = StackHelper
                        .toInfo((StackWalker.StackFrame[]) null)
                        .mode(StackMode.FAIR);
            }
            then:
            {
                assertThat(resultIfo).isNull();
            }
        }

        @Test
        void toInfo_notEmptyStackWithNULL_returnsNull() {
            given:
            {
                sourceStack = stackOfSize(3);
            }
            when:
            {
                resultIfo = StackHelper
                        .toInfo(sourceStack)
                        .mode(null);
            }
            then:
            {
                assertThat(resultIfo).isNull();
            }
        }

        @Test
        void toInfo_notEmptyStackWithNONE_returnsEmptyStack() {
            given:
            {
                sourceStack = stackOfSize(3);
                expectedIfo = new StackInfo(StackMode.NONE, stackOfSize(0), 3);
            }
            when:
            {
                resultIfo = StackHelper
                        .toInfo(sourceStack)
                        .mode(StackMode.NONE);
            }
            then:
            {
                assertThat(resultIfo).isEqualTo(expectedIfo);
            }
        }

        @Test
        void toInfo_stackLessThenDefaultLimitWithFAIR_returnsSourceStack() {
            given:
            {
                sourceStack = stackOfSize(StackHelper.Default.LIMIT - 1);
                expectedIfo = new StackInfo(StackMode.FAIR, sourceStack, sourceStack.length);
            }
            when:
            {
                resultIfo = StackHelper
                        .toInfo(sourceStack)
                        .mode(StackMode.FAIR);
            }
            then:
            {
                assertThat(resultIfo).isEqualTo(expectedIfo);
            }
        }

        @Test
        void toInfo_stackGreaterThenDefaultLimitWithFAIR_returnsTrimmedStack() {
            given:
            {
                sourceStack = stackOfSize(StackHelper.Default.LIMIT + 1);
                expectedIfo = new StackInfo(StackMode.FAIR, stackOfSize(StackHelper.Default.LIMIT), StackHelper.Default.LIMIT + 1);
            }
            when:
            {
                resultIfo = StackHelper
                        .toInfo(sourceStack)
                        .mode(StackMode.FAIR);
            }
            then:
            {
                assertThat(resultIfo).isEqualTo(expectedIfo);
            }
        }

        @Test
        void toInfo_stackGreaterThenDefaultLimitWithFULL_returnsSourceStack() {
            given:
            {
                sourceStack = stackOfSize(StackHelper.Default.LIMIT + 1);
                expectedIfo = new StackInfo(StackMode.FULL, sourceStack, sourceStack.length);
            }
            when:
            {
                resultIfo = StackHelper
                        .toInfo(sourceStack)
                        .mode(StackMode.FULL);
            }
            then:
            {
                assertThat(resultIfo).isEqualTo(expectedIfo);
            }
        }
    }

    private void validate(StackWalker.StackFrame[] expected, StackWalker.StackFrame[] result) {

        if (expected == result) {
            return;
        }

        if (expected != null && result != null) {

            if (expected.length != result.length) {
                fail("Invalid result length, expected %s but got %s".formatted(expected.length, result.length));
            }

            for (var i = 0; i < expected.length; i++) {
                if (!expected[i].equals(result[i])) {
                    fail("Invalid frame at position %s, expected %s but got %s".formatted(i, expected[i], result[i]));
                }
            }
        } else if (expected == null) {
            fail("Expected null but got %s elements".formatted(result.length));
        } else {
            fail("Expected %s elements but got null".formatted(expected.length));
        }
    }

    private void validate(StackWalker.StackFrame expected, StackWalker.StackFrame result) {
        assertThat(result.getClassName()).isEqualTo(expected.getClassName());
        assertThat(result.getMethodName()).isEqualTo(expected.getMethodName());
    }
}
