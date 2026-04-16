package tech.artefficiency.logging.tools.formatter;

import org.junit.jupiter.api.Test;
import tech.artefficiency.logging.configuration.Configuration.Entry.PatternStyle;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.artefficiency.logging.tools.formatter.FormatterTest.Data.*;

public class FormatterTest {

    interface Data {
        String   PATTERN         = "User {} logged in from {}";
        String   ARG1            = "Alice";
        String   ARG2            = "192.168.1.1";
        Object[] PARAMETERS      = new Object[]{ARG1, ARG2};
        String   EXPECTED        = "User Alice logged in from 192.168.1.1";
        String   COMPILED_PARAMS = "Alice, 192.168.1.1";
        String   STRING_PATTERN  = "User %s logged in from %s";
        String   STRING_EXPECTED = "User Alice logged in from 192.168.1.1";
    }

    Formatter.ConcreteFormatter target;

    @Test
    void formatter_slf4jStyle_returnsFormatter() {
        when:
        {
            target = Formatter.formatter(PatternStyle.SLF4J);
        }
        then:
        {
            assertThat(target).isNotNull();
        }
    }

    @Test
    void formatter_stringFormatStyle_returnsFormatter() {
        when:
        {
            target = Formatter.formatter(PatternStyle.STRING_FORMAT);
        }
        then:
        {
            assertThat(target).isNotNull();
        }
    }

    @Test
    void formatter_slf4jStyle_formatsCorrectly() {
        when:
        {
            target = Formatter.formatter(PatternStyle.SLF4J);
        }
        then:
        {
            assertThat(target.process(PATTERN, PARAMETERS))
                    .isEqualTo(EXPECTED);
        }
    }

    @Test
    void formatter_stringFormatStyle_formatsCorrectly() {
        when:
        {
            target = Formatter.formatter(PatternStyle.STRING_FORMAT);
        }
        then:
        {
            assertThat(target.process(STRING_PATTERN, PARAMETERS))
                    .isEqualTo(STRING_EXPECTED);
        }
    }

    @Test
    void formatter_slf4jStyle_nullPatternWithParameters_compilesPattern() {
        when:
        {
            target = Formatter.formatter(PatternStyle.SLF4J);
        }
        then:
        {
            assertThat(target.process(null, PARAMETERS))
                    .isEqualTo(COMPILED_PARAMS);
        }
    }

    @Test
    void formatter_stringFormatStyle_nullPatternWithParameters_compilesPattern() {
        when:
        {
            target = Formatter.formatter(PatternStyle.STRING_FORMAT);
        }
        then:
        {
            assertThat(target.process(null, PARAMETERS))
                    .isEqualTo(COMPILED_PARAMS);
        }
    }

    @Test
    void formatter_slf4jStyle_nullPatternNullParameters_returnsNull() {
        when:
        {
            target = Formatter.formatter(PatternStyle.SLF4J);
        }
        then:
        {
            assertThat(target.process(null, null)).isNull();
        }
    }

    @Test
    void formatter_stringFormatStyle_nullPatternNullParameters_returnsNull() {
        when:
        {
            target = Formatter.formatter(PatternStyle.STRING_FORMAT);
        }
        then:
        {
            assertThat(target.process(null, null)).isNull();
        }
    }
}
