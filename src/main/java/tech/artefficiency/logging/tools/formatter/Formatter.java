package tech.artefficiency.logging.tools.formatter;

import tech.artefficiency.logging.configuration.Configuration;
import tech.artefficiency.logging.tools.formatter.formats.Slf4jFormatProcessor;
import tech.artefficiency.logging.tools.formatter.formats.StringFormatProcessor;

public class Formatter {

    private final static StringFormatProcessor stringFormat = new StringFormatProcessor();
    private final static Slf4jFormatProcessor  slf4jFormat  = new Slf4jFormatProcessor();

    public static ConcreteFormatter formatter(Configuration.Entry.PatternStyle style) {
        return (pattern, params) -> process(style, pattern, params);
    }

    private static String process(Configuration.Entry.PatternStyle style, String pattern, Object[] parameters) {
        return switch (style) {
            case SLF4J -> slf4jFormat.process(pattern, parameters);
            case STRING_FORMAT -> stringFormat.process(pattern, parameters);
        };
    }

    public interface ConcreteFormatter {
        String process(String pattern, Object[] parameters);
    }
}
