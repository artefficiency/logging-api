package tech.artefficiency.logging.implementation.logger;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tech.artefficiency.logging.configuration.Configuration;
import tech.artefficiency.logging.exceptions.ArgumentNullException;
import tech.artefficiency.logging.stubs.TestConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static tech.artefficiency.logging.implementation.logger.EntryLoggerTest.Data.*;

public class EntryLoggerTest {

    interface Data {
        String        LOGGER_NAME = "test.logger";
        Configuration CONFIG      = new TestConfiguration();
    }

    private EntryLogger target;
    private Throwable   thrown;
    private boolean     result;

    @BeforeAll
    static void initializeClass() {
        EntryLogger.initialize(CONFIG);
    }

    @Test
    void initialize_nullConfiguration_throwsArgumentNullException() {
        when:
        {
            thrown = catchThrowable(() -> EntryLogger.initialize(null));
        }
        then:
        {
            assertThat(thrown)
                    .isInstanceOf(ArgumentNullException.class)
                    .hasMessageContaining("configuration");
        }
    }

    @Test
    void isInitialized_afterInitialize_returnsTrue() {
        then:
        {
            assertThat(EntryLogger.isInitialized()).isTrue();
        }
    }

    @Test
    void ctor_initialized_createsValidInstance() {
        when:
        {
            target = new EntryLogger(LOGGER_NAME);
        }
        then:
        {
            assertThat(target.name()).isEqualTo(LOGGER_NAME);
        }
    }

    @Test
    void configuration_initialized_returnsConfiguration() {
        given:
        {
            target = new EntryLogger(LOGGER_NAME);
        }
        then:
        {
            assertThat(target.configuration()).isSameAs(CONFIG);
        }
    }

    @Test
    void name_returnsBackendName() {
        given:
        {
            target = new EntryLogger(LOGGER_NAME);
        }
        then:
        {
            assertThat(target.name()).isEqualTo(LOGGER_NAME);
        }
    }

    @Test
    void isEnabled_defaultLevel_returnsTrue() {
        given:
        {
            target = new EntryLogger(LOGGER_NAME);
        }
        when:
        {
            result = target.isEnabled(Data.CONFIG.entry().defaults().level());
        }
        then:
        {
            assertThat(result).isTrue();
        }
    }
}