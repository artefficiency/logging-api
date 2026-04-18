package tech.artefficiency.logging.data.entries.base;

import com.google.common.collect.Streams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tech.artefficiency.logging.api.Level;
import tech.artefficiency.logging.stubs.FieldData;
import tech.artefficiency.logging.stubs.TestEntriesContext;
import tech.artefficiency.logging.stubs.TestEntry;
import tech.artefficiency.logging.data.tools.EntryValidator;
import tech.artefficiency.logging.exceptions.ArgumentNullException;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static tech.artefficiency.logging.data.entries.base.BaseEntryTest.Data.*;
import static tech.artefficiency.logging.data.entries.base.BaseEntryTest.Data.DURATION;
import static tech.artefficiency.logging.tools.Cast.cast;

public class BaseEntryTest {

    interface Data {
        Level       LEVEL                   = Level.TRACE;
        String      NAME                    = "name";
        int         DEPTH                   = 4;
        Duration    DURATION                = Duration.ofSeconds(2);
        FieldData   FIELD_DATA              = new FieldData("field #0", () -> false, Objects::toString);
        FieldData   FIELD_DATA_FORMATTER    = new FieldData("field #1", () -> 5, $ -> "hello");
        FieldData[] FIELDS_DATA             = new FieldData[]{
                FIELD_DATA,
                FIELD_DATA_FORMATTER,
                new FieldData("field #2", () -> "6", Objects::toString)};
        String      PATTERN                 = "{} + {} = {}";
        Object[]    PARAMETERS              = {1, 2, 3};
        String      MESSAGE                 = "1 + 2 = 3";
        String      JUST_MESSAGE            = "Just message";
        Object[]    JUST_PARAMETERS         = {5, 6};
        String      JUST_PARAMETERS_MESSAGE = "5, 6";
    }

    EntryValidator<TestEntry> validator;
    EntryBuilder<TestEntry>   builder;
    TestEntriesContext        context;
    TestEntry                 parent;
    TestEntry                 target;
    Throwable                 thrown;

    @BeforeEach
    void initializeTest() {
        this.context = new TestEntriesContext();
        this.builder = new EntryBuilder<>(context, TestEntry::new)
                .withLevel(LEVEL);

        this.validator = new EntryValidator<TestEntry>()
                .on(BaseEntry::name).expectingNull()
                .on(BaseEntry::level).expecting(LEVEL)
                .on(BaseEntry::message).expectingNull()
                .on(BaseEntry::fields).expecting(List.of())
                .on(BaseEntry::depth).expecting(0)
                .on(BaseEntry::duration).expectingNull()
                .on(BaseEntry::configuration).expecting(context.entryConfiguration());
    }

    @Nested
    class Constructors {

        @Test
        void ctor_nullLevel_setsDefaultLevel() {
            when:
            {
                target = new TestEntry(null, context);
            }
            then:
            {
                assertThat(target.level())
                        .isEqualTo(context.configuration().entry().defaults().level());
            }
        }

        @Test
        void ctor_nullContext_throwsArgumentNullException() {
            when:
            {
                thrown = catchThrowable(() -> new TestEntry(LEVEL, null));
            }
            then:
            {
                assertThat(thrown)
                        .isInstanceOf(ArgumentNullException.class)
                        .hasMessageContaining("context");
            }
        }

        @Test
        void ctor_default_initializesInstance() {
            when:
            {
                target = builder.build();
            }
            then:
            {
                validator.validate(target);
            }
        }

        @Test
        void ctor_nullParent_throwsNullPointerException() {
            when:
            {
                thrown = catchThrowable(() -> new TestEntry(null));
            }
            then:
            {
                assertThat(thrown)
                        .isInstanceOf(NullPointerException.class);
            }
        }

        @Test
        void ctor_validParent_copiesParentsFields() {
            given:
            {
                parent = builder
                        .withName(NAME)
                        .withPattern(PATTERN)
                        .withParameters(PARAMETERS)
                        .withDepth(DEPTH)
                        .withDuration(DURATION)
                        .withFields(FIELDS_DATA)
                        .build();
            }
            when:
            {
                target = new TestEntry(parent);
            }
            then:
            {
                validator.on(TestEntry::name).expecting(NAME)
                        .on(TestEntry::message).expecting(MESSAGE)
                        .on(TestEntry::depth).expecting(DEPTH)
                        .on(TestEntry::duration).expecting(DURATION)
                        .on(TestEntry::fields).expecting(Arrays.stream(FIELDS_DATA).map(FieldData::asField).toList())
                        .validate(target);
            }
        }
    }

