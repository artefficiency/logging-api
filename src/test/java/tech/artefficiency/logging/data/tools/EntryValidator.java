package tech.artefficiency.logging.data.tools;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import tech.artefficiency.logging.tools.Builder;
import tech.artefficiency.logging.tools.SerializableGetter;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.*;
import static tech.artefficiency.logging.tools.Cast.cast;
import static tech.artefficiency.logging.tools.SerializableGetter.getMethodName;

public class EntryValidator<T> extends Builder<EntryValidator<T>> {

    private final Map<String, BaseRule<T, ?>> rules = Maps.newHashMap();

    public void validate(T instance) {
        rules.values().forEach(rule -> rule.validate(instance));
    }

    public <V> ExpectedValueSetter<V> on(SerializableGetter<T, V> getter) {
        return new ExpectedValueSetter<>(getter);
    }

    private <V> void addEqualityRule(SerializableGetter<T, V> getter, V expected) {
        var            name = getMethodName(getter);
        BaseRule<T, ?> rule;

        if (expected instanceof Iterable<?> expectedValues) {
            rule = new IterableRule<>(cast(getter), expectedValues);
        } else if (isArray(expected)) {
            rule = new ArrayRule<>(cast(getter), (Object[]) expected);
        } else {
            rule = new ValueRule<>(cast(getter), expected);
        }

        rules.put(name, rule);
    }

    private <V> void addPredicateRule(SerializableGetter<T, V> getter, Predicate<V> predicate) {
        var                 name = getMethodName(getter);
        PredicateRule<T, V> rule = new PredicateRule<>(getter, predicate);

        rules.put(name, rule);
    }

    private boolean isArray(Object value) {
        return Optional.ofNullable(value).map(x -> x.getClass().isArray()).orElse(false);
    }

    public final class ExpectedValueSetter<V> {

        private final SerializableGetter<T, V> getter;

        public ExpectedValueSetter(SerializableGetter<T, V> getter) {
            this.getter = getter;
        }

        public EntryValidator<T> expectingNull() {
            return expecting((V) null);
        }

        public EntryValidator<T> expecting(V expected) {
            return EntryValidator.this.setAndContinue(getter, get -> addEqualityRule(get, expected), NullAction.THROW);
        }

        public EntryValidator<T> expecting(Predicate<V> predicate) {
            return EntryValidator.this.setAndContinue(getter, get -> addPredicateRule(get, predicate), NullAction.THROW);
        }
    }

    private static final class PredicateRule<T, V> extends BaseRule<T, V> {

        private final Predicate<V> predicate;

        private PredicateRule(SerializableGetter<T, V> getter, Predicate<V> predicate) {
            super(getter);
            this.predicate = predicate;
        }

        @Override
        public void validate(T instance) {
            assertThat(predicate.test(actual(instance)))
                    .withFailMessage(() -> failureMessage(true, false))
                    .isTrue();
        }
    }

    private static final class IterableRule<T> extends BaseRule<T, Iterable<?>> {

        private final Iterable<?> expected;

        private IterableRule(SerializableGetter<T, Iterable<?>> getter, Iterable<?> expected) {
            super(getter);
            this.expected = expected;
        }

        @Override
        public void validate(T instance) {
            var set = Sets.newHashSet(expected);

            for (var element : actual(instance)) {
                if (!set.remove(element)) {
                    fail(failureMessage(null, element));
                }
            }

            for (var element : set) {
                fail(failureMessage(element, null));
            }
        }
    }

    private static final class ArrayRule<T> extends BaseRule<T, Object[]> {

        private final Object[] expected;

        private ArrayRule(SerializableGetter<T, Object[]> getter, Object[] expected) {
            super(getter);
            this.expected = expected;
        }

        @Override
        public void validate(T instance) {
            var actual = actual(instance);

            assertThat(expected.length)
                    .withFailMessage(() -> failureMessage(expected.length, actual.length))
                    .isEqualTo(actual.length);

            for (int i = 0; i < expected.length; i++) {
                assertThat(expected[i])
                        .withFailMessage("Wrong element at %s".formatted(i))
                        .isEqualTo(actual[i]);
            }
        }
    }

    private static final class ValueRule<T> extends BaseRule<T, Object> {

        private final Object expected;

        private ValueRule(SerializableGetter<T, Object> getter, Object expected) {
            super(getter);
            this.expected = expected;
        }

        @Override
        public void validate(T instance) {
            assertThat(actual(instance))
                    .withFailMessage(() -> failureMessage(expected, actual(instance)))
                    .isEqualTo(expected);
        }
    }

    private static abstract class BaseRule<T, V> {

        private final SerializableGetter<T, V> getter;

        private BaseRule(SerializableGetter<T, V> getter) {
            this.getter = getter;
        }

        protected V actual(T instance) {
            return getter.apply(instance);
        }

        public abstract void validate(T instance);

        protected String failureMessage(Object expected, Object actual) {
            return """
                    Validation failed for field : %s
                        Expected                : %s
                        Actual                  : %s
                    """.formatted(getMethodName(getter), expected, actual);
        }
    }
}