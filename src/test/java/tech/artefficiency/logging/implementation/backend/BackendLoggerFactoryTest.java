package tech.artefficiency.logging.implementation.backend;

import org.junit.jupiter.api.Test;
import tech.artefficiency.logging.configuration.Configuration;
import tech.artefficiency.logging.implementation.backend.loggers.StandardLogger;
import tech.artefficiency.logging.implementation.backend.loggers.SystemOutLogger;
import tech.artefficiency.logging.implementation.backend.loggers.Slf4jLogger;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class BackendLoggerFactoryTest {

    interface Data {
        String LOGGER_NAME = "logger";
    }

    BackendLoggerFactory target;
    BackendLogger        backendLogger;

    @Test
    void create_nullBackend_createsDefaultBackend() {
        given:
        {
            target = factoryFor(null);
        }
        when:
        {
            backendLogger = target.create(Data.LOGGER_NAME);
        }
        then:
        {
            assertThat(backendLogger).isExactlyInstanceOf(Slf4jLogger.class);
            assertThat(backendLogger.name()).isEqualTo(Data.LOGGER_NAME);
        }
    }

    @Test
    void create_systemOutBackend_createsSystemOutBackend() {
        given:
        {
            target = factoryFor(Configuration.Logger.Backend.SYSTEM_OUT);
        }
        when:
        {
            backendLogger = target.create(Data.LOGGER_NAME);
        }
        then:
        {
            assertThat(backendLogger).isExactlyInstanceOf(SystemOutLogger.class);
            assertThat(backendLogger.name()).isEqualTo(Data.LOGGER_NAME);
        }
    }

    @Test
    void create_standardBackend_createsSystemOutBackend() {
        given:
        {
            target = factoryFor(Configuration.Logger.Backend.STANDARD);
        }
        when:
        {
            backendLogger = target.create(Data.LOGGER_NAME);
        }
        then:
        {
            assertThat(backendLogger).isExactlyInstanceOf(StandardLogger.class);
            assertThat(backendLogger.name()).isEqualTo(Data.LOGGER_NAME);
        }
    }

    @Test
    void create_slf4jBackend_createsSystemOutBackend() {
        given:
        {
            target = factoryFor(Configuration.Logger.Backend.SLF4J);
        }
        when:
        {
            backendLogger = target.create(Data.LOGGER_NAME);
        }
        then:
        {
            assertThat(backendLogger).isExactlyInstanceOf(Slf4jLogger.class);
            assertThat(backendLogger.name()).isEqualTo(Data.LOGGER_NAME);
        }
    }

    private BackendLoggerFactory factoryFor(Configuration.Logger.Backend backend) {
        var configuration = Optional.ofNullable(backend)
                .map(b -> new Configuration() {
                    @Override
                    public Logger logger() {
                        return new Logger() {
                            @Override
                            public Backend backend() {
                                return b;
                            }
                        };
                    }
                })
                .orElse(null);

        return new BackendLoggerFactory(configuration);
    }
}
