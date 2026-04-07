package tech.artefficiency.logging.implementation.compilers.message;

import tech.artefficiency.logging.configuration.Configuration;
import tech.artefficiency.logging.data.entries.message.ExceptionMessage;
import tech.artefficiency.logging.implementation.compilers.base.BaseTextCompiler;

import java.util.List;
import java.util.function.BiPredicate;

public class ExceptionMessageCompiler extends BaseTextCompiler<ExceptionMessage> {

    public ExceptionMessageCompiler(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected List<BiPredicate<StringBuilder, ExceptionMessage>> initializePipeline() {
        return List.of(
                this::printOffset,
                this::printMessage,
                this::printFields,
                this::printException
        );
    }

    private boolean printException(StringBuilder builder, ExceptionMessage message) {
        return printException(builder, message.exceptionInfo(), false);
    }
}