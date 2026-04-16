package tech.artefficiency.logging.data.entries.layer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.MessageFormatter;
import tech.artefficiency.logging.api.LayerApi;
import tech.artefficiency.logging.api.Level;
import tech.artefficiency.logging.data.entries.FieldsMessage;
import tech.artefficiency.logging.data.entries.base.BaseEntry;
import tech.artefficiency.logging.data.entries.base.EntryBuilder;
import tech.artefficiency.logging.data.stubs.FieldData;
import tech.artefficiency.logging.data.stubs.TestEntriesContext;
import tech.artefficiency.logging.data.stubs.TestEntry;
import tech.artefficiency.logging.data.tools.EntryValidator;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.artefficiency.logging.data.entries.layer.LayerReporterTest.Data.*;
import static tech.artefficiency.logging.data.entries.layer.LayerTest.Data.DEPTH;
import static tech.artefficiency.logging.data.entries.layer.LayerTest.Data.FIELD_DATA;
import static tech.artefficiency.logging.data.entries.layer.LayerTest.Data.LAYER;
import static tech.artefficiency.logging.data.entries.layer.LayerTest.Data.LEVEL;
import static tech.artefficiency.logging.data.entries.layer.LayerTest.Data.MESSAGE;
import static tech.artefficiency.logging.data.entries.layer.LayerTest.Data.PARAMETERS;
import static tech.artefficiency.logging.data.entries.layer.LayerTest.Data.PATTERN;
import static tech.artefficiency.logging.tools.Cast.cast;

public class LayerReporterTest {

    interface Data {
        Level     LEVEL                   = Level.TRACE;
        String    NAME                    = "name";
        String    LAYER                   = "layer";
        int       DEPTH                   = 4;
        Duration  DURATION                = Duration.ofSeconds(2);
        FieldData FIELD_DATA              = new FieldData("field #2", () -> "6", Objects::toString);
        String    PATTERN                 = "pattern {} {}";
        Object[]  PARAMETERS              = new Object[]{7, "8"};
        String    MESSAGE                 = MessageFormatter.basicArrayFormat(PATTERN, PARAMETERS);
        Level     REPORTER_LEVEL          = Level.WARN;
        String    REPORTER_PATTERN        = "pattern {}";
        Object[]  REPORTER_PARAMETERS     = new Object[]{Duration.ofSeconds(9)};
        String    REPORTER_MESSAGE        = MessageFormatter.basicArrayFormat(REPORTER_PATTERN, REPORTER_PARAMETERS);
        String    JUST_MESSAGE            = "Just message";
        Object[]  JUST_PARAMETERS         = new Object[]{true, false};
        String    JUST_PARAMETERS_MESSAGE = "true, false";
    }

