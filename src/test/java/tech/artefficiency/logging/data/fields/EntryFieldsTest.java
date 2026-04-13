package tech.artefficiency.logging.data.fields;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.fail;
import static tech.artefficiency.logging.data.fields.EntryFieldsTest.Data.*;

public class EntryFieldsTest {

    interface Data {
        List<Integer>             VALUES       = List.of(1, 2, 3, 4, 5);
        Function<Object, String>  FORMATTER    = x -> String.valueOf((int) x * 10);
        Function<Integer, String> NAME_FACTORY = "Field #%s"::formatted;
    }

    EntryFields result;
    Field[]     expected;

    @Test
    public void ctor_default_returnsEmptyResult() {
        given:
        {
            result = new EntryFields();
        }
        then:
        {
            validate(result);
        }
    }

    @Test
    public void add_defaultFormatter_initializesCorrectFields() {
        given:
        {
            result   = new EntryFields();
            expected = VALUES.stream()
                    .map(value ->
                                 new Field(
                                         NAME_FACTORY.apply(value),
                                         () -> value,
                                         null))
                    .toArray(Field[]::new);
        }
        when:
        {
            VALUES.forEach(value -> result.add(NAME_FACTORY.apply(value), () -> value, null));
        }
        then:
        {
            validate(result, expected);
        }
    }

    @Test
    public void add_customFormatter_initializesCorrectFields() {
        given:
        {
            result   = new EntryFields();
            expected = VALUES.stream()
                    .map(value ->
                                 new Field(
                                         NAME_FACTORY.apply(value),
                                         () -> value,
                                         FORMATTER))
                    .toArray(Field[]::new);
        }
        when:
        {
            VALUES.forEach(value -> result.add(NAME_FACTORY.apply(value), () -> value, FORMATTER));
        }
        then:
        {
            validate(result, expected);
        }
    }

    @Test
    public void add_fields_initializesCorrectFields() {
        given:
        {
            result   = new EntryFields();
            expected = VALUES.stream()
                    .map(value ->
                                 new Field(
                                         NAME_FACTORY.apply(value),
                                         () -> value,
                                         FORMATTER))
                    .toArray(Field[]::new);
        }
        when:
        {
            Arrays.stream(expected).forEach(result::add);
        }
        then:
        {
            validate(result, expected);
        }
    }

    private void validate(EntryFields result, Field... expected) {
        var set = Sets.newHashSet(expected);

        for (var field : result) {
            if (!set.remove(field)) {
                fail("%s is not expected".formatted(field));
            }
        }

        for (var missing : set) {
            fail("%s is missing".formatted(missing));
        }
    }
}
