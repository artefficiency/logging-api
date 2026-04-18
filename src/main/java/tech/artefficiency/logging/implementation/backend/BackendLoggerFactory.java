package tech.artefficiency.logging.implementation.backend;

import tech.artefficiency.logging.configuration.Configuration;
import tech.artefficiency.logging.implementation.backend.loggers.StandardLogger;
import tech.artefficiency.logging.implementation.backend.loggers.SystemOutLogger;
import tech.artefficiency.logging.implementation.backend.loggers.Slf4jLogger;

import java.util.Optional;

public class BackendLoggerFactory {

    interface Default {
        Configuration.Logger.Backend BACKEND = Configuration.Logger.Backend.SLF4J;
    }

    private final Configuration configuration;

    public BackendLoggerFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    private Configuration.Logger.Backend backend() {
        return Optional.ofNullable(configuration)
                .map(Configuration::logger)
                .map(Configuration.Logger::backend)
                .orElse(Default.BACKEND);
    }

    public BackendLogger create(String name) {
        return switch (backend()) {
            case SYSTEM_OUT -> new SystemOutLogger(name);
            case STANDARD -> new StandardLogger(name);
            case SLF4J -> new Slf4jLogger(name);
        };
    }
}
