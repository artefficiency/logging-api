package tech.artefficiency.logging.data.entries.layer;

import tech.artefficiency.logging.api.LayerApi;
import tech.artefficiency.logging.api.Level;
import tech.artefficiency.logging.data.entries.FieldsMessage;
import tech.artefficiency.logging.data.entries.base.BaseEntry;
import tech.artefficiency.logging.tools.NanoTime;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

public class Layer extends BaseEntry implements LayerApi.Starter {

    private final long nanoTimestamp;

    public Layer(BaseEntry parent, String name) {
        super(parent);

        initializeName(name);

        if (calculateDuration()) {
            this.nanoTimestamp = System.nanoTime();
        } else {
            this.nanoTimestamp = 0;
        }
    }

    private void initializeName(String name) {

        if (name == null && name() == null) {
            name = defaultName();
        }

        setName(name);
    }

    long nanoTimestamp() {
        return nanoTimestamp;
    }

    private String defaultName() {
        return configuration().defaults().token().layerName();
    }

    private boolean calculateDuration() {
        return configuration().layer().calculateDuration();
    }

    @Override
    public LayerApi.Reporter start(String pattern, Object... parameters) {
        super.commitWith(pattern, parameters);
        return new Reporter();
    }

    public final class Reporter extends BaseEntry implements LayerApi.Reporter {

        private boolean  skip = false;
        private Duration duration;

        Reporter() {
            super(Layer.this);

        }

        @Override
        public void report(Level level, String pattern, Object... parameters) {
            setLevel(level);
            setMessage(pattern, parameters);
        }

        public Duration duration() {
            return duration;
        }

        public boolean isSkipped() {
            return skip;
        }

        @Override
        public void skip() {
            skip = true;
        }

        public FieldsMessage flatten() {
            return FieldsMessage.of(Layer.this, this);
        }

        @Override
        public void close() {
            initializeDuration();
            commit();
        }

        private void initializeDuration() {
            if (calculateDuration()) {
                duration = Duration.ofNanos(System.nanoTime() - nanoTimestamp);
            } else {
                duration = Duration.ZERO;
            }
        }
    }
}
