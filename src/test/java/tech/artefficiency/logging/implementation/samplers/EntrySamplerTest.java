package tech.artefficiency.logging.implementation.samplers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.artefficiency.logging.data.handle.Handle;
import tech.artefficiency.logging.stubs.TestHandle;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static tech.artefficiency.logging.implementation.samplers.EntrySamplerTest.Data.*;

public class EntrySamplerTest {

    interface Data {
        Handle   HANDLE_1 = new TestHandle("class_1", "method_1", 1);
        Handle   HANDLE_2 = new TestHandle("class_2", "method_2", 2);
        int      HITS     = 10;
        Duration INTERVAL = Duration.ofSeconds(10);
    }

    private EntrySampler target;
    private boolean      result;
    private boolean      anotherResult;
    private Throwable    thrown;

    @BeforeEach
    void initializeTest() {
        target = new EntrySampler();
    }

    @Test
    void checkCount_zeroHits_returnsTrue() {
        when:
        {
            result = target.check(HANDLE_1, 0);
        }
        then:
        {
            assertThat(result).isTrue();
        }
    }

    @Test
    void checkCount_positiveHits_returnsTrueOnFirstCall() {
        when:
        {
            result = target.check(HANDLE_1, HITS);
        }
        then:
        {
            assertThat(result).isTrue();
        }
    }

    @Test
    void checkCount_subsequentCalls_returnsFalse() {
        given:
        {
            target.check(HANDLE_1, HITS);
        }
        when:
        {
            result = target.check(HANDLE_1, HITS);
        }
        then:
        {
            assertThat(result).isFalse();
        }
    }

    @Test
    void checkCount_afterCountMinusOneCalls_returnsFalse() {
        given:
        {
            for (int i = 0; i < HITS - 1; i++) {
                target.check(HANDLE_1, HITS);
            }
        }
        when:
        {
            result = target.check(HANDLE_1, HITS);
        }
        then:
        {
            assertThat(result).isFalse();
        }
    }

    @Test
    void checkCount_afterCountCalls_returnsTrue() {
        given:
        {
            for (int i = 0; i < HITS; i++) {
                target.check(HANDLE_1, HITS);
            }
        }
        when:
        {
            result = target.check(HANDLE_1, HITS);
        }
        then:
        {
            assertThat(result).isTrue();
        }
    }

    @Test
    void checkCount_differentHandles_workIndependently() {
        when:
        {
            result        = target.check(HANDLE_1, HITS);
            anotherResult = target.check(HANDLE_2, HITS);
        }
        then:
        {
            assertThat(result).isTrue();
            assertThat(anotherResult).isTrue();
        }
    }

    @Test
    void checkCount_nullHandle_throwsException() {
        when:
        {
            thrown = catchThrowable(() -> result = target.check(null, HITS));
        }
        then:
        {
            assertThat(thrown).isNotNull();
        }
    }

    @Test
    void checkDuration_firstCall_returnsTrue() {
        when:
        {
            result = target.check(HANDLE_1, INTERVAL);
        }
        then:
        {
            assertThat(result).isTrue();
        }
    }

    @Test
    void checkDuration_secondCallWithinSameInterval_returnsFalse() {
        given:
        {
            target.check(HANDLE_1, INTERVAL);
        }
        when:
        {
            result = target.check(HANDLE_1, INTERVAL);
        }
        then:
        {
            assertThat(result).isFalse();
        }
    }

    @Test
    void checkDuration_differentHandles_workIndependently() {
        when:
        {
            result        = target.check(HANDLE_1, INTERVAL);
            anotherResult = target.check(HANDLE_2, INTERVAL);
        }
        then:
        {
            assertThat(result).isTrue();
            assertThat(anotherResult).isTrue();
        }
    }
}