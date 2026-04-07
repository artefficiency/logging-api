package tech.artefficiency.logging.tools;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class FieldNameHelper {

    interface Constant {
        String NO_NAME_ARGUMENT = "arg0";
        String SPLIT_REGEX      = "([A-Z]?[a-z]*)";
    }

    private static final Set<Class<?>> WELL_KNOWN = new HashSet<>() {{
        add(Boolean.class);
        add(Byte.class);
        add(Character.class);
        add(Short.class);
        add(Integer.class);
        add(Long.class);
        add(Double.class);
        add(Float.class);
        add(Void.class);
        add(Object.class);
        add(LocalDateTime.class);
        add(LocalDate.class);
        add(Duration.class);
        add(Period.class);
    }};

    private static final Set<String>                    PREPOSITIONS = new HashSet<>() {{
        add("with");
        add("at");
        add("for");
        add("in");
        add("on");
        add("since");
        add("over");
        add("till");
        add("until");
        add("from");
        add("during");
        add("while");
        add("as");
        add("past");
        add("after");
        add("before");
        add("by");
        add("across");
        add("along");
        add("round");
        add("around");
        add("down");
        add("into");
        add("though");
        add("to");
        add("up");
        add("above");
        add("under");
        add("behind");
        add("among");
        add("inside");
        add("outside");
    }};
    private final static Map<Method, String>            CACHE        = Maps.newConcurrentMap();
    private final static Pattern                        PATTERN      = Pattern.compile(Constant.SPLIT_REGEX);
    private final static List<Function<Method, String>> STRATEGY     = List.of(

            //-- First get from method name excluding prepositions (null if whole name is preposition(s))
            FieldNameHelper::fromMethodName,

            //-- Then try parameter name (unless parameter name is known, i.e. not arg0)
            FieldNameHelper::fromArgumentName,

            //-- Then try parameter type (unless it is not Object.class)
            FieldNameHelper::fromArgumentType
    );

    public static String getFieldNameFor(Method method) {
        return CACHE.computeIfAbsent(method,
                                     m -> STRATEGY.stream()
                                             .map(x -> x.apply(m))
                                             .filter(Objects::nonNull)
                                             .findFirst()
                                             .orElseGet(() -> capitalize(method.getName())));
    }

    private static String fromMethodName(Method method) {

        var match  = PATTERN.matcher(method.getName());
        var result = new StringBuilder();

        while (match.find()) {

            var group = match.group();

            if (!Strings.isNullOrEmpty(group) && !isPreposition(group)) {
                result.append(capitalize(group));
            }
        }

        if (result.isEmpty()) {
            return null;
        }

        return result.toString();
    }

    private static boolean isPreposition(String token) {
        return PREPOSITIONS.contains(token.toLowerCase());
    }

    private static String capitalize(String token) {
        if (Character.isUpperCase(token.charAt(0))) {
            return token;
        } else {
            return Character.toUpperCase(token.charAt(0)) + token.substring(1);
        }
    }

    private static String fromArgumentName(Method method) {
        return tryFindParameter(method, parameter -> !parameter.getName().equals(Constant.NO_NAME_ARGUMENT))
                .map(Parameter::getName)
                .orElse(null);
    }

    private static String fromArgumentType(Method method) {
        return tryFindParameter(method, parameter -> !isWellKnown(parameter.getType()))
                .map(parameter -> parameter.getType().getSimpleName())
                .orElse(null);
    }

    private static Optional<Parameter> tryFindParameter(Method method, Predicate<Parameter> predicate) {
        return Arrays.stream(method.getParameters())
                .findFirst()
                .filter(predicate);
    }

    private static boolean isWellKnown(Class<?> type) {
        return type.isPrimitive() || WELL_KNOWN.contains(type);
    }
}
