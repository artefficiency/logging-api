package tech.artefficiency.logging.implementation.backend.loggers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.artefficiency.logging.api.Level;
import tech.artefficiency.logging.implementation.compilers.CompiledEntry;

import java.util.logging.Logger;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import static tech.artefficiency.logging.implementation.backend.loggers.StandardLoggerTest.Data.*;

@ExtendWith(MockitoExtension.class)
public class StandardLoggerTest {

    interface Data {
        Level                               LEVEL       = Level.DEBUG;
        Map<Level, java.util.logging.Level> LEVELS      = Map.of(
                Level.ERROR, java.util.logging.Level.SEVERE,
                Level.WARN, java.util.logging.Level.WARNING,
                Level.INFO, java.util.logging.Level.INFO,
                Level.DEBUG, java.util.logging.Level.FINE,
                Level.TRACE, java.util.logging.Level.FINER
        );
        String                              LOGGER_NAME = "logger";
        String                              ENTRY_DATA  = "data";
    }

    @Mock
    Logger loggerMock;

    @Captor
    ArgumentCaptor<java.util.logging.Level> levelCaptor;

    @Captor
    ArgumentCaptor<String> dataCaptor;

    StandardLogger target;

    String  name;
    boolean isEnabled;

    @BeforeEach
    void initializeTest() {
        target = new StandardLogger(LOGGER_NAME, __ -> loggerMock);
    }

    @Test
    void name_default_delegatesToLoggerName() {
        when:
        {
            name = target.name();
        }
        then:
        {
            verify(loggerMock).getName();
        }
    }

    @Test
    void isEnabled_default_delegatesToLoggerIsEnabledForLevel() {
        when:
        {
            target.isEnabled(LEVEL);
        }
        then:
        {
            verify(loggerMock).isLoggable(LEVELS.get(LEVEL));
        }
    }

    @ParameterizedTest
    @EnumSource(Level.class)
    void write_default_callsUnderlyingLoggerWithCorrectParameters(Level level) {
        when:
        {
            target.write(entryAt(level));
        }
        then:
        {
            verify(loggerMock).log(levelCaptor.capture(), dataCaptor.capture());

            assertThat(levelCaptor.getValue()).isEqualTo(LEVELS.get(level));
            assertThat(dataCaptor.getValue()).isEqualTo(ENTRY_DATA);
        }
    }

    private CompiledEntry entryAt(Level level) {
        return new CompiledEntry(level, ENTRY_DATA);
    }
}
