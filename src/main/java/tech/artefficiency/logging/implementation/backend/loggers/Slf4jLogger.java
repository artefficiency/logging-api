package tech.artefficiency.logging.implementation.backend.loggers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.artefficiency.logging.api.Level;
import tech.artefficiency.logging.implementation.backend.BackendLogger;
import tech.artefficiency.logging.implementation.compilers.CompiledEntry;

import java.util.function.Function;


public class Slf4jLogger implements BackendLogger {

    private final Logger logger;

    public Slf4jLogger(String name) {
        this(name, LoggerFactory::getLogger);
    }

    Slf4jLogger(String name, Function<String, Logger> factory) {
        this.logger = factory.apply(name);
    }

    @Override
    public void write(CompiledEntry layer) {
        logger.atLevel(toNativeLevel(layer.level())).log(layer.data());
    }

    private static org.slf4j.event.Level toNativeLevel(Level level) {
        return switch (level) {
            case ERROR -> org.slf4j.event.Level.ERROR;
            case WARN -> org.slf4j.event.Level.WARN;
            case INFO -> org.slf4j.event.Level.INFO;
            case DEBUG -> org.slf4j.event.Level.DEBUG;
            case TRACE -> org.slf4j.event.Level.TRACE;
        };
    }

    @Override
    public boolean isEnabled(Level level) {
        return logger.isEnabledForLevel(toNativeLevel(level));
    }

    @Override
    public String name() {
        return logger.getName();
    }
}
