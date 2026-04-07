package tech.artefficiency.logging.implementation.compilers.message;

import tech.artefficiency.logging.configuration.Configuration;
import tech.artefficiency.logging.data.entries.message.BaseMessage;
import tech.artefficiency.logging.implementation.compilers.base.BaseTextCompiler;

import java.util.List;
import java.util.function.BiPredicate;

public final class MessageCompiler extends BaseTextCompiler<BaseMessage<?>> {

    public MessageCompiler(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected List<BiPredicate<StringBuilder, BaseMessage<?>>> initializePipeline() {
        return List.of(
                this::printOffset,
                this::printName,
                this::printMessage,
                this::printFields,
                this::printDuration
        );
    }
}