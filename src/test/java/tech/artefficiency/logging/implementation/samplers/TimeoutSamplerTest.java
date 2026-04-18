package tech.artefficiency.logging.implementation.samplers;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static tech.artefficiency.logging.implementation.samplers.TimeoutSamplerTest.Data.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeoutSamplerTest {

    interface Data {
        Duration INTERVAL = Duration.ofMillis(100);
        Duration DELTA    = Duration.ofMillis(7);
        int      THREADS  = 1;
        int      COUNT    = 1000;
    }

    private TimeoutSampler target;
    private Instant        now;
    private TimeThread     time;

    @Test
    public void check_N_returnsTrueEveryN() {
        given:
        {
            target = new TimeoutSampler(this::now);
            now    = Instant.now();
        }
        then:
        {
            var valid = 0;

            for (int i = 0; i < COUNT; i++) {

                if (target.check(INTERVAL)) {
                    valid++;
                }

                now = now.plus(DELTA);
            }

            assertEquals(DELTA.toMillis() * COUNT / INTERVAL.toMillis(), valid);
        }
    }

    private Instant now() {
        return now;
    }

    @Test
    public void check_NinSeveralThreads_returnsTrueEveryN() throws InterruptedException {
        given:
        {
            time   = new TimeThread();
            target = new TimeoutSampler(() -> time.now.get());
        }
        when:
        {
            TestThread.runInParallelFor(target, time.running);
            time.start();
        }
        then:
        {
            while (time.running.get()) {
                sleepSafe();
            }

            assertEquals(DELTA.toMillis() * COUNT / INTERVAL.toMillis(), TestThread.VALID.get());
        }
    }

    private static class TimeThread extends Thread {

        private final AtomicBoolean            running = new AtomicBoolean(true);
        private final AtomicReference<Instant> now     = new AtomicReference<>(Instant.now());

        @Override
        public void run() {

            for (var i = 0; i < COUNT; i++) {
                now.set(now.get().plus(DELTA));
                sleepSafe();
            }

            running.set(false);
        }
    }

    private static void sleepSafe() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static class TestThread extends Thread {

        private static final AtomicInteger VALID = new AtomicInteger(0);

        public static void runInParallelFor(TimeoutSampler target, AtomicBoolean running) throws InterruptedException {

            for (int i = 0; i < THREADS; i++) {
                new TestThread(target, running).start();
            }
        }

        private final TimeoutSampler target;
        private final AtomicBoolean  running;

        private TestThread(TimeoutSampler target, AtomicBoolean running) {
            this.target  = target;
            this.running = running;
        }

        @Override
        public void run() {

            while (running.get()) {
                if (target.check(INTERVAL)) {
                    VALID.incrementAndGet();
                }
            }
        }
    }
}
