package tech.artefficiency.logging.data.entries.base;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import tech.artefficiency.logging.api.Level;
import tech.artefficiency.logging.configuration.Configuration;
import tech.artefficiency.logging.data.entries.EntriesContext;
import tech.artefficiency.logging.data.fields.EntryFields;
import tech.artefficiency.logging.data.fields.Field;
import tech.artefficiency.logging.exceptions.ArgumentNullException;
import tech.artefficiency.logging.tools.formatter.Formatter;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class BaseEntry {

    private final EntriesContext context;

    private String      name;
    private Level       level;
    private String      message;
    private EntryFields fields;

    private int      depth    = 0;
    private Duration duration = null;

    protected BaseEntry(BaseEntry parent) {
        this(parent.level, parent.context);

        this.name     = parent.name;
        this.fields   = parent.fields;
        this.depth    = parent.depth;
        this.duration = parent.duration;
        this.message  = parent.message;
    }

    protected BaseEntry(Level level, EntriesContext context) {

        this.context = Optional.ofNullable(context)
                .orElseThrow(() -> new ArgumentNullException("context"));

        this.level = Optional.ofNullable(level)
                .orElseGet(() -> context.entryConfiguration().defaults().level());
    }

    public String name() {
        return name;
    }

    public Level level() {
        return level;
    }

    public String message() {
        return message;
    }

    public Iterable<Field> fields() {
        return Optional.ofNullable((Iterable<Field>) fields)
                .orElse(List.of());
    }

    protected Configuration.Entry configuration() {
        return context.entryConfiguration();
    }

    protected void setLevel(Level level) {
        if (level != null) {
            this.level = level;
        }
    }

    protected void setName(String name) {
        if (name != null) {
            this.name = name;
        }
    }

    public int depth() {
        return depth;
    }

    public void setDepth(int depth) {
        if (depth >= 0) {
            this.depth = depth;
        }
    }

    public Duration duration() {
        return duration;
    }

    protected void setDuration(Duration duration) {
        if (duration != null) {
            this.duration = duration;
        }
    }

    <T> void setField(String name, Supplier<T> value) {
        setField(name, value, null);
    }

    protected <T> void setField(String name, Supplier<T> value, Function<T, String> formatter) {

        if (name == null) {
            throw new ArgumentNullException("name");
        }

        if (value == null) {
            throw new ArgumentNullException("value");
        }

        if (fields == null) {
            fields = new EntryFields();
        }

        fields.add(name, value, formatter);
    }

    protected void setMessage(String pattern, Object[] parameters) {
        if (StringUtils.isNotBlank(pattern) || ArrayUtils.isNotEmpty(parameters)) {
            this.message = Formatter
                    .formatter(configuration().patternStyle())
                    .process(pattern, parameters);
        }
    }

    protected void commit() {
        context.accept(this);
    }

    protected void commitWith(String pattern, Object... parameters) {
        setMessage(pattern, parameters);
        commit();
    }

    protected void merge(BaseEntry entry) {

        if (entry == null) {
            return;
        }

        mergeLevel(entry.level);
        mergeName(entry.name);
        mergeMessage(entry.message);
        mergeFields(entry.fields);
        mergeDepth(entry.depth);
        mergeDuration(entry.duration);
    }

    private void mergeLevel(Level level) {
        merge(level, () -> this.level, x -> this.level = x, this::doMergeLevel);
    }

    private Level doMergeLevel(Level local, Level remote) {
        return local.value() > remote.value() ? local : remote;
    }

    private void mergeName(String name) {
        merge(name, () -> this.name, x -> this.name = x, this::doMergeName);
    }

    private String doMergeName(String local, String remote) {
        return Optional.ofNullable(local).orElse(remote);
    }

    private void mergeMessage(String message) {
        merge(message, () -> this.message, x -> this.message = x, this::doMergeMessage);
    }

    private String doMergeMessage(String local, String remote) {
        var result = local;

        if (result != null) {
            result += " ";
        }

        return result + remote;
    }

    private void mergeFields(EntryFields fields) {
        merge(fields, () -> this.fields, x -> this.fields = x, this::doMergeFields);
    }

    private EntryFields doMergeFields(EntryFields local, EntryFields remote) {

        if (local == remote) {
            return local;
        }

        for (var field : remote) {
            local.add(field);
        }

        return local;
    }

    private void mergeDepth(int depth) {
        merge(depth, () -> this.depth, x -> this.depth = x, this::doMergeDepth);
    }

    private int doMergeDepth(int local, int remote) {
        return Math.max(local, remote);
    }

    private void mergeDuration(Duration duration) {
        merge(duration, () -> this.duration, x -> this.duration = x, this::doMergeDuration);
    }

    private Duration doMergeDuration(Duration local, Duration remote) {
        return local.compareTo(remote) > 0 ? local : remote;
    }

    private <T> void merge(T value, Supplier<T> getter, Consumer<T> setter, BiFunction<T, T, T> doMerge) {
        if (value == null) {
            return;
        }

        var localValue = getter.get();

        if (localValue == null) {
            setter.accept(value);
            return;
        }

        setter.accept(doMerge.apply(localValue, value));
    }
}
