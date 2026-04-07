package tech.artefficiency.logging.data.stubs;

import tech.artefficiency.logging.data.fields.Field;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public record FieldData(String name, Supplier<Object> value, Function<Object, String> formatter) {
    public Field asField() {
        return new Field(name, value, formatter);
    }
}
