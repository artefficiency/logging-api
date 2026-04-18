package tech.artefficiency.logging.data.entries.base;

import org.assertj.core.util.Lists;
import tech.artefficiency.logging.api.Level;
import tech.artefficiency.logging.data.entries.EntriesContext;
import tech.artefficiency.logging.stubs.FieldData;
import tech.artefficiency.logging.tools.Builder;

import java.time.Duration;
import java.util.List;
import java.util.function.BiFunction;

import static tech.artefficiency.logging.tools.Cast.cast;

public class EntryBuilder<T extends BaseEntry> extends Builder<EntryBuilder<T>> {

    private final EntriesContext                       context;
    private final BiFunction<Level, EntriesContext, T> factory;

    private String          name;
    private Level           level;
    private String          pattern;
    private Object[]        parameters;
    private List<FieldData> fields;
    private int             depth;
    private Duration        duration;

    public EntryBuilder(EntriesContext context, BiFunction<Level, EntriesContext, T> factory) {
        this.context = context;
        this.factory = factory;
    }

    public T build() {
        var result = factory.apply(level, context);

        result.setName(name);
        result.setMessage(pattern, parameters);
        result.setDepth(depth);
        result.setDuration(duration);

        if (fields != null) {
            fields.forEach(field ->
                                   result.setField(field.name(),
                                                   cast(field.value()),
                                                   cast(field.formatter())));
        }

        return result;
    }

    public EntriesContext context() {
        return context;
    }

    public String name() {
        return name;
    }

    public Level level() {
        return level;
    }

    public String pattern() {
        return pattern;
    }

    public Object[] parameters() {
        return parameters;
    }

    public List<FieldData> fields() {
        return fields;
    }

    public int depth() {
        return depth;
    }

    public Duration duration() {
        return duration;
    }

    //<editor-fold desc="Setters">
    public EntryBuilder<T> withLevel(Level level) {
        return setAndContinue(level, x -> this.level = x, NullAction.ACCEPT);
    }

    public EntryBuilder<T> withName(String name) {
        return setAndContinue(name, x -> this.name = x, NullAction.ACCEPT);
    }

    public EntryBuilder<T> withPattern(String pattern) {
        return setAndContinue(pattern, x -> this.pattern = x, NullAction.ACCEPT);
    }

    public EntryBuilder<T> withParameters(Object... parameters) {
        return setAndContinue(parameters, x -> this.parameters = x, NullAction.ACCEPT);
    }

    public EntryBuilder<T> withDepth(int depth) {
        return setAndContinue(depth, x -> this.depth = x, NullAction.ACCEPT);
    }

    public EntryBuilder<T> withDuration(Duration duration) {
        return setAndContinue(duration, x -> this.duration = x, NullAction.ACCEPT);
    }

    public EntryBuilder<T> withFields(FieldData... fields) {
        return setAndContinue(fields, x -> this.fields = map(x), NullAction.ACCEPT);
    }

    private List<FieldData> map(FieldData[] fields) {

        if (fields == null || fields.length == 0) {
            return null;
        }

        return Lists.newArrayList(fields);
    }
    //</editor-fold>
}
