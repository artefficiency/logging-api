package tech.artefficiency.logging.tools.formatter.formats;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public abstract class BaseFormatProcessor {

    interface Token {
        String PARAMETER_SEPARATOR = ", ";
    }

    public String process(String pattern, Object[] parameters) {

        if (ArrayUtils.isNotEmpty(parameters)) {
            if (StringUtils.isBlank(pattern)) {
                pattern = compileParametersPattern(parameters.length);
            }

            return doProcess(pattern, parameters);
        }

        return pattern;
    }

    private String parameterSeparator() {
        return Token.PARAMETER_SEPARATOR;
    }

    protected abstract String parameterPattern();

    protected abstract String doProcess(String pattern, Object[] parameters);

    private String compileParametersPattern(int parametersCount) {
        var result = new StringBuilder();

        for (int i = 0; i < parametersCount; i++) {

            if (!result.isEmpty()) {
                result.append(parameterSeparator());
            }

            result.append(parameterPattern());
        }

        return result.toString();
    }
}
