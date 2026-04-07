package tech.artefficiency.logging.implementation.logger;

import tech.artefficiency.logging.api.Level;
import tech.artefficiency.logging.configuration.Configuration;
import tech.artefficiency.logging.data.entries.base.BaseEntry;
import tech.artefficiency.logging.exceptions.ArgumentNullException;
import tech.artefficiency.logging.exceptions.EntryLoggerNotInitializedException;
import tech.artefficiency.logging.implementation.backend.BackendLogger;
import tech.artefficiency.logging.implementation.backend.BackendLoggerFactory;
import tech.artefficiency.logging.implementation.backend.Logger;
import tech.artefficiency.logging.implementation.compilers.EntryCompiler;
import tech.artefficiency.logging.implementation.preprocessors.EntryPreprocessor;

import java.util.Optional;
import java.util.function.Consumer;

public class EntryLogger implements Consumer<BaseEntry>, Logger {

    private static Configuration        configuration;
    private static BackendLoggerFactory backendLoggerFactory;

    public static void initialize(Configuration configuration) {
        EntryLogger.configuration = Optional.ofNullable(configuration)
                .orElseThrow(() -> new ArgumentNullException("configuration"));

        backendLoggerFactory = new BackendLoggerFactory(configuration);
    }

    public static boolean isInitialized() {
        return configuration != null;
    }

    private final BackendLogger       backend;
    private final Consumer<BaseEntry> entrypoint;

    public EntryLogger(String name) {

        if (configuration == null) {
            throw new EntryLoggerNotInitializedException();
        }

        backend    = backendLoggerFactory.create(name);
        entrypoint = new EntryPreprocessor(
                configuration,
                new EntryCompiler(
                        configuration,
                        backend::write));
    }

    public Configuration configuration() {
        return configuration;
    }

    @Override
    public void accept(BaseEntry entry) {
        entrypoint.accept(entry);
    }

    @Override
    public String name() {
        return backend.name();
    }

    @Override
    public boolean isEnabled(Level level) {
        return backend.isEnabled(level);
    }
}