    @Nested
    class Properties {
        @Test
        void setLevel_null_doesNotChangeValue() {
            given:
            {
                target = builder.build();
            }
            when:
            {
                target.setLevel(null);
            }
            then:
            {
                validator.validate(target);
            }
        }

        @Test
        void setLevel_default_setsValue() {
            given:
            {
                target = builder.build();
            }
            when:
            {
                target.setLevel(LEVEL);
            }
            then:
            {
                validator.validate(target);
            }
        }

        @Test
        void setName_default_setsValue() {
            given:
            {
                target = builder.build();
            }
            when:
            {
                target.setName(NAME);
            }
            then:
            {
                validator
                        .on(BaseEntry::name).expecting(NAME)
                        .validate(target);
            }
        }

        @Test
        void setName_null_doesNotChangeValue() {
            given:
            {
                target = builder
                        .withName(NAME)
                        .build();
            }
            when:
            {
                target.setName(null);
            }
            then:
            {
                validator
                        .on(BaseEntry::name).expecting(NAME)
                        .validate(target);
            }
        }

        @Test
        void setDepth_default_setsValue() {
            given:
            {
                target = builder.build();
            }
            when:
            {
                target.setDepth(DEPTH);
            }
            then:
            {
                validator
                        .on(BaseEntry::depth).expecting(DEPTH)
                        .validate(target);
            }
        }

        @Test
        void setDuration_default_setsValue() {
            given:
            {
                target = builder.build();
            }
            when:
            {
                target.setDuration(DURATION);
            }
            then:
            {
                validator
                        .on(BaseEntry::duration).expecting(DURATION)
                        .validate(target);
            }
        }

        @Test
        void setDuration_null_doesNotChangeValue() {
            given:
            {
                target = builder
                        .withDuration(DURATION)
                        .build();
            }
            when:
            {
                target.setDuration(null);
            }
            then:
            {
                validator
                        .on(BaseEntry::duration).expecting(DURATION)
                        .validate(target);
            }
        }

        @Test
        void setField_nullName_throwsArgumentNullException() {
            given:
            {
                target = builder.build();
            }
            when:
            {
                thrown = catchThrowable(() -> target.setField(null, FIELD_DATA.value(), FIELD_DATA.formatter()));
            }
            then:
            {
                assertThat(thrown)
                        .isInstanceOf(ArgumentNullException.class)
                        .hasMessageContaining("name");
            }
        }

        @Test
        void setField_nullSupplier_throwsArgumentNullException() {
            given:
            {
                target = builder.build();
            }
            when:
            {
                thrown = catchThrowable(() -> target.setField(FIELD_DATA.name(), null, FIELD_DATA.formatter()));
            }
            then:
            {
                assertThat(thrown)
                        .isInstanceOf(ArgumentNullException.class)
                        .hasMessageContaining("value");
            }
        }

        @Test
        void setField_nullFormatter_addsField() {
            given:
            {
                target = builder.build();
            }
            when:
            {
                target.setField(FIELD_DATA.name(), FIELD_DATA.value(), null);
            }
            then:
            {
                validator
                        .on(BaseEntry::fields).expecting(List.of(FIELD_DATA.asField()))
                        .validate(target);
            }
        }

        @Test
        void setField_nullFormatter_addsCorrectField() {
            given:
            {
                target = builder.build();
            }
            when:
            {
                target.setField(FIELD_DATA.name(), FIELD_DATA.value(), null);
            }
            then:
            {
                validator
                        .on(BaseEntry::fields).expecting(List.of(FIELD_DATA.asField()))
                        .validate(target);
            }
        }

        @Test
        void setField_default_addsCorrectField() {
            given:
            {
                target = builder.build();
            }
            when:
            {
                target.setField(FIELD_DATA_FORMATTER.name(),
                                FIELD_DATA_FORMATTER.value(),
                                FIELD_DATA_FORMATTER.formatter());
            }
            then:
            {
                validator
                        .on(BaseEntry::fields).expecting(List.of(FIELD_DATA_FORMATTER.asField()))
                        .validate(target);
            }
        }

        @Test
        void setField_severalTimes_addsAllFields() {
            given:
            {
                target = builder.build();
            }
            when:
            {
                Arrays.stream(FIELDS_DATA).forEach(field ->
                                                           target.setField(field.name(),
                                                                           cast(field.value()),
                                                                           cast(field.formatter())));
            }
            then:
            {
                validator
                        .on(BaseEntry::fields).expecting(
                                Arrays.stream(FIELDS_DATA)
                                        .map(FieldData::asField)
                                        .toList())
                        .validate(target);
            }
        }

