package tech.artefficiency.logging.implementation.preprocessors.elements;

import tech.artefficiency.logging.configuration.Configuration;
import tech.artefficiency.logging.data.entries.base.BaseEntry;
import tech.artefficiency.logging.implementation.preprocessors.base.BasePreprocessor;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class MaskedPreprocessor extends BasePreprocessor<Configuration.Preprocessors.Masked> {

    interface Default {
        String REPLACEMENT = "*******";
    }

    public MaskedPreprocessor(Configuration configuration, Consumer<BaseEntry> nextConsumer) {
        super(configuration, c -> c.preprocessors().masked(), nextConsumer);
    }

    @Override
    protected boolean preprocess(BaseEntry entry) {

        for (var field : entry.fields()) {
            if (fields().contains(field.name())) {
                field.setFormatter(this::mask);
            }
        }

        return true;
    }

    @Override
    protected boolean enabled() {
        return fields() != null && !fields().isEmpty();
    }

    private Set<String> fields() {
        return configuration().fields();
    }

    private String mask(Object ignored) {
        return Optional.ofNullable(configuration().replacement()).orElse(Default.REPLACEMENT);
    }
}
