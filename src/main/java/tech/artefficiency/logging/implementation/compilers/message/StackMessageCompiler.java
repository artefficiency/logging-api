package tech.artefficiency.logging.implementation.compilers.message;

import tech.artefficiency.logging.configuration.Configuration;
import tech.artefficiency.logging.data.entries.message.StackMessage;
import tech.artefficiency.logging.implementation.compilers.base.BaseTextCompiler;

import java.util.List;
import java.util.function.BiPredicate;

public class StackMessageCompiler extends BaseTextCompiler<StackMessage> {

    public StackMessageCompiler(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected List<BiPredicate<StringBuilder, StackMessage>> initializePipeline() {
        return List.of(
                this::printOffset,
                this::printMessage,
                this::printFields,
                this::printStack
        );
    }

    private boolean printStack(StringBuilder builder, StackMessage message) {
        return printStack(builder, message.stackInfo());
    }
}