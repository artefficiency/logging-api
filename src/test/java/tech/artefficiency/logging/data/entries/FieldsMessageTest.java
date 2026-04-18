package tech.artefficiency.logging.data.entries;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.MessageFormatter;
import tech.artefficiency.logging.api.LayerApi;
import tech.artefficiency.logging.api.Level;
import tech.artefficiency.logging.api.MessageApi;
import tech.artefficiency.logging.data.entries.base.BaseEntry;
import tech.artefficiency.logging.data.entries.message.ExceptionMessage;
import tech.artefficiency.logging.data.entries.message.StackMessage;
import tech.artefficiency.logging.data.exception.ExceptionInfo;
import tech.artefficiency.logging.data.fields.Field;
import tech.artefficiency.logging.stubs.TestEntriesContext;
import tech.artefficiency.logging.exceptions.ArgumentNullException;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static tech.artefficiency.logging.data.entries.FieldsMessageTest.Data.*;

public class FieldsMessageTest {

    interface Data {
        String           NAME               = "layer";
        Level            LEVEL              = Level.TRACE;
        String           PATTERN            = "pattern {} {}";
        Object[]         PARAMETERS         = new Object[]{7, "8"};
        String           PARAMETERS_MESSAGE = "7, 8";
        String           MESSAGE            = MessageFormatter.basicArrayFormat(PATTERN, PARAMETERS);
        Field            FIELD_A            = new Field("a", () -> 4325, null);
        Field            FIELD_B            = new Field("b", () -> "value", null);
        int              DEPTH              = 21;
        RuntimeException EXCEPTION          = new RuntimeException("Exception");
    }

    TestEntriesContext context;
    FieldsMessage      target;
    Throwable          thrown;
    Object             nextStep;

    @BeforeEach
    void initializeTest() {
        this.context = new TestEntriesContext();
    }

    @Test
    void ctor_nullLevel_initializesInstanceWithDefaultLevel() {
        when:
        {
            target = new FieldsMessage(null, context);
        }
        then:
        {
            assertThat(target.level()).isEqualTo(context.configuration().entry().defaults().level());
        }
    }

    @Test
    void ctor_nullContext_throwsArgumentNullException() {
        when:
        {
            thrown = catchThrowable(() -> new FieldsMessage(LEVEL, null));
        }
        then:
        {
            assertThat(thrown)
                    .isInstanceOf(ArgumentNullException.class)
                    .hasMessageContaining("context");
        }
    }

    @Test
    void field_nullName_throwsArgumentNullException() {
        given:
        {
            target = new FieldsMessage(null, context);
        }
        when:
        {
            thrown = catchThrowable(() -> target.field(null).set(FIELD_A.value()));
        }
        then:
        {
            assertThat(thrown)
                    .isInstanceOf(ArgumentNullException.class)
                    .hasMessageContaining("name");
        }
    }

    @Test
    void field_nullSupplierValue_throwsArgumentNullException() {
        given:
        {
            target = new FieldsMessage(null, context);
        }
        when:
        {
            thrown = catchThrowable(() -> target.field(FIELD_A.name()).set((Supplier<Object>) null));
        }
        then:
        {
            assertThat(thrown)
                    .isInstanceOf(ArgumentNullException.class)
                    .hasMessageContaining("value");
        }
    }

    @Test
    void field_nullObjectValue_setsValueNull() {
        given:
        {
            target = new FieldsMessage(null, context);
        }
        when:
        {
            target.field(FIELD_A.name()).set((Object) null);
        }
        then:
        {
            validate(target.fields(), new Field(FIELD_A.name(), () -> null, null));
        }
    }

    @Test
    void field_default_addsField() {
        given:
        {
            target = new FieldsMessage(null, context);
        }
        when:
        {
            target.field(FIELD_A.name()).set(FIELD_A.value());
        }
        then:
        {
            validate(target.fields(), FIELD_A);
        }
    }

    @Test
    void field_coupleOfFields_addsFields() {
        given:
        {
            target = new FieldsMessage(null, context);
        }
        when:
        {
            target.field(FIELD_A.name()).set(FIELD_A.value())
                    .field(FIELD_B.name()).set(FIELD_B.value());
        }
        then:
        {
            validate(target.fields(), FIELD_A, FIELD_B);
        }
    }

    @Test
    void setDepth_validValue_setsDepth() {
        given:
        {
            target = new FieldsMessage(null, context);
        }
        when:
        {
            target.setDepth(DEPTH);
        }
        then:
        {
            assertThat(target.depth()).isEqualTo(DEPTH);
        }
    }

    @Test
    void layer_default_returnsCorrectInstance() {
        given:
        {
            target = new FieldsMessage(null, context);
        }
        when:
        {
            nextStep = target.layer(NAME);
        }
        then:
        {
            assertThat(nextStep).isInstanceOf(LayerApi.Starter.class);
            assertThat(((BaseEntry) nextStep).name()).isEqualTo(NAME);
        }
    }

    @Test
    void putStack_default_returnsCorrectInstance() {
        given:
        {
            target = new FieldsMessage(null, context);
        }
        when:
        {
            nextStep = target.putStack();
        }
        then:
        {
            assertThat(nextStep).isInstanceOf(MessageApi.DefaultAdder.class);
            assertThat(((StackMessage) nextStep).stackInfo().mode()).isEqualTo(context.configuration().entry().stack().stackMode());
        }
    }

    @Test
    void exception_default_returnsCorrectInstance() {
        given:
        {
            target = new FieldsMessage(null, context);
        }
        when:
        {
            nextStep = target.exception(EXCEPTION);
        }
        then:
        {
            assertThat(nextStep).isInstanceOf(MessageApi.ExceptionFormatter.class);
            assertThat(((ExceptionMessage) nextStep).exceptionInfo()).isEqualTo(ExceptionInfo.of(EXCEPTION).build());
        }
    }

    @Test
    void add_noPatternNoParameters_leavesMessageUnset() {
        given:
        {
            target = new FieldsMessage(null, context);
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
            target = new FieldsMessage(null, context);
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
            target = new FieldsMessage(null, context);
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
            target = new FieldsMessage(null, context);
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
            target = new FieldsMessage(null, context);
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

    void validate(Iterable<Field> actual, Field... expected) {
        var set = Sets.newHashSet(expected);

        for (var field : actual) {
            assertThat(set.remove(field)).isTrue();
        }

        assertThat(set.isEmpty()).isTrue();
    }
}
