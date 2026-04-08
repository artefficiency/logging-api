package tech.artefficiency.logging.data.fields;

import org.junit.jupiter.api.Test;
import tech.artefficiency.logging.exceptions.ArgumentNullException;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static tech.artefficiency.logging.data.fields.FieldTest.Data.*;

public class FieldTest {

    interface Data {
        String                   NAME                = "field";
        String                   ANOTHER_NAME        = "another";
        Object                   VALUE               = new Object();
        Object                   ANOTHER_VALUE       = new Object();
        String                   FORMATTED_VALUE     = "string representation";
        Function<Object, String> FORMATTER           = x -> FORMATTED_VALUE;
        String                   TO_STRING           = "(Field) Name: %s, Value: %s".formatted(NAME, VALUE);
        String                   FORMATTED_TO_STRING = "(Field) Name: %s, Value: %s".formatted(NAME, FORMATTED_VALUE);
    }

    Field     target;
    Field     another;
    Throwable thrown;

    @Test
    void ctor_nullName_throwsArgumentNullException() {
        when:
        {
            thrown = catchThrowable(() -> new Field(null, () -> VALUE, FORMATTER));
        }
        then:
        {
            assertThat(thrown)
                    .isInstanceOf(ArgumentNullException.class)
                    .hasMessageContaining("name");
        }
    }

    @Test
    void ctor_nullValue_throwsArgumentNullException() {
        when:
        {
            thrown = catchThrowable(() -> new Field(NAME, null, FORMATTER));
        }
        then:
        {
            assertThat(thrown)
                    .isInstanceOf(ArgumentNullException.class)
                    .hasMessageContaining("value");
        }
    }

    @Test
    void ctor_nullFormatter_initializesInstanceWithDefaultFormatter() {
        when:
        {
            target = new Field(NAME, () -> VALUE, null);
        }
        then:
        {
            assertThat(target.name()).isEqualTo(NAME);
            assertThat(target.value()).isEqualTo(VALUE.toString());
        }
    }

    @Test
    void ctor_default_initializesCorrectInstance() {
        when:
        {
            target = new Field(NAME, () -> VALUE, FORMATTER);
        }
        then:
        {
            assertThat(target.name()).isEqualTo(NAME);
            assertThat(target.value()).isEqualTo(FORMATTED_VALUE);
        }
    }


    @Test
    void of_nullName_throwsArgumentNullException() {
        when:
        {
            thrown = catchThrowable(() -> Field.of(null, () -> VALUE));
        }
        then:
        {
            assertThat(thrown)
                    .isInstanceOf(ArgumentNullException.class)
                    .hasMessageContaining("name");
        }
    }

    @Test
    void of_nullValue_throwsArgumentNullException() {
        when:
        {
            thrown = catchThrowable(() -> Field.of(NAME, null));
        }
        then:
        {
            assertThat(thrown)
                    .isInstanceOf(ArgumentNullException.class)
                    .hasMessageContaining("value");
        }
    }

    @Test
    void of_default_initializesCorrectInstance() {
        when:
        {
            target = Field.of(NAME, () -> VALUE);
        }
        then:
        {
            assertThat(target.name()).isEqualTo(NAME);
            assertThat(target.value()).isEqualTo(VALUE.toString());
        }
    }

    @Test
    void setFormatter_null_changesFormatterToDefault() {
        given:
        {
            target = new Field(NAME, () -> VALUE, FORMATTER);
        }
        when:
        {
            target.setFormatter(null);
        }
        then:
        {
            assertThat(target.name()).isEqualTo(NAME);
            assertThat(target.value()).isEqualTo(VALUE.toString());
        }
    }

    @Test
    void setFormatter_validFormatter_changesFormatterToSet() {
        given:
        {
            target = new Field(NAME, () -> VALUE, null);
        }
        when:
        {
            target.setFormatter(FORMATTER);
        }
        then:
        {
            assertThat(target.name()).isEqualTo(NAME);
            assertThat(target.value()).isEqualTo(FORMATTED_VALUE);
        }
    }

    @Test
    void equals_twoInstancesWithEqualNameAndValue_returnsTrue() {
        given:
        {
            target  = new Field(NAME, () -> VALUE, null);
            another = new Field(NAME, () -> VALUE, null);
        }
        then:
        {
            assertThat(target.equals(another)).isTrue();
        }
    }

    @Test
    void equals_twoInstancesWithDifferentFormatters_returnsFalse() {
        given:
        {
            target  = new Field(NAME, () -> VALUE, null);
            another = new Field(NAME, () -> VALUE, FORMATTER);
        }
        then:
        {
            assertThat(target.equals(another)).isFalse();
        }
    }

    @Test
    void equals_twoInstancesWithDifferentNames_returnsFalse() {
        given:
        {
            target  = new Field(NAME, () -> VALUE, null);
            another = new Field(ANOTHER_NAME, () -> VALUE, null);
        }
        then:
        {
            assertThat(target.equals(another)).isFalse();
        }
    }

    @Test
    void equals_twoInstancesWithDifferentValues_returnsFalse() {
        given:
        {
            target  = new Field(NAME, () -> VALUE, null);
            another = new Field(NAME, () -> ANOTHER_VALUE, null);
        }
        then:
        {
            assertThat(target.equals(another)).isFalse();
        }
    }

    @Test
    void equals_twoInstancesWithDifferentNamesAndValues_returnsFalse() {
        given:
        {
            target  = new Field(NAME, () -> VALUE, null);
            another = new Field(ANOTHER_NAME, () -> ANOTHER_VALUE, null);
        }
        then:
        {
            assertThat(target.equals(another)).isFalse();
        }
    }

    @Test
    void hashCode_twoInstancesWithEqualNameAndValue_returnsSameValue() {
        given:
        {
            target  = new Field(NAME, () -> VALUE, null);
            another = new Field(NAME, () -> VALUE, null);
        }
        then:
        {
            assertThat(target.hashCode()).isEqualTo(another.hashCode());
        }
    }

    @Test
    void hashCode_twoInstancesWithEqualNameAndValueAndFormatter_returnsSameValue() {
        given:
        {
            target  = new Field(NAME, () -> VALUE, FORMATTER);
            another = new Field(NAME, () -> VALUE, FORMATTER);
        }
        then:
        {
            assertThat(target.hashCode()).isEqualTo(another.hashCode());
        }
    }

    @Test
    void toString_defaultFormatter_providesCorrectString() {
        given:
        {
            target = new Field(NAME, () -> VALUE, null);
        }
        then:
        {
            assertThat(target.toString()).isEqualTo(TO_STRING);
        }
    }

    @Test
    void toString_setFormatter_providesCorrectString() {
        given:
        {
            target = new Field(NAME, () -> VALUE, FORMATTER);
        }
        then:
        {
            assertThat(target.toString()).isEqualTo(FORMATTED_TO_STRING);
        }
    }
}
