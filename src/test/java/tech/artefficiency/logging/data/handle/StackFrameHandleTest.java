package tech.artefficiency.logging.data.handle;

import org.junit.jupiter.api.Test;
import tech.artefficiency.logging.data.stubs.TestStackFrame;
import tech.artefficiency.logging.exceptions.ArgumentNullException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static tech.artefficiency.logging.data.handle.StackFrameHandleTest.Data.*;

public class StackFrameHandleTest {

    interface Data {
        String          CLASS_NAME        = "com.example.Service";
        String          METHOD_NAME       = "processOrder";
        String          ANOTHER_CLASS     = "com.example.Controller";
        String          ANOTHER_METHOD    = "handle";
        int             LINE_NUMBER       = 42;
        int             ANOTHER_LINE      = 15;
        TestStackFrame FRAME             = new TestStackFrame(CLASS_NAME, METHOD_NAME, LINE_NUMBER);
        TestStackFrame ANOTHER_FRAME     = new TestStackFrame(ANOTHER_CLASS, ANOTHER_METHOD, ANOTHER_LINE);
        TestStackFrame SAME_CLASS_FRAME  = new TestStackFrame(CLASS_NAME, METHOD_NAME, LINE_NUMBER);
    }

    StackFrameHandle target;
    StackFrameHandle another;
    Throwable        thrown;
    boolean          result;

    @Test
    void ctor_nullElement_throwsArgumentNullException() {
        when:
        {
            thrown = catchThrowable(() -> new StackFrameHandle(null));
        }
        then:
        {
            assertThat(thrown)
                    .isInstanceOf(ArgumentNullException.class)
                    .hasMessageContaining("element");
        }
    }

    @Test
    void ctor_validElement_initializesCorrectInstance() {
        when:
        {
            target = new StackFrameHandle(FRAME);
        }
        then:
        {
            assertThat(target.element()).isEqualTo(FRAME);
        }
    }

    @Test
    void className_returnsCorrectValue() {
        when:
        {
            target = new StackFrameHandle(FRAME);
        }
        then:
        {
            assertThat(target.className()).isEqualTo(CLASS_NAME);
        }
    }

    @Test
    void methodName_returnsCorrectValue() {
        when:
        {
            target = new StackFrameHandle(FRAME);
        }
        then:
        {
            assertThat(target.methodName()).isEqualTo(METHOD_NAME);
        }
    }

    @Test
    void lineNumber_returnsCorrectValue() {
        when:
        {
            target = new StackFrameHandle(FRAME);
        }
        then:
        {
            assertThat(target.lineNumber()).isEqualTo(LINE_NUMBER);
        }
    }

    @Test
    void equals_sameValues_returnsTrue() {
        given:
        {
            target  = new StackFrameHandle(FRAME);
            another = new StackFrameHandle(SAME_CLASS_FRAME);
        }
        when:
        {
            result = target.equals(another);
        }
        then:
        {
            assertThat(result).isTrue();
        }
    }

    @Test
    void equals_differentClassName_returnsFalse() {
        given:
        {
            target  = new StackFrameHandle(FRAME);
            another = new StackFrameHandle(ANOTHER_FRAME);
        }
        when:
        {
            result = target.equals(another);
        }
        then:
        {
            assertThat(result).isFalse();
        }
    }

    @Test
    void equals_differentMethodName_returnsFalse() {
        given:
        {
            target  = new StackFrameHandle(FRAME);
            another = new StackFrameHandle(ANOTHER_FRAME);
        }
        when:
        {
            result = target.equals(another);
        }
        then:
        {
            assertThat(result).isFalse();
        }
    }

    @Test
    void equals_differentLineNumber_returnsFalse() {
        given:
        {
            target  = new StackFrameHandle(FRAME);
            another = new StackFrameHandle(ANOTHER_FRAME);
        }
        when:
        {
            result = target.equals(another);
        }
        then:
        {
            assertThat(result).isFalse();
        }
    }

    @Test
    void equals_null_returnsFalse() {
        given:
        {
            target = new StackFrameHandle(FRAME);
            another = null;
        }
        when:
        {
            result = target.equals(another);
        }
        then:
        {
            assertThat(result).isFalse();
        }
    }

    @Test
    void equals_differentClass_returnsFalse() {
        given:
        {
            target = new StackFrameHandle(FRAME);
        }
        when:
        {
            result = target.equals("not a StackFrameHandle");
        }
        then:
        {
            assertThat(result).isFalse();
        }
    }

    @Test
    void hashCode_sameValues_sameHashCode() {
        given:
        {
            target  = new StackFrameHandle(FRAME);
            another = new StackFrameHandle(SAME_CLASS_FRAME);
        }
        when:
        {
            result = target.hashCode() == another.hashCode();
        }
        then:
        {
            assertThat(result).isTrue();
        }
    }

    @Test
    void hashCode_differentValues_differentHashCode() {
        given:
        {
            target  = new StackFrameHandle(FRAME);
            another = new StackFrameHandle(ANOTHER_FRAME);
        }
        when:
        {
            result = target.hashCode() != another.hashCode();
        }
        then:
        {
            assertThat(result).isTrue();
        }
    }
}
