package tech.artefficiency.logging.implementation.backend.loggers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.artefficiency.logging.api.Level;
import tech.artefficiency.logging.implementation.backend.BackendLogger;
import tech.artefficiency.logging.implementation.compilers.CompiledEntry;


public class slf4jLogger implements BackendLogger {

    private final Logger logger;

    public slf4jLogger(String name) {
        this.logger = LoggerFactory.getLogger(name);
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
