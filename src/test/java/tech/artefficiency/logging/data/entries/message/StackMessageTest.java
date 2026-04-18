package tech.artefficiency.logging.data.entries.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.MessageFormatter;
import tech.artefficiency.logging.api.Level;
import tech.artefficiency.logging.api.StackMode;
import tech.artefficiency.logging.stubs.TestEntriesContext;
import tech.artefficiency.logging.stubs.TestEntry;
import tech.artefficiency.logging.stubs.TestStackHelper;
import tech.artefficiency.logging.tools.stack.StackHelper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import static tech.artefficiency.logging.data.entries.message.StackMessageTest.Data.*;
import static tech.artefficiency.logging.data.tools.StackUtils.stackOfSize;

public class StackMessageTest {

    interface Data {
        Level                    LEVEL              = Level.TRACE;
        String                   PATTERN            = "pattern {} {}";
        Object[]                 PARAMETERS         = new Object[]{7, "8"};
        String                   PARAMETERS_MESSAGE = "7, 8";
        String                   MESSAGE            = MessageFormatter.basicArrayFormat(PATTERN, PARAMETERS);
        StackWalker.StackFrame[] STACK              = stackOfSize(StackHelper.Default.LIMIT + 2);
    }

    TestEntriesContext context;
    TestEntry          parent;
    StackMessage       target;
    Throwable          thrown;

    @BeforeEach
    void initializeTest() {
        this.context = new TestEntriesContext();
        this.parent  = new TestEntry(LEVEL, context);

        StackMessage.STACK_GETTER = new TestStackHelper(STACK);
    }

    @Test
    void ctor_nullParent_throwsNullPointerException() {
        when:
        {
            thrown = catchThrowable(() -> new StackMessage(null, StackMode.FULL));
        }
        then:
        {
            assertThat(thrown)
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("parent");
        }
    }

    @Test
    void ctor_nullStackMode_initializesInstanceWithDefaultStackMode() {
        when:
        {
            target = new StackMessage(parent, null);
        }
        then:
        {
            assertThat(target.stackInfo().mode()).isEqualTo(context.configuration().entry().stack().stackMode());
        }
    }

    @Test
    void ctor_fullStackMode_initializesInstanceWithCorrectStackInfo() {
        when:
        {
            target = new StackMessage(parent, StackMode.FULL);
        }
        then:
        {
            var expected = StackHelper.toInfo(STACK).mode(StackMode.FULL);

            assertThat(target.stackInfo()).isEqualTo(expected);
        }
    }

    @Test
    void ctor_fairStackMode_initializesInstanceWithCorrectStackInfo() {
        when:
        {
            target = new StackMessage(parent, StackMode.FAIR);
        }
        then:
        {
            var expected = StackHelper.toInfo(STACK).mode(StackMode.FAIR);

            assertThat(target.stackInfo()).isEqualTo(expected);
        }
    }

    @Test
    void ctor_noStackMode_initializesInstanceWithCorrectStackInfo() {
        when:
        {
            target = new StackMessage(parent, StackMode.NONE);
        }
        then:
        {
            var expected = StackHelper.toInfo(STACK).mode(StackMode.NONE);

            assertThat(target.stackInfo()).isEqualTo(expected);
        }
    }

    @Test
    void add_noPatternNoParameters_leavesMessageUnset() {
        given:
        {
            target = new StackMessage(parent, StackMode.NONE);
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
            target = new StackMessage(parent, StackMode.NONE);
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
            target = new StackMessage(parent, StackMode.NONE);
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
            target = new StackMessage(parent, StackMode.NONE);
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
            target = new StackMessage(parent, StackMode.NONE);
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
