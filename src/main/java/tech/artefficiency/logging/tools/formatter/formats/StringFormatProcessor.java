package tech.artefficiency.logging.tools.formatter.formats;

public final class StringFormatProcessor extends BaseFormatProcessor {

    interface Token extends BaseFormatProcessor.Token {
        String PARAMETER_PATTERN = "%s";
    }

    @Override
    protected String parameterPattern() {
        return Token.PARAMETER_PATTERN;
    }

    @Override
    protected String doProcess(String pattern, Object[] parameters) {
        return String.format(pattern, parameters);
    }
}
