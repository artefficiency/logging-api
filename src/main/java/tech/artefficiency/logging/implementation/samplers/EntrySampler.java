package tech.artefficiency.logging.implementation.samplers;

import com.google.common.collect.Maps;
import tech.artefficiency.logging.data.handle.Handle;

import java.time.Duration;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

public final class EntrySampler {

    private final Map<Handle, CountSampler>   counts   = Maps.newConcurrentMap();
    private final Map<Handle, TimeoutSampler> timeouts = Maps.newConcurrentMap();

    public boolean check(Handle handle, int count) {
        return check(counts, CountSampler::new, handle, count, CountSampler::check);
    }

    public boolean check(Handle handle, Duration interval) {
        return check(timeouts, TimeoutSampler::new, handle, interval, TimeoutSampler::check);
    }

    private <S, P> boolean check(Map<Handle, S> map, Supplier<S> factory, Handle handle, P param, BiPredicate<S, P> predicate) {
        return predicate.test(map.computeIfAbsent(handle, h -> factory.get()), param);
    }
}