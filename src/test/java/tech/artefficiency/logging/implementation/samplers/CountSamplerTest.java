package tech.artefficiency.logging.implementation.samplers;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.artefficiency.logging.implementation.samplers.CountSamplerTest.Data.*;

public class CountSamplerTest {

    interface Data {
        int HITS    = 8;
        int THREADS = 10;
        int COUNT   = 1000;
    }

    private CountSampler target;

    @Test
    public void check_N_returnsTrueEveryN() {
        given:
        {
            target = new CountSampler();
        }
        then:
        {
            for (int i = 0; i < COUNT; i++) {
                assertEquals(i % HITS == 0, target.check(HITS), "Error on iteration %s".formatted(i));
            }
        }
    }

    @Test
    public void check_NinSeveralThreads_returnsTrueEveryN() throws InterruptedException {
        given:
        {
            target = new CountSampler();
        }
        when:
        {
            TestThread.runInParallelFor(target);
        }
        then:
        {
            assertEquals(THREADS * COUNT / HITS, TestThread.VALID.get());
        }
    }

    private static class TestThread extends Thread {

        private static final AtomicInteger  VALID = new AtomicInteger(0);
        private static       CountDownLatch ACTIVE_THREADS;

        public static void runInParallelFor(CountSampler target) throws InterruptedException {

            ACTIVE_THREADS = new CountDownLatch(THREADS);

            for (int i = 0; i < THREADS; i++) {
                new TestThread(target).start();
            }

            ACTIVE_THREADS.await();
        }

        private final CountSampler target;

        private TestThread(CountSampler target) {
            this.target = target;
        }

        @Override
        public void run() {

            for (var i = 0; i < COUNT; i++) {
                if (target.check(HITS)) {
                    VALID.incrementAndGet();
                }
            }

            ACTIVE_THREADS.countDown();
        }
    }
}
