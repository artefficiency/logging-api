package tech.artefficiency.logging.implementation.compilers;

import tech.artefficiency.logging.configuration.Configuration;
import tech.artefficiency.logging.data.entries.base.BaseEntry;
import tech.artefficiency.logging.data.entries.layer.Layer;
import tech.artefficiency.logging.data.entries.message.BaseMessage;
import tech.artefficiency.logging.data.entries.message.ExceptionMessage;
import tech.artefficiency.logging.data.entries.message.StackMessage;
import tech.artefficiency.logging.exceptions.ArgumentNullException;
import tech.artefficiency.logging.exceptions.UnknownEntryClassException;
import tech.artefficiency.logging.implementation.compilers.layer.LayerCompiler;
import tech.artefficiency.logging.implementation.compilers.layer.ReporterCompiler;
import tech.artefficiency.logging.implementation.compilers.message.ExceptionMessageCompiler;
import tech.artefficiency.logging.implementation.compilers.message.MessageCompiler;
import tech.artefficiency.logging.implementation.compilers.message.StackMessageCompiler;

import java.util.Optional;
import java.util.function.Consumer;

public class EntryCompiler implements Consumer<BaseEntry> {

    private final Compiler                compiler;
    private final Consumer<CompiledEntry> consumer;

    public EntryCompiler(Configuration configuration, Consumer<CompiledEntry> consumer) {

        Optional.ofNullable(configuration)
                .orElseThrow(() -> new ArgumentNullException("configuration"));
        Optional.ofNullable(consumer)
                .orElseThrow(() -> new ArgumentNullException("consumer"));

        this.compiler = new Compiler(configuration);
        this.consumer = consumer;
    }

    @Override
    public void accept(BaseEntry entry) {
        Optional.ofNullable(compiler.compile(entry)).ifPresent(consumer);
    }

    private final static class Compiler {

        private final MessageCompiler          messageCompiler;
        private final StackMessageCompiler     stackMessageCompiler;
        private final ExceptionMessageCompiler exceptionMessageCompiler;
        private final LayerCompiler            layerCompiler;
        private final ReporterCompiler         reporterCompiler;

        public Compiler(Configuration configuration) {
            messageCompiler          = new MessageCompiler(configuration);
            stackMessageCompiler     = new StackMessageCompiler(configuration);
            exceptionMessageCompiler = new ExceptionMessageCompiler(configuration);
            layerCompiler            = new LayerCompiler(configuration);
            reporterCompiler         = new ReporterCompiler(configuration);
        }

        public CompiledEntry compile(BaseEntry entry) {

            if (entry == null) {
                return null;
            }

            var compiled = switch (entry) {
                case Layer layer -> layerCompiler.compile(layer);
                case Layer.Reporter reporter -> reporterCompiler.compile(reporter);
                case StackMessage stackMessage -> stackMessageCompiler.compile(stackMessage);
                case ExceptionMessage exceptionMessage -> exceptionMessageCompiler.compile(exceptionMessage);
                case BaseMessage<?> message -> messageCompiler.compile(message);
                default -> throw new UnknownEntryClassException(entry.getClass());
            };

            return new CompiledEntry(entry.level(), compiled);
        }
    }
}
