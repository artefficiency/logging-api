package tech.artefficiency.logging.implementation.backend.loggers;

import tech.artefficiency.logging.api.Level;
import tech.artefficiency.logging.implementation.backend.BackendLogger;
import tech.artefficiency.logging.implementation.compilers.CompiledEntry;

import java.util.function.Function;
import java.util.logging.Logger;

public class StandardLogger implements BackendLogger {

    private final Logger logger;

    public StandardLogger(String name) {
        this(name, Logger::getLogger);
    }

    StandardLogger(String name, Function<String, Logger> factory) {
        this.logger = factory.apply(name);
    }

    @Override
    public String name() {
        return logger.getName();
    }

    @Override
    public boolean isEnabled(Level level) {
        return logger.isLoggable(map(level));
    }

    @Override
    public void write(CompiledEntry layer) {
        logger.log(map(layer.level()), layer.data());
    }

    private java.util.logging.Level map(Level level) {
        return switch (level) {
            case ERROR -> java.util.logging.Level.SEVERE;
            case WARN -> java.util.logging.Level.WARNING;
            case INFO -> java.util.logging.Level.INFO;
            case DEBUG -> java.util.logging.Level.FINE;
            case TRACE -> java.util.logging.Level.FINER;
        };
    }
}