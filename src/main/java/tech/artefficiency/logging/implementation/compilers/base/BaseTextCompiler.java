package tech.artefficiency.logging.implementation.compilers.base;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import tech.artefficiency.logging.configuration.Configuration;
import tech.artefficiency.logging.data.entries.base.BaseEntry;
import tech.artefficiency.logging.data.exception.ExceptionInfo;
import tech.artefficiency.logging.data.stack.StackInfo;

import java.util.List;
import java.util.function.BiPredicate;

import static tech.artefficiency.logging.implementation.compilers.base.BaseTextCompiler.Token.*;

public abstract class BaseTextCompiler<T extends BaseEntry> {

    protected interface Token {
        String OFFSET                = "   ";
        String SEPARATOR             = ":";
        String SPACE                 = " ";
        String DURATION_START        = "[";
        String DURATION_STOP         = "]";
        String FIELDS_START          = "(";
        String FIELDS_STOP           = ")";
        String FIELDS_SEPARATOR      = ",";
        String FIELD_VALUE_SEPARATOR = "=";
        String LAYER_IN              = "{+}";
        String LAYER_OUT             = "{-}";
        String STACK_DOTS            = "...";
        String STACK_MORE            = " more";
        String EXCEPTION             = "Exception: ";
        String EXCEPTION_CAUSE       = "Caused by: ";
        String NULL                  = "null";
    }

    private final Configuration                       configuration;
    private final List<BiPredicate<StringBuilder, T>> pipeline;

    protected BaseTextCompiler(Configuration configuration) {
        this.configuration = configuration;
        this.pipeline      = initializePipeline();
    }

    protected abstract List<BiPredicate<StringBuilder, T>> initializePipeline();

    protected Configuration.Compiler configuration() {
        return configuration.compiler();
    }

    public String compile(T entry) {

        if (entry == null) {
            return NULL;
        }

        var result = new StringBuilder();

        for (var step : pipeline) {
            if (step.test(result, entry)) {
                result.append(SPACE);
            }
        }

        return result.toString();
    }

    protected boolean printName(StringBuilder builder, T entry) {
        if (!Strings.isNullOrEmpty(entry.name())) {
            builder.append(entry.name()).append(Token.SEPARATOR);
            return true;
        }
        return false;
    }

    protected boolean printOffset(StringBuilder builder, T entry) {
        if (entry.depth() > 0 && configuration().printOffsets()) {
            builder.append(OFFSET.repeat(entry.depth()));
            return true;
        }
        return false;
    }

    protected boolean printMessage(StringBuilder builder, T entry) {
        var result = true;

        if (StringUtils.isNotBlank(entry.message())) {
            builder.append(entry.message());
        } else {
            result = false;
        }

        return result;
    }

    protected boolean printFields(StringBuilder builder, T entry) {
        var firstField = true;

        if (entry.fields() != null) {

            for (var field : entry.fields()) {
                if (firstField) {
                    builder.append(FIELDS_START);
                    firstField = false;
                } else {
                    builder.append(FIELDS_SEPARATOR);
                }
                builder.append(field.name()).append(FIELD_VALUE_SEPARATOR).append(field.value());
            }

            if (!firstField) {
                builder.append(FIELDS_STOP);
            }
        }

        return !firstField;
    }

    protected boolean printDuration(StringBuilder builder, T entry) {
        var result = true;

        if (entry.duration() != null && configuration().printDuration()) {
            builder.append(DURATION_START);
            builder.append(entry.duration()).append(DURATION_STOP);
        } else {
            result = false;
        }

        return result;
    }

    protected boolean printStack(StringBuilder builder, StackInfo stack) {
        if (stack != null) {
            for (var frame : stack.elements()) {
                builder.append(System.lineSeparator()).append(frame);
            }

            var more = stack.size() - stack.elements().length;

            if (more > 0) {
                builder.append(System.lineSeparator())
                        .append(STACK_DOTS)
                        .append(more)
                        .append(STACK_MORE);
            }

            return true;
        }
        return false;
    }

    protected boolean printException(StringBuilder builder, ExceptionInfo exception, boolean cause) {
        if (exception != null) {
            if (cause) {
                builder.append(System.lineSeparator()).append(EXCEPTION_CAUSE);
            } else {
                builder.append(System.lineSeparator()).append(EXCEPTION);
            }

            builder.append(exception.type()).append(SEPARATOR).append(exception.message());

            printStack(builder, exception.stack());
            printException(builder, exception.causedBy(), true);

            return true;
        }
        return false;
    }
}
