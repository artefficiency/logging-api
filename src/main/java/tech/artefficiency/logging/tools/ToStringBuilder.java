package tech.artefficiency.logging.tools;

import org.apache.commons.lang3.StringUtils;

import java.io.Closeable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ToStringBuilder {

    interface Token {
        String TAB          = " ".repeat(4);
        String VALUE_OFFSET = TAB.repeat(5);
        String SEPARATOR    = ": ";
    }

    private final PropertyScope propertyScope = new PropertyScope();
    private final ValueSetter   valueSetter   = new ValueSetter();

    private final StringBuilder builder = new StringBuilder();
    private       int           offset  = 0;

    public PropertyScope withProperty(String name) {
        return propertyScope.initialize(name);
    }

    public ValueSetter property(String name) {
        return valueSetter.initialize(name);
    }

    private void print(String name, List<?> values) {

        var namePrinted = false;

        if (values.isEmpty()) {
            printName(name, true);
        } else {
            for (var value : values) {

                if (namePrinted) {
                    builder.append(Token.VALUE_OFFSET).append(Token.SEPARATOR);
                } else {
                    printName(name, false);
                    namePrinted = true;
                }

                builder.append(value).append(System.lineSeparator());
            }
        }
    }

    private void printName(String name, boolean completeLine) {
        var tabs   = Token.TAB.repeat(offset);
        var width  = Token.VALUE_OFFSET.length() - tabs.length();
        var padded = StringUtils.rightPad(name, width);

        builder.append(tabs).append(padded).append(Token.SEPARATOR);

        if (completeLine) {
            builder.append(System.lineSeparator());
        }
    }

    @Override
    public String toString() {
        return builder.toString();
    }

    public final class PropertyScope implements Closeable {

        PropertyScope initialize(String name) {

            printName(name, true);
            offset++;

            return this;
        }

        @Override
        public void close() {
            offset--;
        }
    }

    public final class ValueSetter {

        private String name;

        ValueSetter initialize(String name) {
            this.name = name;
            return this;
        }

        public void set(Object value) {
            set(List.of(Optional.ofNullable(value).orElse("null")));
        }

        public void set(Object[] values) {
            if (values == null || values.length == 0) {
                set(List.of("[]"));
            } else {
                set(Arrays.stream(values).map(x -> x == null ? "null" : x.toString()).toList());
            }
        }

        private void set(List<?> values) {
            print(name, values);
        }
    }
}
