package tech.artefficiency.logging.data.fields;

import tech.artefficiency.logging.exceptions.ArgumentNullException;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Field {

    interface Default {
        String NULL = "null";
    }

    private final String           name;
    private final Supplier<Object> value;

    private Function<Object, String> formatter = null;

    public Field(String name, Supplier<Object> value, Function<Object, String> formatter) {

        this.name  = Optional.ofNullable(name)
                .orElseThrow(() -> new ArgumentNullException("name"));
        this.value = Optional.ofNullable(value)
                .orElseThrow(() -> new ArgumentNullException("value"));

        setFormatter(formatter);
    }

    public String name() {
        return name;
    }

    public String value() {
        return Optional.ofNullable(value.get()).map(formatter).orElse(Default.NULL);
    }

    public void setFormatter(Function<Object, String> formatter) {
        this.formatter = x -> Optional.ofNullable(x)
                .map(ensureFormatter(formatter))
                .orElse(Default.NULL);
    }

    private Function<Object, String> ensureFormatter(Function<Object, String> formatter) {
        return Optional.ofNullable(formatter)
                .orElse(Object::toString);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Field field = (Field) o;
        return Objects.equals(name, field.name) && Objects.equals(value(), field.value());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value());
    }

    @Override
    public String toString() {
        return "(Field) Name: %s, Value: %s".formatted(name, value());
    }

    public static Field of(String name, Supplier<Object> value) {
        return new Field(name, value, null);
    }
}
