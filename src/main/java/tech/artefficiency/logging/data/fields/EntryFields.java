package tech.artefficiency.logging.data.fields;

import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static tech.artefficiency.logging.tools.Cast.cast;

public class EntryFields implements Iterable<Field> {

    private final List<Field> fields = Lists.newArrayList();

    public <T> void add(String name, T value, Function<T, String> formatter) {
        add(name, (Supplier<T>) () -> value, formatter);
    }


    public <T> void add(String name, Supplier<T> value, Function<T, String> formatter) {
        add(new Field(name, cast(value), cast(formatter)));
    }

    public <T> void add(Field field) {
        fields.add(field);
    }

    @Override
    public Iterator<Field> iterator() {
        return fields.iterator();
    }
}
