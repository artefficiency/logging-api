package tech.artefficiency.logging.tools.formatter.formats;

import org.slf4j.helpers.MessageFormatter;

public final class Slf4jFormatProcessor extends BaseFormatProcessor {

    interface Token extends BaseFormatProcessor.Token {
        String PARAMETER_PATTERN = "{}";
    }

    @Override
    protected String parameterPattern() {
        return Token.PARAMETER_PATTERN;
    }

    @Override
    protected String doProcess(String pattern, Object[] parameters) {
        return MessageFormatter.basicArrayFormat(pattern, parameters);
    }
}
