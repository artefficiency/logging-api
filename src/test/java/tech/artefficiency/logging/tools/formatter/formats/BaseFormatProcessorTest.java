package tech.artefficiency.logging.tools.formatter.formats;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.artefficiency.logging.tools.formatter.formats.BaseFormatProcessorTest.Data.*;

public class BaseFormatProcessorTest {

    interface Data {
        String PATTERN              = "User {} logged in from {}";
        String ARG1                 = "alice";
        String ARG2                 = "192.168.1.1";
        Object[] PARAMETERS        = new Object[]{ARG1, ARG2};
        String EXPECTED             = "User alice logged in from 192.168.1.1";
        String COMPILED_TWO_PARAMS = "alice, 192.168.1.1";

        String SINGLE_PARAM_PATTERN = "Value is {}";
        int    SINGLE_ARG          = 42;
    }

    Slf4jFormatProcessor target;
    String               result;

    @BeforeEach
    void initializeTest() {
        target = new Slf4jFormatProcessor();
    }

    @Test
    void process_withPatternAndParameters_formatsCorrectly() {
        when:
        {
            result = target.process(PATTERN, PARAMETERS);
        }
        then:
        {
            assertThat(result).isEqualTo(EXPECTED);
        }
    }

    @Test
    void process_nullPatternWithParameters_compilesPattern() {
        when:
        {
            result = target.process(null, PARAMETERS);
        }
        then:
        {
            assertThat(result).isEqualTo(COMPILED_TWO_PARAMS);
        }
    }

    @Test
    void process_blankPatternWithParameters_compilesPattern() {
        when:
        {
            result = target.process("  ", PARAMETERS);
        }
        then:
        {
            assertThat(result).isEqualTo(COMPILED_TWO_PARAMS);
        }
    }

    @Test
    void process_emptyPatternWithParameters_compilesPattern() {
        when:
        {
            result = target.process("", PARAMETERS);
        }
        then:
        {
            assertThat(result).isEqualTo(COMPILED_TWO_PARAMS);
        }
    }

    @Test
    void process_withPatternNoParameters_returnsPattern() {
        when:
        {
            result = target.process(PATTERN, null);
        }
        then:
        {
            assertThat(result).isEqualTo(PATTERN);
        }
    }

    @Test
    void process_withPatternEmptyParameters_returnsPattern() {
        when:
        {
            result = target.process(PATTERN, new Object[]{});
        }
        then:
        {
            assertThat(result).isEqualTo(PATTERN);
        }
    }

    @Test
    void process_nullPatternNoParameters_returnsNull() {
        when:
        {
            result = target.process(null, null);
        }
        then:
        {
            assertThat(result).isNull();
        }
    }

    @Test
    void process_nullPatternWithNullElementArray_compilesPattern() {
        when:
        {
            result = target.process(null, new Object[]{null, null});
        }
        then:
        {
            assertThat(result).isEqualTo("null, null");
        }
    }

    @Test
    void process_singleParameter_formatsCorrectly() {
        when:
        {
            result = target.process(SINGLE_PARAM_PATTERN, new Object[]{SINGLE_ARG});
        }
        then:
        {
            assertThat(result).isEqualTo("Value is 42");
        }
    }
}
