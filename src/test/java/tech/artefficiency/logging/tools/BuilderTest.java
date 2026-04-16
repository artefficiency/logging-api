package tech.artefficiency.logging.tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.artefficiency.logging.exceptions.ArgumentNullException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BuilderTest {

    interface Data {
        String INITIAL = "initial";
        String CHANGED = "changed";
    }

    private TestBuilder target;

    @BeforeEach
    void initializeTest() {
        target = new TestBuilder(Data.INITIAL);
    }

    @Test
    void acceptNull_validValue_changesValue() {
        when:
        {
            target.acceptNull(Data.CHANGED);
        }
        then:
        {
            assertThat(target.value()).isEqualTo(Data.CHANGED);
        }
    }

    @Test
    void acceptNull_null_changesValue() {
        when:
        {
            target.acceptNull(null);
        }
        then:
        {
            assertThat(target.value()).isNull();
        }
    }

    @Test
    void skipNull_validValue_changesValue() {
        when:
        {
            target.skipNull(Data.CHANGED);
        }
        then:
        {
            assertThat(target.value()).isEqualTo(Data.CHANGED);
        }
    }

    @Test
    void skipNull_null_leavesValueUnchanged() {
        when:
        {
            target.skipNull(null);
        }
        then:
        {
            assertThat(target.value()).isEqualTo(Data.INITIAL);
        }
    }

    @Test
    void throwOnNull_validValue_changesValue() {
        when:
        {
            target.throwOnNull(Data.CHANGED);
        }
        then:
        {
            assertThat(target.value()).isEqualTo(Data.CHANGED);
        }
    }

    @Test
    void throwOnNull_null_throwsArgumentNullException() {
        then:
        {
            assertThatThrownBy(() -> target.throwOnNull(null))
                    .isInstanceOf(ArgumentNullException.class);
        }
    }

    @Test
    void setAndContinue_nullSetter_returnsSameInstance() {
        given:
        {
        }
        when:
        {
            target.setAndContinueWithNullSetter(Data.CHANGED);
        }
        then:
        {
            assertThat(target.value()).isEqualTo(Data.INITIAL);
        }
    }

    @Test
    void setAndContinue_nullSetterNullValue_returnsSameInstance() {
        given:
        {
        }
        when:
        {
            target.setAndContinueWithNullSetter(null);
        }
        then:
        {
            assertThat(target.value()).isEqualTo(Data.INITIAL);
        }
    }

    private static final class TestBuilder extends Builder<TestBuilder> {

        private String value;

        public TestBuilder(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }

        public void acceptNull(String value) {
            setAndContinue(value, x -> this.value = x, NullAction.ACCEPT);
        }

        public void skipNull(String value) {
            setAndContinue(value, x -> this.value = x, NullAction.SKIP);
        }

        public void throwOnNull(String value) {
            setAndContinue(value, x -> this.value = x, NullAction.THROW);
        }

        public void setAndContinueWithNullSetter(String value) {
            setAndContinue(value, null, NullAction.ACCEPT);
        }
    }
}
