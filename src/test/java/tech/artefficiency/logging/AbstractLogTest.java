package tech.artefficiency.logging;

import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.artefficiency.logging.api.Level;
import tech.artefficiency.logging.api.Log;
import tech.artefficiency.logging.api.Message;
import tech.artefficiency.logging.data.entries.base.BaseEntry;
import tech.artefficiency.logging.exceptions.ArgumentNullException;
import tech.artefficiency.logging.implementation.logger.EntryLogger;
import tech.artefficiency.logging.implementation.samplers.EntrySampler;
import tech.artefficiency.logging.stubs.*;
import tech.artefficiency.logging.tools.stack.StackHelper;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static tech.artefficiency.logging.AbstractLogTest.Data.*;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractLogTest<T extends Message> {

    interface Data {
        String                   LOGGER_NAME    = "test.logger";
        Level                    LEVEL          = Level.INFO;
        int                      SAMPLE_HITS    = 5;
        Duration                 SAMPLE_TIMEOUT = Duration.ofSeconds(3);
        StackWalker.StackFrame[] FRAMES        = new StackWalker.StackFrame[]{
                new TestStackFrame("com.example.Service", "processOrder", 42),
                new TestStackFrame("com.example.Controller", "handle", 15),
                new TestStackFrame("com.example.Api", "endpoint", 8)
        };
        TestConfiguration        CONFIGURATION = new TestConfiguration();
        TestEntry                ENTRY         = new TestEntry(LEVEL, new TestEntriesContext());
    }

    final static EntryLogger     loggerMock      = mock(EntryLogger.class);
    final static EntrySampler    samplerMock     = mock(EntrySampler.class);
    final static TestStackHelper stackHelperMock = new TestStackHelper(FRAMES);

    @Captor
    ArgumentCaptor<BaseEntry> entryCaptor;

    protected AbstractLog<T> abstractTarget;
    protected Log<T>         target;
    protected T              message;
    private   Throwable      thrown;

    @BeforeAll
    static void initializeClass() {
        AbstractLog.sampler = samplerMock;
        AbstractLog.stack   = stackHelperMock;

        EntryLogger.initialize(CONFIGURATION);
    }

    @BeforeEach
    void initializeTest() {

        reset(samplerMock);
        reset(loggerMock);

        target = createTarget(LOGGER_NAME, x -> loggerMock);
    }

    @AfterAll
    static void cleanupClass() {
        AbstractLog.sampler = new EntrySampler();
        AbstractLog.stack   = new StackHelper().withKnown(AbstractLog.class);
    }

    protected abstract Log<T> createTarget(String loggerName, Function<String, EntryLogger> loggerFactory);

    protected abstract Class<T> messageClass();

    @Test
    void ctor_nullLoggerName_throwsArgumentNullException() {
        when:
        {
            thrown = catchThrowable(() -> new AbstractLog<>(null, messageClass(), x -> loggerMock) {
                @Override
                protected T createMessage(Level level, Class<T> messageClass) {
                    throw new NotImplementedException();
                }
            });
        }
        then:
        {
            assertThat(thrown)
                    .isInstanceOf(ArgumentNullException.class)
                    .hasMessageContaining("loggerName");
        }
    }

    @Test
    void ctor_nullMessageClass_throwsArgumentNullException() {
        when:
        {
            thrown = catchThrowable(() -> new AbstractLog<>(LOGGER_NAME, (Class<T>) null, x -> loggerMock) {
                @Override
                protected T createMessage(Level level, Class<T> messageClass) {
                    throw new NotImplementedException();
                }
            });
        }
        then:
        {
            assertThat(thrown)
                    .isInstanceOf(ArgumentNullException.class)
                    .hasMessageContaining("messageClass");
        }
    }

    @Test
    void ctor_default_createsValidInstance() {
        when:
        {
            abstractTarget = new AbstractLog<>(LOGGER_NAME, messageClass(), x -> loggerMock) {
                @Override
                protected T createMessage(Level level, Class<T> messageClass) {
                    throw new NotImplementedException();
                }
            };
        }
        then:
        {
            assertThat(abstractTarget.loggerName()).isEqualTo(LOGGER_NAME);
            assertThat(abstractTarget.messageClass()).isEqualTo(messageClass());
        }
    }

    @Test
    void every_invalidHitsDisabledLevel_returnsDummyInstance() {
        given:
        {
            when(samplerMock.check(any(), anyInt())).thenReturn(false);
            when(loggerMock.isEnabled(any())).thenReturn(false);
        }
        when:
        {
            message = target.every(SAMPLE_HITS).level(LEVEL);
        }
        then:
        {
            assertThat(message.toString()).contains("(Dummy)");
        }
    }

    @Test
    void every_validHitsDisabledLevel_returnsDummyInstance() {
        given:
        {
            when(samplerMock.check(any(), anyInt())).thenReturn(true);
            when(loggerMock.isEnabled(any())).thenReturn(false);
        }
        when:
        {
            message = target.every(SAMPLE_HITS).level(LEVEL);
        }
        then:
        {
            assertThat(message.toString()).contains("(Dummy)");
        }
    }

    @Test
    void every_invalidHitsEnabledLevel_returnsDummyInstance() {
        given:
        {
            when(samplerMock.check(any(), anyInt())).thenReturn(false);
            when(loggerMock.isEnabled(any())).thenReturn(true);
        }
        when:
        {
            message = target.every(SAMPLE_HITS).level(LEVEL);
        }
        then:
        {
            assertThat(message.toString()).contains("(Dummy)");
        }
    }

    @Test
    void every_validHitsEnabledLevel_returnsSolidInstance() {
        given:
        {
            when(samplerMock.check(any(), anyInt())).thenReturn(true);
            when(loggerMock.isEnabled(any())).thenReturn(true);
        }
        when:
        {
            message = target.every(SAMPLE_HITS).level(LEVEL);
        }
        then:
        {
            assertThat(message.toString()).doesNotContain("(Dummy)");
        }
    }

    @Test
    void every_invalidDurationDisabledLevel_returnsDummyInstance() {
        given:
        {
            when(samplerMock.check(any(), any(Duration.class))).thenReturn(false);
            when(loggerMock.isEnabled(any())).thenReturn(false);
        }
        when:
        {
            message = target.every(SAMPLE_TIMEOUT).level(LEVEL);
        }
        then:
        {
            assertThat(message.toString()).contains("(Dummy)");
        }
    }

    @Test
    void every_validDurationDisabledLevel_returnsDummyInstance() {
        given:
        {
            when(samplerMock.check(any(), any(Duration.class))).thenReturn(true);
            when(loggerMock.isEnabled(any())).thenReturn(false);
        }
        when:
        {
            message = target.every(SAMPLE_TIMEOUT).level(LEVEL);
        }
        then:
        {
            assertThat(message.toString()).contains("(Dummy)");
        }
    }

    @Test
    void every_invalidDurationEnabledLevel_returnsDummyInstance() {
        given:
        {
            when(samplerMock.check(any(), any(Duration.class))).thenReturn(false);
            when(loggerMock.isEnabled(any())).thenReturn(true);
        }
        when:
        {
            message = target.every(SAMPLE_TIMEOUT).level(LEVEL);
        }
        then:
        {
            assertThat(message.toString()).contains("(Dummy)");
        }
    }

    @Test
    void every_validDurationEnabledLevel_returnsSolidInstance() {
        given:
        {
            when(samplerMock.check(any(), any(Duration.class))).thenReturn(true);
            when(loggerMock.isEnabled(any())).thenReturn(true);
        }
        when:
        {
            message = target.every(SAMPLE_TIMEOUT).level(LEVEL);
        }
        then:
        {
            assertThat(message.toString()).doesNotContain("(Dummy)");
        }
    }

    @Test
    void every_invalidPredicateDisabledLevel_returnsDummyInstance() {
        given:
        {
            when(loggerMock.isEnabled(any())).thenReturn(false);
        }
        when:
        {
            message = target.every(() -> false).level(LEVEL);
        }
        then:
        {
            assertThat(message.toString()).contains("(Dummy)");
        }
    }

    @Test
    void every_validPredicateDisabledLevel_returnsDummyInstance() {
        given:
        {
            when(loggerMock.isEnabled(any())).thenReturn(false);
        }
        when:
        {
            message = target.every(() -> true).level(LEVEL);
        }
        then:
        {
            assertThat(message.toString()).contains("(Dummy)");
        }
    }

    @Test
    void every_invalidPredicateEnabledLevel_returnsDummyInstance() {
        given:
        {
            when(loggerMock.isEnabled(any())).thenReturn(true);
        }
        when:
        {
            message = target.every(() -> false).level(LEVEL);
        }
        then:
        {
            assertThat(message.toString()).contains("(Dummy)");
        }
    }

    @Test
    void every_validPredicateEnabledLevel_returnsSolidInstance() {
        given:
        {
            when(loggerMock.isEnabled(any())).thenReturn(true);
        }
        when:
        {
            message = target.every(() -> true).level(LEVEL);
        }
        then:
        {
            assertThat(message.toString()).doesNotContain("(Dummy)");
        }
    }

    @Test
    void entryConfiguration_default_isTheSameAsInEntryLogger() {
        given:
        {
            when(loggerMock.configuration()).thenReturn(CONFIGURATION);
        }
        then:
        {
            assertThat(((AbstractLog<T>) target).entryConfiguration()).isSameAs(CONFIGURATION.entry());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void accept_default_delegatesToLoggerAccept() {
        when:
        {
            ((Consumer<TestEntry>) target).accept(ENTRY);
        }
        then:
        {
            verify(loggerMock).accept(entryCaptor.capture());

            assertThat(entryCaptor.getValue()).isSameAs(ENTRY);
        }
    }
}
