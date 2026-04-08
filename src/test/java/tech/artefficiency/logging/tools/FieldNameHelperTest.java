package tech.artefficiency.logging.tools;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FieldNameHelperTest {

    @Test
    public void getFieldNameFor_noPrepositions_returnsExpectedName() {
        check("age", "Age");
    }

    @Test
    public void getFieldNameFor_startingFromPreposition_returnsExpectedName() {
        check("byName", "Name");
    }

    @Test
    public void getFieldNameFor_endingWithPreposition_returnsExpectedName() {
        check("namedAs", "Named");
    }

    @Test
    public void getFieldNameFor_withSeveralPrepositions_returnsExpectedName() {
        check("withNameAs", "Name");
    }

    @Test
    public void getFieldNameFor_nameConsistsOfPrepositionsOnlyButParameterIsNotWellKnown_returnsExpectedName() {
        check("byAs", "NotKnown");
    }

    @Test
    public void getFieldNameFor_nameConsistsOfPrepositionAndParameterIsWellKnown_returnsExpectedName() {
        check("byAs", "NotKnown");
    }

    @Test
    public void getFieldNameFor_singleCharacterField_returnsExpectedName() {
        check("a", "A");
    }

    private void check(String methodName, String expectedName) {
        var method = Arrays.stream(TestInterface.class.getMethods())
                .filter(x -> x.getName().equals(methodName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cannot find method " + methodName));

        assertEquals(expectedName, FieldNameHelper.getFieldNameFor(method));
    }

    interface TestInterface {

        void a(boolean value);

        void age(int value);

        void byName(String name);

        void namedAs(String name);

        void withNameAs(String name);

        void byAs(NotKnown arg);

        void from(Object value);

    }

    interface NotKnown {

    }
}
