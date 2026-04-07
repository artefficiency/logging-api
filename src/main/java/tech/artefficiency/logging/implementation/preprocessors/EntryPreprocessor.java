package tech.artefficiency.logging.implementation.preprocessors;

import tech.artefficiency.logging.configuration.Configuration;
import tech.artefficiency.logging.data.entries.base.BaseEntry;
import tech.artefficiency.logging.implementation.preprocessors.base.BasePreprocessor;
import tech.artefficiency.logging.implementation.preprocessors.elements.DepthPreprocessor;
import tech.artefficiency.logging.implementation.preprocessors.elements.MaskedPreprocessor;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class EntryPreprocessor implements Consumer<BaseEntry> {

    private final static List<BiFunction<Configuration, Consumer<BaseEntry>, BasePreprocessor<?>>> FACTORIES = List.of(
            DepthPreprocessor::new,
            MaskedPreprocessor::new);

    private final Consumer<BaseEntry> entryPoint;

    public EntryPreprocessor(Configuration configuration, Consumer<BaseEntry> nextConsumer) {
        entryPoint = initializePipeline(configuration, nextConsumer);
    }

    private Consumer<BaseEntry> initializePipeline(Configuration configuration, Consumer<BaseEntry> nextConsumer) {

        for (var factory : FACTORIES) {
            nextConsumer = factory.apply(configuration, nextConsumer);
        }

        return nextConsumer;
    }


    @Override
    public void accept(BaseEntry entry) {
        entryPoint.accept(entry);
    }
}
