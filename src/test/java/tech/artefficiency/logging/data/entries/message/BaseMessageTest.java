package tech.artefficiency.logging.data.entries.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.MessageFormatter;
import tech.artefficiency.logging.api.LayerApi;
import tech.artefficiency.logging.api.Level;
import tech.artefficiency.logging.api.MessageApi;
import tech.artefficiency.logging.api.StackMode;
import tech.artefficiency.logging.data.entries.base.BaseEntry;
import tech.artefficiency.logging.data.entries.layer.Layer;
import tech.artefficiency.logging.stubs.TestEntriesContext;
import tech.artefficiency.logging.stubs.TestEntry;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.artefficiency.logging.data.entries.message.BaseMessageTest.Data.*;

public class BaseMessageTest {

    interface Data {
        Level            LEVEL              = Level.TRACE;
        String           NAME               = "name";
        String           PATTERN            = "pattern {} {}";
        Object[]         PARAMETERS         = new Object[]{7, "8"};
        String           PARAMETERS_MESSAGE = "7, 8";
        String           MESSAGE            = MessageFormatter.basicArrayFormat(PATTERN, PARAMETERS);
        RuntimeException EXCEPTION          = new RuntimeException("test exception");
    }

    TestEntriesContext context;
    TestEntry          parent;
    TestMessage        target;

    MessageApi.ExceptionFormatter exceptionMessage;
    MessageApi.DefaultAdder       stackMessage;
    LayerApi.Starter              layer;

    @BeforeEach
    void initializeTest() {
        this.context = new TestEntriesContext();
        this.parent  = new TestEntry(LEVEL, context);
    }

    @Test
    void add_noPatternNoParameters_leavesMessageUnset() {
        given:
        {
            target = new TestMessage(parent);
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
            target = new TestMessage(parent);
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
            target = new TestMessage(parent);
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
            target = new TestMessage(parent);
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
            target = new TestMessage(parent);
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

    @Test
    void exception_nullException_returnsExceptionFormatterWithNullExceptionInfo() {
        given:
        {
            target = new TestMessage(parent);
        }
        when:
        {
            exceptionMessage = target.exception(null);
        }
        then:
        {
            assertThat(((ExceptionMessage) exceptionMessage).exceptionInfo()).isNull();
        }
    }

    @Test
    void exception_validException_returnsExceptionFormatterWithCorrectExceptionInfo() {
        given:
        {
            target = new TestMessage(parent);
        }
        when:
        {
            exceptionMessage = target.exception(EXCEPTION);
        }
        then:
        {
            assertThat(((ExceptionMessage) exceptionMessage).exceptionInfo().type()).isEqualTo(RuntimeException.class);
            assertThat(((ExceptionMessage) exceptionMessage).exceptionInfo().message()).isEqualTo(EXCEPTION.getMessage());
        }
    }

    @Test
    void putStack_nullMode_returnsStackMessageWithDefaultStackMode() {
        given:
        {
            target = new TestMessage(parent);
        }
        when:
        {
            stackMessage = target.putStack(null);
        }
        then:
        {
            assertThat(((StackMessage) stackMessage).stackInfo().mode()).isEqualTo(context.configuration().entry().stack().stackMode());
        }
    }

    @Test
    void putStack_validMode_returnsStackMessageWithCorrectStackMode() {
        given:
        {
            target = new TestMessage(parent);
        }
        when:
        {
            stackMessage = target.putStack(StackMode.NONE);
        }
        then:
        {
            assertThat(((StackMessage) stackMessage).stackInfo().mode()).isEqualTo(StackMode.NONE);
        }
    }

    @Test
    void layer_nullName_returnsLayerWithDefaultName() {
        given:
        {
            target = new TestMessage(parent);
        }
        when:
        {
            layer = target.layer(null);
        }
        then:
        {
            assertThat(((Layer) layer).name()).isEqualTo(context.configuration().entry().defaults().token().layerName());
        }
    }

    @Test
    void layer_validName_returnsLayerWithCorrectName() {
        given:
        {
            target = new TestMessage(parent);
        }
        when:
        {
            layer = target.layer(NAME);
        }
        then:
        {
            assertThat(((Layer) layer).name()).isEqualTo(NAME);
        }
    }

    public static final class TestMessage extends BaseMessage<TestMessage> {

        public TestMessage(BaseEntry parent) {
            super(parent);
        }
    }
}
