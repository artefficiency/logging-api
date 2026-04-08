package tech.artefficiency.logging.data.entries.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.MessageFormatter;
import tech.artefficiency.logging.api.Level;
import tech.artefficiency.logging.api.StackMode;
import tech.artefficiency.logging.data.exception.ExceptionInfo;
import tech.artefficiency.logging.data.stubs.TestEntriesContext;
import tech.artefficiency.logging.data.stubs.TestEntry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static tech.artefficiency.logging.data.entries.message.ExceptionMessageTest.Data.*;

public class ExceptionMessageTest {

    interface Data {
        Level            LEVEL              = Level.TRACE;
        RuntimeException CAUSE              = new RuntimeException("Cause");
        String           PATTERN            = "pattern {} {}";
        Object[]         PARAMETERS         = new Object[]{7, "8"};
        String           PARAMETERS_MESSAGE = MessageFormatter.basicArrayFormat("{},{}", PARAMETERS);
        String           MESSAGE            = MessageFormatter.basicArrayFormat(PATTERN, PARAMETERS);
        RuntimeException EXCEPTION          = new RuntimeException("Exception", CAUSE);
    }

    TestEntriesContext context;
    TestEntry          parent;
    ExceptionMessage   target;
    Throwable          thrown;

    @BeforeEach
    void initializeTest() {
        this.context = new TestEntriesContext();
        this.parent  = new TestEntry(LEVEL, context);
    }

    @Test
    void ctor_nullParent_throwsNullPointerException() {
        when:
        {
            thrown = catchThrowable(() -> new ExceptionMessage(null, EXCEPTION));
        }
        then:
        {
            assertThat(thrown)
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("parent");
        }
    }

    @Test
    void ctor_nullException_initializesInstanceWithNullException() {
        when:
        {
            target = new ExceptionMessage(parent, null);
        }
        then:
        {
            assertThat(target.exceptionInfo()).isNull();
        }
    }

    @Test
    void ctor_validException_initializesInstanceWithDefaultExceptionInfo() {
        when:
        {
            target = new ExceptionMessage(parent, EXCEPTION);
        }
        then:
        {
            var expected = ExceptionInfo.of(EXCEPTION)
                    .build();

            assertThat(target.exceptionInfo()).isEqualTo(expected);
        }
    }

    @Test
    void noStack_default_configuresExceptionInfo() {
        given:
        {
            target = new ExceptionMessage(parent, EXCEPTION);
        }
        when:
        {
            target.noStack();
        }
        then:
        {
            var expected = ExceptionInfo.of(EXCEPTION)
                    .noStack()
                    .build();

            assertThat(target.exceptionInfo()).isEqualTo(expected);
        }
    }

    @Test
    void stackMode_set_configuresExceptionInfo() {
        given:
        {
            target = new ExceptionMessage(parent, EXCEPTION);
        }
        when:
        {
            target.stackMode(StackMode.FULL);
        }
        then:
        {
            var expected = ExceptionInfo.of(EXCEPTION)
                    .stackMode(StackMode.FULL)
                    .build();

            assertThat(target.exceptionInfo()).isEqualTo(expected);
        }
    }

    @Test
    void noClass_default_configuresExceptionInfo() {
        given:
        {
            target = new ExceptionMessage(parent, EXCEPTION);
        }
        when:
        {
            target.noClass();
        }
        then:
        {
            var expected = ExceptionInfo.of(EXCEPTION)
                    .noClass()
                    .build();

            assertThat(target.exceptionInfo()).isEqualTo(expected);
        }
    }

    @Test
    void noMessage_default_configuresExceptionInfo() {
        given:
        {
            target = new ExceptionMessage(parent, EXCEPTION);
        }
        when:
        {
            target.noMessage();
        }
        then:
        {
            var expected = ExceptionInfo.of(EXCEPTION)
                    .noMessage()
                    .build();

            assertThat(target.exceptionInfo()).isEqualTo(expected);
        }
    }

    @Test
    void add_noPatternNoParameters_leavesMessageUnset() {
        given:
        {
            target = new ExceptionMessage(parent, EXCEPTION);
        }
        when:
        {
            target.add(null);
        }
        then:
        {
            assertThat(target.message()).isNull();
        }
    }

    @Test
    void add_withPatternNoParameters_setsMessageToMessage() {
        given:
        {
            target = new ExceptionMessage(parent, EXCEPTION);
        }
        when:
        {
            target.add(MESSAGE);
        }
        then:
        {
            assertThat(target.message()).isEqualTo(MESSAGE);
        }
    }

    @Test
    void add_noPatternWithParameters_setsMessageToJustParametersMessage() {
        given:
        {
            target = new ExceptionMessage(parent, EXCEPTION);
        }
        when:
        {
            target.add(null, PARAMETERS);
        }
        then:
        {
            assertThat(target.message()).isEqualTo(PARAMETERS_MESSAGE);
        }
    }

    @Test
    void add_withPatternWithParameters_setsMessageToMessage() {
        given:
        {
            target = new ExceptionMessage(parent, EXCEPTION);
        }
        when:
        {
            target.add(PATTERN, PARAMETERS);
        }
        then:
        {
            assertThat(target.message()).isEqualTo(MESSAGE);
        }
    }

    @Test
    void add_default_passesMessageToContext() {
        given:
        {
            target = new ExceptionMessage(parent, EXCEPTION);
        }
        when:
        {
            target.add(PATTERN, PARAMETERS);
        }
        then:
        {
            assertThat(context.acceptedEntries()).containsOnly(target);
        }
    }
}
