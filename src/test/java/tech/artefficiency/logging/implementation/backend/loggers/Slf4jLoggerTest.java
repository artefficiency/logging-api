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
import org.slf4j.Logger;
import org.slf4j.spi.LoggingEventBuilder;
import tech.artefficiency.logging.api.Level;
import tech.artefficiency.logging.implementation.compilers.CompiledEntry;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tech.artefficiency.logging.implementation.backend.loggers.Slf4jLoggerTest.Data.*;

@ExtendWith(MockitoExtension.class)
public class Slf4jLoggerTest {

    interface Data {
        Level                             LEVEL       = Level.DEBUG;
        Map<Level, org.slf4j.event.Level> LEVELS      = Map.of(
                Level.ERROR, org.slf4j.event.Level.ERROR,
                Level.WARN, org.slf4j.event.Level.WARN,
                Level.INFO, org.slf4j.event.Level.INFO,
                Level.DEBUG, org.slf4j.event.Level.DEBUG,
                Level.TRACE, org.slf4j.event.Level.TRACE
        );
        String                            LOGGER_NAME = "logger";
        String                            ENTRY_DATA  = "data";
    }

    @Mock
    Logger loggerMock;

    @Mock
    LoggingEventBuilder eventBuilder;

    @Captor
    ArgumentCaptor<Level> levelCaptor;

    @Captor
    ArgumentCaptor<String> dataCaptor;

    Slf4jLogger target;

    String  name;
    boolean isEnabled;

    @BeforeEach
    void initializeTest() {
        target = new Slf4jLogger(LOGGER_NAME, __ -> loggerMock);
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
            verify(loggerMock).isEnabledForLevel(LEVELS.get(LEVEL));
        }
    }

    @ParameterizedTest
    @EnumSource(Level.class)
    void write_default_callsUnderlyingLoggerWithCorrectParameters(Level level) {
        given:
        {
            when(loggerMock.atLevel(LEVELS.get(level))).thenReturn(eventBuilder);
        }
        when:
        {
            target.write(entryAt(level));
        }
        then:
        {
            verify(eventBuilder).log(dataCaptor.capture());

            assertThat(dataCaptor.getValue()).isEqualTo(ENTRY_DATA);
        }
    }

    private CompiledEntry entryAt(Level level) {
        return new CompiledEntry(level, ENTRY_DATA);
    }
}
