package tech.artefficiency.logging.data.entries.layer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.MessageFormatter;
import tech.artefficiency.logging.api.LayerApi;
import tech.artefficiency.logging.api.Level;
import tech.artefficiency.logging.data.entries.base.BaseEntry;
import tech.artefficiency.logging.data.entries.base.EntryBuilder;
import tech.artefficiency.logging.stubs.FieldData;
import tech.artefficiency.logging.stubs.TestEntriesContext;
import tech.artefficiency.logging.stubs.TestEntry;
import tech.artefficiency.logging.data.tools.EntryValidator;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static tech.artefficiency.logging.data.entries.layer.LayerTest.Data.*;
import static tech.artefficiency.logging.data.entries.layer.LayerTest.Data.DURATION;
import static tech.artefficiency.logging.tools.Cast.cast;

public class LayerTest {

    interface Data {
        Level     LEVEL            = Level.TRACE;
        String    NAME             = "name";
        String    LAYER            = "layer";
        int       DEPTH            = 4;
        Duration  DURATION         = Duration.ofSeconds(2);
        FieldData FIELD_DATA       = new FieldData("field #2", () -> "6", Objects::toString);
        String    PATTERN          = "pattern {} {}";
        Object[]  PARAMETERS       = new Object[]{7, "8"};
        String    MESSAGE          = MessageFormatter.basicArrayFormat(PATTERN, PARAMETERS);
        String    LAYER_PATTERN    = "pattern {}";
        Object[]  LAYER_PARAMETERS = new Object[]{Duration.ofSeconds(9)};
        String    LAYER_MESSAGE    = MessageFormatter.basicArrayFormat(LAYER_PATTERN, LAYER_PARAMETERS);
    }

    TestEntriesContext      context;
    EntryBuilder<TestEntry> parentBuilder;
    EntryValidator<Layer>   validator;
    TestEntry               parent;
    Throwable               thrown;
    Layer                   target;
    LayerApi.Reporter       reporter;

    @BeforeEach
    void initializeTest() {
        this.context       = new TestEntriesContext();
        this.parentBuilder = new EntryBuilder<>(context, TestEntry::new)
                .withLevel(LEVEL)
                .withName(NAME)
                .withDepth(DEPTH)
                .withDuration(DURATION)
                .withPattern(PATTERN)
                .withParameters(PARAMETERS)
                .withFields(FIELD_DATA);
        this.parent        = parentBuilder.build();
        this.validator     = new EntryValidator<Layer>()
                .on(BaseEntry::name).expecting(NAME)
                .on(BaseEntry::level).expecting(LEVEL)
                .on(BaseEntry::message).expecting(MESSAGE)
                .on(BaseEntry::fields).expecting(List.of(FIELD_DATA.asField()))
                .on(BaseEntry::depth).expecting(DEPTH)
                .on(BaseEntry::duration).expecting(DURATION);
    }

    @Test
    void ctor_nullParent_throwsArgumentNullException() {
        when:
        {
            thrown = catchThrowable(() -> new Layer(null, LAYER));
        }
        then:
        {
            assertThat(thrown)
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Test
    void ctor_nullName_initializesLayerWithParentName() {
        when:
        {
            target = new Layer(parent, null);
        }
        then:
        {
            assertThat(target.name()).isEqualTo(parent.name());
        }
    }

    @Test
    void ctor_nullNameParentWithoutName_initializesLayerWithDefaultName() {
        when:
        {
            parent = parentBuilder.withName(null).build();
            target = new Layer(parent, null);
        }
        then:
        {
            assertThat(target.name()).isEqualTo(context.configuration().entry().defaults().token().layerName());
        }
    }

    @Test
    void ctor_ownName_initializesLayerWithPassedName() {
        when:
        {
            target = new Layer(parent, LAYER);
        }
        then:
        {
            assertThat(target.name()).isEqualTo(LAYER);
        }
    }

    @Test
    void ctor_calculateDurationEnabled_storesTimestamp() {
        when:
        {
            target = new Layer(parent, LAYER);
        }
        then:
        {
            assertThat(target.nanoTimestamp()).isPositive();
        }
    }

    @Test
    void ctor_calculateDurationDisabled_doesNotStoreTimestamp() {
        given:
        {
            context.configuration()
                    .entry()
                    .layer()
                    .setCalculateDuration(false);
        }
        when:
        {
            target = new Layer(parent, LAYER);
        }
        then:
        {
            assertThat(target.nanoTimestamp()).isZero();
        }
    }

    @Test
    void ctor_default_initializesInstance() {
        given:
        {
            validator.on(Layer::name).expecting(LAYER);
        }
        when:
        {
            target = new Layer(parent, LAYER);
        }
        then:
        {
            validator.validate(target);
        }
    }

    @Test
    void start_noParentPatternAndParametersNoSelf_leavesPatternAndParametersUnset() {
        given:
        {
            parent = parentBuilder.withPattern(null).withParameters().build();
            target = new Layer(parent, LAYER);

            validator
                    .on(Layer::name).expecting(LAYER)
                    .on(Layer::message).expectingNull();
        }
        when:
        {
            target.start();
        }
        then:
        {
            validator.validate(target);
        }
    }

    @Test
    void start_noParentPatternAndParametersHasSelf_setsPatternAndParametersToSelf() {
        given:
        {
            parent = parentBuilder.withPattern(null).withParameters().build();
            target = new Layer(parent, LAYER);

            validator
                    .on(Layer::name).expecting(LAYER)
                    .on(Layer::message).expecting(LAYER_MESSAGE);
        }
        when:
        {
            target.start(LAYER_PATTERN, LAYER_PARAMETERS);
        }
        then:
        {
            validator.validate(target);
        }
    }

    @Test
    void start_hasParentPatternAndParametersNoSelf_leavesParentsPatternAndParameters() {
        given:
        {
            target = new Layer(parent, LAYER);

            validator
                    .on(Layer::name).expecting(LAYER)
                    .on(Layer::message).expecting(MESSAGE);
        }
        when:
        {
            target.start();
        }
        then:
        {
            validator.validate(target);
        }
    }

    @Test
    void start_hasParentPatternAndParametersAndHasSelf_setsPatternAndParametersToSelf() {
        given:
        {
            target = new Layer(parent, LAYER);

            validator
                    .on(Layer::name).expecting(LAYER)
                    .on(Layer::message).expecting(LAYER_MESSAGE);
        }
        when:
        {
            target.start(LAYER_PATTERN, LAYER_PARAMETERS);
        }
        then:
        {
            validator.validate(target);
        }
    }

    @Test
    void start_default_passesSelfToContext() {
        given:
        {
            target = new Layer(parent, LAYER);
        }
        when:
        {
            target.start();
        }
        then:
        {
            assertThat(context.acceptedEntries()).containsOnly(target);
        }
    }

    @Test
    void start_default_returnsCorrectlyInitializedReporter() {
        given:
        {
            target = new Layer(parent, LAYER);
        }
        when:
        {
            reporter = target.start();
        }
        then:
        {
            new EntryValidator<Layer.Reporter>()
                    .on(Layer.Reporter::name).expecting(LAYER)
                    .on(Layer.Reporter::level).expecting(LEVEL)
                    .on(Layer.Reporter::message).expecting(MESSAGE)
                    .on(Layer.Reporter::fields).expecting(List.of(FIELD_DATA.asField()))
                    .on(Layer.Reporter::depth).expecting(DEPTH)
                    .on(Layer.Reporter::duration).expectingNull()
                    .on(Layer.Reporter::isSkipped).expecting(false)
                    .validate(cast(reporter));
        }
    }
}
