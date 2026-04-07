package tech.artefficiency.logging.implementation.samplers;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public final class TimeoutSampler {

    private final Supplier<Instant> now;
    private final AtomicLong        lastRun = new AtomicLong(0);

    public TimeoutSampler() {
        this(Instant::now);
    }

    TimeoutSampler(Supplier<Instant> now) {
        this.now = now;
    }

    public Boolean check(Duration interval) {
        var nowMillis      = now.get().toEpochMilli();
        var intervalMillis = interval.toMillis();

        return lastRun.getAndAccumulate(nowMillis, (last, now) -> last + intervalMillis < nowMillis ? getNearestTimestamp(last, nowMillis, intervalMillis) : last) < (nowMillis - intervalMillis);
    }

    private long getNearestTimestamp(long last, long now, long interval) {
        if (last > 0) {
            return now - (now - last) % interval;
        } else {
            return now;
        }
    }
}