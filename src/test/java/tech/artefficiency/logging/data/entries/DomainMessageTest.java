package tech.artefficiency.logging.data.entries;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.MessageFormatter;
import tech.artefficiency.logging.api.LayerApi;
import tech.artefficiency.logging.api.Level;
import tech.artefficiency.logging.api.Message;
import tech.artefficiency.logging.api.MessageApi;
import tech.artefficiency.logging.data.entries.base.BaseEntry;
import tech.artefficiency.logging.data.entries.message.ExceptionMessage;
import tech.artefficiency.logging.data.entries.message.StackMessage;
import tech.artefficiency.logging.data.exception.ExceptionInfo;
import tech.artefficiency.logging.data.fields.Field;
import tech.artefficiency.logging.data.stubs.TestEntriesContext;
import tech.artefficiency.logging.exceptions.ArgumentNullException;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static tech.artefficiency.logging.data.entries.DomainMessageTest.Data.*;

public class DomainMessageTest {

    interface DomainFields extends Message {

        DomainFields a(Integer value);

        DomainFields b(Supplier<String> value);
    }

    interface Data {
        String           NAME               = "layer";
        Level            LEVEL              = Level.TRACE;
        String           PATTERN            = "pattern {} {}";
        Object[]         PARAMETERS         = new Object[]{7, "8"};
        String           PARAMETERS_MESSAGE = "7, 8";
        String           MESSAGE            = MessageFormatter.basicArrayFormat(PATTERN, PARAMETERS);
        int              FIELD_A_VALUE      = 4325;
        Field            FIELD_A            = new Field("A", () -> FIELD_A_VALUE, null);
        String           FIELD_B_VALUE      = "value";
        Field            FIELD_B            = new Field("B", () -> FIELD_B_VALUE, null);
        int              DEPTH              = 21;
        RuntimeException EXCEPTION          = new RuntimeException("Exception");
    }

    TestEntriesContext          context;
    DomainMessage<DomainFields> target;
    DomainFields                proxy;
    Throwable                   thrown;
    Object                      nextStep;

    @BeforeEach
    void initializeTest() {
        this.context = new TestEntriesContext();
    }

    @Test
    void ctor_nullLevel_initializesInstanceWithDefaultLevel() {
        when:
        {
            target = new DomainMessage<>(null, context, DomainFields.class);
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
            thrown = catchThrowable(() -> new DomainMessage<>(LEVEL, null, DomainFields.class));
        }
        then:
        {
            assertThat(thrown)
                    .isInstanceOf(ArgumentNullException.class)
                    .hasMessageContaining("context");
        }
    }

    @Test
    void ctor_nullDomainClass_throwsArgumentNullException() {
        when:
        {
            thrown = catchThrowable(() -> new DomainMessage<>(LEVEL, context, null));
        }
        then:
        {
            assertThat(thrown)
                    .isInstanceOf(ArgumentNullException.class)
                    .hasMessageContaining("domainClass");
        }
    }

    @Test
    void field_nullSupplierValue_throwsArgumentNullException() {
        given:
        {
            target = new DomainMessage<>(LEVEL, context, DomainFields.class);
            proxy  = target.asProxy();
        }
        when:
        {
            proxy.b(null);
        }
        then:
        {
            validate(target.fields(), new Field(FIELD_B.name(), () -> null, null));
        }
    }

    @Test
    void field_nullObjectValue_setsValueNull() {
        given:
        {
            target = new DomainMessage<>(LEVEL, context, DomainFields.class);
            proxy  = target.asProxy();
        }
        when:
        {
            proxy.a(null);
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
            target = new DomainMessage<>(LEVEL, context, DomainFields.class);
            proxy  = target.asProxy();
        }
        when:
        {
            proxy.a(FIELD_A_VALUE);
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
            target = new DomainMessage<>(LEVEL, context, DomainFields.class);
            proxy  = target.asProxy();
        }
        when:
        {
            proxy.a(FIELD_A_VALUE).b(() -> FIELD_B_VALUE);
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
            target = new DomainMessage<>(LEVEL, context, DomainFields.class);
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
            target = new DomainMessage<>(LEVEL, context, DomainFields.class);
            proxy  = target.asProxy();
        }
        when:
        {
            nextStep = proxy.layer(NAME);
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
            target = new DomainMessage<>(LEVEL, context, DomainFields.class);
            proxy  = target.asProxy();
        }
        when:
        {
            nextStep = proxy.putStack();
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
            target = new DomainMessage<>(LEVEL, context, DomainFields.class);
            proxy  = target.asProxy();
        }
        when:
        {
            nextStep = proxy.exception(EXCEPTION);
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
            target = new DomainMessage<>(LEVEL, context, DomainFields.class);
            proxy  = target.asProxy();
        }
        when:
        {
            proxy.add(null);
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
            target = new DomainMessage<>(LEVEL, context, DomainFields.class);
            proxy  = target.asProxy();
        }
        when:
        {
            proxy.add(MESSAGE);
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
            target = new DomainMessage<>(LEVEL, context, DomainFields.class);
            proxy  = target.asProxy();
        }
        when:
        {
            proxy.add(null, PARAMETERS);
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
            target = new DomainMessage<>(LEVEL, context, DomainFields.class);
            proxy  = target.asProxy();
        }
        when:
        {
            proxy.add(PATTERN, PARAMETERS);
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
            target = new DomainMessage<>(LEVEL, context, DomainFields.class);
            proxy  = target.asProxy();
        }
        when:
        {
            proxy.add(PATTERN, PARAMETERS);
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
