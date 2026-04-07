package tech.artefficiency.logging.api;

import java.util.function.Supplier;

public interface FieldsApi extends Message {

    ValueSetter field(String name);

    interface ValueSetter {

        default FieldsApi set(Object value) {
            return set(() -> value);
        }

        FieldsApi set(Supplier<Object> value);
    }
}
