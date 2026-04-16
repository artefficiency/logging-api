package tech.artefficiency.logging.tools.formatter.formats;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.artefficiency.logging.tools.formatter.formats.StringFormatProcessorTest.Data.*;

public class StringFormatProcessorTest {

    interface Data {
        String PATTERN              = "User %s logged in from %s";
        String EXPECTED             = "User Alice logged in from 192.168.1.1";
        String ARG1                 = "Alice";
        String ARG2                 = "192.168.1.1";
        int    SINGLE_ARG           = 42;
        String SINGLE_PATTERN       = "Value is %s";
        String SINGLE_COMPILED      = "Value is 42";
        String COMPILED_TWO_PARAMS  = "Alice, 192.168.1.1";
        String COMPILED_NULL_ARRAY  = "null, null";
        String COMPILED_SINGLE_NULL = "Value is null";
    }

    StringFormatProcessor target;

    @Test
    void process_nullPatternWithParameters_createsPatternFromParameters() {
        when:
        {
            target = new StringFormatProcessor();
        }
        then:
        {
            assertThat(target.process(null, new Object[]{ARG1, ARG2}))
                    .isEqualTo(COMPILED_TWO_PARAMS);
        }
    }

    @Test
    void process_blankPatternWithParameters_createsPatternFromParameters() {
        when:
        {
            target = new StringFormatProcessor();
        }
        then:
        {
            assertThat(target.process("  ", new Object[]{ARG1, ARG2}))
                    .isEqualTo(COMPILED_TWO_PARAMS);
        }
    }

    @Test
    void process_emptyPatternWithParameters_createsPatternFromParameters() {
        when:
        {
            target = new StringFormatProcessor();
        }
        then:
        {
            assertThat(target.process("", new Object[]{ARG1, ARG2}))
                    .isEqualTo(COMPILED_TWO_PARAMS);
        }
    }

    @Test
    void process_validPatternWithParameters_formatsCorrectly() {
        when:
        {
            target = new StringFormatProcessor();
        }
        then:
        {
            assertThat(target.process(PATTERN, new Object[]{ARG1, ARG2}))
                    .isEqualTo(EXPECTED);
        }
    }

    @Test
    void process_singleParameter_formatsCorrectly() {
        when:
        {
            target = new StringFormatProcessor();
        }
        then:
        {
            assertThat(target.process(SINGLE_PATTERN, new Object[]{SINGLE_ARG}))
                    .isEqualTo(SINGLE_COMPILED);
        }
    }

    @Test
    void process_nullParameters_returnsPattern() {
        when:
        {
            target = new StringFormatProcessor();
        }
        then:
        {
            assertThat(target.process(PATTERN, null))
                    .isEqualTo(PATTERN);
        }
    }

    @Test
    void process_emptyParameters_returnsPattern() {
        when:
        {
            target = new StringFormatProcessor();
        }
        then:
        {
            assertThat(target.process(PATTERN, new Object[]{}))
                    .isEqualTo(PATTERN);
        }
    }

    @Test
    void process_nullPatternWithoutParameters_returnsNull() {
        when:
        {
            target = new StringFormatProcessor();
        }
        then:
        {
            assertThat(target.process(null, null))
                    .isNull();
        }
    }

    @Test
    void process_nullPatternWithNullElementArray_createsPattern() {
        when:
        {
            target = new StringFormatProcessor();
        }
        then:
        {
            assertThat(target.process(null, new Object[]{null, null}))
                    .isEqualTo(COMPILED_NULL_ARRAY);
        }
    }

    @Test
    void process_nullWithinParameters_preservesNull() {
        when:
        {
            target = new StringFormatProcessor();
        }
        then:
        {
            assertThat(target.process(SINGLE_PATTERN, new Object[]{null}))
                    .isEqualTo(COMPILED_SINGLE_NULL);
        }
    }
}
