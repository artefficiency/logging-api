package tech.artefficiency.logging.api;

import java.time.Duration;
import java.util.function.Supplier;

public interface LogApi {

    interface Sampler<Entry> extends LevelSetter<Entry> {

        LevelSetter<Entry> every(int hits);

        LevelSetter<Entry> every(Duration timeout);

        LevelSetter<Entry> every(Supplier<Boolean> predicate);
    }

    interface LevelSetter<Entry> {

        default Entry error() {
            return level(Level.ERROR);
        }

        default Entry warn() {
            return level(Level.WARN);
        }

        default Entry info() {
            return level(Level.INFO);
        }

        default Entry debug() {
            return level(Level.DEBUG);
        }

        default Entry trace() {
            return level(Level.TRACE);
        }

        Entry level(Level level);
    }
}