        @Test
        void setMessage_default_setsValue() {
            given:
            {
                target = builder.build();
            }
            when:
            {
                target.setMessage(PATTERN, PARAMETERS);
            }
            then:
            {
                validator
                        .on(BaseEntry::message).expecting(MESSAGE)
                        .validate(target);
            }
        }

        @Test
        void setMessage_nullPatternNullParameters_doesNotChangeValue() {
            given:
            {
                target = builder
                        .withPattern(PATTERN)
                        .withParameters(PARAMETERS)
                        .build();
            }
            when:
            {
                target.setMessage(null, null);
            }
            then:
            {
                validator
                        .on(BaseEntry::message).expecting(MESSAGE)
                        .validate(target);
            }
        }

        @Test
        void setMessage_validPatternNullParameters_setsMessageToPattern() {
            given:
            {
                target = builder
                        .withPattern(PATTERN)
                        .withParameters(PARAMETERS)
                        .build();
            }
            when:
            {
                target.setMessage(JUST_MESSAGE, null);
            }
            then:
            {
                validator
                        .on(BaseEntry::message).expecting(JUST_MESSAGE)
                        .validate(target);
            }
        }

        @Test
        void setMessage_nullPatternValidParameters_setsMessageToParametersWithDelimiters() {
            given:
            {
                target = builder
                        .withPattern(PATTERN)
                        .withParameters(PARAMETERS)
                        .build();
            }
            when:
            {
                target.setMessage(null, JUST_PARAMETERS);
            }
            then:
            {
                validator
                        .on(BaseEntry::message).expecting(JUST_PARAMETERS_MESSAGE)
                        .validate(target);
            }
        }
    }

    @Nested
    class Commit {
        @Test
        void commit_default_passesInstanceToContext() {
            given:
            {
                target = builder.build();
            }
            when:
            {
                target.commit();
            }
            then:
            {
                assertThat(context.acceptedEntries())
                        .containsExactly(target);
            }
        }

        @Test
        void commitWith_patternAndParametersSet_passesInstanceToContextWithPassedPatternAndParameter() {
            given:
            {
                target = builder.build();

                validator
                        .on(BaseEntry::message).expecting(MESSAGE);
            }
            when:
            {
                target.commitWith(PATTERN, PARAMETERS);
            }
            then:
            {
                assertThat(context.acceptedEntries())
                        .containsExactly(target);
            }
        }

        @Test
        void commitWith_patternAndParametersNull_passesInstanceToContextWithInitialPatternAndParameter() {
            given:
            {
                target = builder
                        .withPattern(PATTERN)
                        .withParameters(PARAMETERS)
                        .build();

                validator
                        .on(BaseEntry::message).expecting(MESSAGE);
            }
            when:
            {
                target.commitWith(null);
            }
            then:
            {
                assertThat(context.acceptedEntries())
                        .containsExactly(target);
            }
        }
    }

    @Nested
    class Merge {

        EntryBuilder<TestEntry> targetBuilder;
        EntryBuilder<TestEntry> parentBuilder;

        @BeforeEach
        void initializeTest() {
            targetBuilder = new EntryBuilder<>(context, TestEntry::new)
                    .withLevel(Level.ERROR)
                    .withName("parent")
                    .withPattern("parent {}")
                    .withParameters(5)
                    .withDepth(3)
                    .withDuration(Duration.ofSeconds(6))
                    .withFields(new FieldData("field #0", () -> 0, Objects::toString),
                                new FieldData("field #3", () -> 3, Objects::toString));

            parentBuilder = new EntryBuilder<>(context, TestEntry::new)
                    .withLevel(Level.DEBUG)
                    .withName("target")
                    .withPattern("target {} {}")
                    .withParameters(3, 4)
                    .withDepth(7)
                    .withDuration(Duration.ofSeconds(1))
                    .withFields(new FieldData("field #1", () -> 1, Objects::toString));
        }

