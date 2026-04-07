package tech.artefficiency.logging.implementation.preprocessors.base;

import tech.artefficiency.logging.configuration.Configuration;
import tech.artefficiency.logging.data.entries.base.BaseEntry;
import tech.artefficiency.logging.exceptions.ArgumentNullException;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class BasePreprocessor<C> implements Consumer<BaseEntry> {

    private final Configuration              configuration;
    private final Function<Configuration, C> configurationGetter;
    private final Consumer<BaseEntry>        nextConsumer;

    public BasePreprocessor(Configuration configuration, Function<Configuration, C> configurationGetter, Consumer<BaseEntry> nextConsumer) {

        this.configuration = Optional.ofNullable(configuration)
                .orElseThrow(() -> new ArgumentNullException("configuration"));

        this.configurationGetter = Optional.ofNullable(configurationGetter)
                .orElseThrow(() -> new ArgumentNullException("configurationGetter"));

        this.nextConsumer = Optional.ofNullable(nextConsumer)
                .orElseThrow(() -> new ArgumentNullException("nextConsumer"));
    }

    protected C configuration() {
        return configurationGetter.apply(configuration);
    }

    @Override
    public void accept(BaseEntry entry) {

        var proceed = true;

        if (enabled()) {
            proceed = preprocess(entry);
        }

        if (proceed) {
            passToNext(entry);
        }
    }

    protected abstract boolean preprocess(BaseEntry entry);

    protected void passToNext(BaseEntry entry) {
        nextConsumer.accept(entry);
    }

    protected abstract boolean enabled();
}
