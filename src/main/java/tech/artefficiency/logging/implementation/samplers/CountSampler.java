package tech.artefficiency.logging.implementation.samplers;

import java.util.concurrent.atomic.AtomicInteger;

public final class CountSampler {

    private static final class Constant {
        public static final int INITIAL = -1;
        public static final int DELTA   = 1;
    }

    private final AtomicInteger samples = new AtomicInteger(Constant.INITIAL);

    public Boolean check(int count) {
        return samples.accumulateAndGet(Constant.DELTA, (cur, one) -> cur + one >= count ? 0 : cur + one) == 0;
    }
}