        @Test
        void merge_validInstance_mergesFieldsCorrectly() {
            given:
            {
                target = targetBuilder.build();
                parent = parentBuilder.build();

                var expectedFields = Streams.concat(
                                targetBuilder.fields().stream(),
                                parentBuilder.fields().stream())
                        .map(FieldData::asField)
                        .toList();

                validator
                        .on(TestEntry::level).expecting(target.level())
                        .on(TestEntry::name).expecting(target.name())
                        .on(TestEntry::message).expecting(target.message() + " " + parent.message())
                        .on(TestEntry::depth).expecting(parent.depth())
                        .on(TestEntry::duration).expecting(target.duration())
                        .on(TestEntry::fields).expecting(expectedFields);
            }
            when:
            {
                target.merge(parent);
            }
            then:
            {
                validator.validate(target);
            }
        }

        @Test
        void merge_targetNameIsNull_setsParentNameToMerged() {
            given:
            {
                target = targetBuilder.withName(null).build();
                parent = parentBuilder.build();

                var expectedFields = Streams.concat(
                                targetBuilder.fields().stream(),
                                parentBuilder.fields().stream())
                        .map(FieldData::asField)
                        .toList();

                validator
                        .on(TestEntry::level).expecting(target.level())
                        .on(TestEntry::name).expecting(parent.name())
                        .on(TestEntry::message).expecting(target.message() + " " + parent.message())
                        .on(TestEntry::depth).expecting(parent.depth())
                        .on(TestEntry::duration).expecting(target.duration())
                        .on(TestEntry::fields).expecting(expectedFields);
            }
            when:
            {
                target.merge(parent);
            }
            then:
            {
                validator.validate(target);
            }
        }

        @Test
        void merge_targetPatternAndParametersNull_setsParentPatternAndParametersToMerged() {
            given:
            {
                target = targetBuilder.withPattern(null).withParameters().build();
                parent = parentBuilder.build();

                var expectedFields = Streams.concat(
                                targetBuilder.fields().stream(),
                                parentBuilder.fields().stream())
                        .map(FieldData::asField)
                        .toList();

                validator
                        .on(TestEntry::level).expecting(target.level())
                        .on(TestEntry::name).expecting(target.name())
                        .on(TestEntry::message).expecting(parent.message())
                        .on(TestEntry::depth).expecting(parent.depth())
                        .on(TestEntry::duration).expecting(target.duration())
                        .on(TestEntry::fields).expecting(expectedFields);
            }
            when:
            {
                target.merge(parent);
            }
            then:
            {
                validator.validate(target);
            }
        }

        @Test
        void merge_parentPatternAndParametersNull_setsTargetPatternAndParametersToMerged() {
            given:
            {
                target = targetBuilder.build();
                parent = parentBuilder.withPattern(null).withParameters().build();

                var expectedFields = Streams.concat(
                                targetBuilder.fields().stream(),
                                parentBuilder.fields().stream())
                        .map(FieldData::asField)
                        .toList();

                validator
                        .on(TestEntry::level).expecting(target.level())
                        .on(TestEntry::name).expecting(target.name())
                        .on(TestEntry::message).expecting(target.message())
                        .on(TestEntry::depth).expecting(parent.depth())
                        .on(TestEntry::duration).expecting(target.duration())
                        .on(TestEntry::fields).expecting(expectedFields);
            }
            when:
            {
                target.merge(parent);
            }
            then:
            {
                validator.validate(target);
            }
        }

        @Test
        void merge_targetHasNoFields_setsParentFieldsToMerged() {
            given:
            {
                target = targetBuilder.withFields().build();
                parent = parentBuilder.build();

                validator
                        .on(TestEntry::level).expecting(target.level())
                        .on(TestEntry::name).expecting(target.name())
                        .on(TestEntry::message).expecting(target.message() + " " + parent.message())
                        .on(TestEntry::depth).expecting(parent.depth())
                        .on(TestEntry::duration).expecting(target.duration())
                        .on(TestEntry::fields).expecting(parent.fields());
            }
            when:
            {
                target.merge(parent);
            }
            then:
            {
                validator.validate(target);
            }
        }

        @Test
        void merge_parentHasNoFields_setsTargetFieldsToMerged() {
            given:
            {
                target = targetBuilder.build();
                parent = parentBuilder.withFields().build();

                validator
                        .on(TestEntry::level).expecting(target.level())
                        .on(TestEntry::name).expecting(target.name())
                        .on(TestEntry::message).expecting(target.message() + " " + parent.message())
                        .on(TestEntry::depth).expecting(parent.depth())
                        .on(TestEntry::duration).expecting(target.duration())
                        .on(TestEntry::fields).expecting(target.fields());
            }
            when:
            {
                target.merge(parent);
            }
            then:
            {
                validator.validate(target);
            }
        }
    }
}