    TestEntriesContext             context;
    EntryBuilder<TestEntry>        parentBuilder;
    EntryValidator<Layer.Reporter> validator;
    TestEntry                      parent;
    Layer                          layer;
    LayerApi.Reporter              target;
    FieldsMessage                  flattened;

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
        this.layer         = new Layer(parent, LAYER);
        this.validator     = new EntryValidator<Layer.Reporter>()
                .on(Layer.Reporter::name).expecting(LAYER)
                .on(Layer.Reporter::level).expecting(LEVEL)
                .on(Layer.Reporter::message).expecting(MESSAGE)
                .on(Layer.Reporter::fields).expecting(List.of(FIELD_DATA.asField()))
                .on(Layer.Reporter::depth).expecting(DEPTH)
                .on(Layer.Reporter::duration).expectingNull()
                .on(Layer.Reporter::isSkipped).expecting(false);
    }

    @Test
    void ctor_default_producesValidInstance() {
        when:
        {
            target = layer.new Reporter();
        }
        then:
        {
            validator.validate(cast(target));
        }
    }

    @Test
    void report_noLevelNoPatternNoParameters_leavesInstanceUnchanged() {
        given:
        {
            target = layer.new Reporter();
        }
        when:
        {
            target.report(null, null, null);
        }
        then:
        {
            validator.validate(cast(target));
        }
    }

    @Test
    void report_hasLevelNoPatternNoParameters_updatesLevel() {
        given:
        {
            target = layer.new Reporter();
        }
        when:
        {
            target.report(REPORTER_LEVEL, null, null);
        }
        then:
        {
            validator
                    .on(BaseEntry::level).expecting(REPORTER_LEVEL)
                    .validate(cast(target));
        }
    }

    @Test
    void report_noLevelHasPatternNoParameters_updatesMessage() {
        given:
        {
            target = layer.new Reporter();
        }
        when:
        {
            target.report(null, JUST_MESSAGE, null);
        }
        then:
        {
            validator
                    .on(BaseEntry::message).expecting(JUST_MESSAGE)
                    .validate(cast(target));
        }
    }

    @Test
    void report_noLevelNoPatternHasParameters_updatesMessage() {
        given:
        {
            target = layer.new Reporter();
        }
        when:
        {
            target.report(null, null, JUST_PARAMETERS);
        }
        then:
        {
            validator
                    .on(BaseEntry::message).expecting(JUST_PARAMETERS_MESSAGE)
                    .validate(cast(target));
        }
    }

    @Test
    void report_noLevelHasPatternHasParameters_updatesMessage() {
        given:
        {
            target = layer.new Reporter();
        }
        when:
        {
            target.report(null, PATTERN, PARAMETERS);
        }
        then:
        {
            validator
                    .on(BaseEntry::message).expecting(MESSAGE)
                    .validate(cast(target));
        }
    }

    @Test
    void report_hasLevelHasPatternHasParameters_updatesLevelAndMessage() {
        given:
        {
            target = layer.new Reporter();
        }
        when:
        {
            target.report(REPORTER_LEVEL, REPORTER_PATTERN, REPORTER_PARAMETERS);
        }
        then:
        {
            validator
                    .on(BaseEntry::level).expecting(REPORTER_LEVEL)
                    .on(BaseEntry::message).expecting(REPORTER_MESSAGE)
                    .validate(cast(target));
        }
    }

    @Test
    void skip_default_setsIsSkippedToTrue() {
        given:
        {
            target = layer.new Reporter();
        }
        when:
        {
            target.skip();
        }
        then:
        {
            validator
                    .on(Layer.Reporter::isSkipped).expecting(true)
                    .validate(cast(target));
        }
    }

    @Test
    void close_calculateDurationEnabled_calculatesDuration() {
        given:
        {
            target = layer.new Reporter();
        }
        when:
        {
            target.close();
        }
        then:
        {
            validator
                    .on(Layer.Reporter::duration).expecting(Duration::isPositive)
                    .validate(cast(target));
        }
    }

    @Test
    void close_calculateDurationDisabled_doesNotCalculateDuration() {
        given:
        {
            context.configuration().entry().layer().setCalculateDuration(false);

            target = layer.new Reporter();
        }
        when:
        {
            target.close();
        }
        then:
        {
            validator
                    .on(Layer.Reporter::duration).expecting(Duration.ZERO)
                    .validate(cast(target));
        }
    }

    @Test
    void close_default_passesSelfToContext() {
        given:
        {
            target = layer.new Reporter();
        }
        when:
        {
            target.close();
        }
        then:
        {
            assertThat(context.acceptedEntries())
                    .containsOnly((BaseEntry) target);
        }
    }

    @Test
    void flatten_default_returnsExpectedInstance() {
        given:
        {
            target = layer.new Reporter();
        }
        when:
        {
            flattened = ((Layer.Reporter) target).flatten();
        }
        then:
        {
            new EntryValidator<FieldsMessage>()
                    .on(FieldsMessage::name).expecting(LAYER)
                    .on(FieldsMessage::level).expecting(LEVEL)
                    .on(FieldsMessage::message).expecting(MESSAGE + " " + MESSAGE)
                    .on(FieldsMessage::fields).expecting(List.of(FIELD_DATA.asField()))
                    .on(FieldsMessage::depth).expecting(DEPTH)
                    .on(FieldsMessage::duration).expecting(Duration::isPositive)
                    .validate(flattened);

        }
    }

}
