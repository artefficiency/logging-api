package tech.artefficiency.logging.tools;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.artefficiency.logging.tools.FieldNameHelperTest.Data.*;

public class FieldNameHelperTest {

    interface Data {
        String AGE                 = "age";
        String AGE_RESULT          = "Age";
        String BY_NAME             = "byName";
        String BY_NAME_RESULT      = "Name";
        String NAMED_AS            = "namedAs";
        String NAMED_AS_RESULT     = "Named";
        String WITH_NAME_AS        = "withNameAs";
        String WITH_NAME_AS_RESULT = "Name";
        String BY_AS               = "byAs";
        String BY_AS_RESULT        = "NotKnown";
        String SINGLE_CHAR         = "a";
        String SINGLE_CHAR_RESULT  = "A";
        String FROM                = "from";
        String FROM_RESULT         = "From";
        String ID                  = "id";
        String ID_RESULT           = "Id";
        String USER_ID             = "userId";
        String USER_ID_RESULT      = "UserId";
        String FOR_USER            = "forUser";
        String FOR_USER_RESULT     = "User";
    }

    @Test
    void getFieldNameFor_noPrepositions_returnsExpectedName() {
        check(AGE, AGE_RESULT);
    }

    @Test
    void getFieldNameFor_startingFromPreposition_returnsExpectedName() {
        check(BY_NAME, BY_NAME_RESULT);
    }

    @Test
    void getFieldNameFor_endingWithPreposition_returnsExpectedName() {
        check(NAMED_AS, NAMED_AS_RESULT);
    }

    @Test
    void getFieldNameFor_withSeveralPrepositions_returnsExpectedName() {
        check(WITH_NAME_AS, WITH_NAME_AS_RESULT);
    }

    @Test
    void getFieldNameFor_nameConsistsOfPrepositionsOnlyButParameterIsNotWellKnown_returnsExpectedName() {
        check(BY_AS, BY_AS_RESULT);
    }

    @Test
    void getFieldNameFor_nameConsistsOfPrepositionAndParameterIsWellKnown_returnsExpectedName() {
        check(FROM, FROM_RESULT);
    }

    @Test
    void getFieldNameFor_singleCharacterField_returnsExpectedName() {
        check(SINGLE_CHAR, SINGLE_CHAR_RESULT);
    }

    @Test
    void getFieldNameFor_camelCaseId_returnsExpectedName() {
        check(ID, ID_RESULT);
    }

    @Test
    void getFieldNameFor_camelCaseUserId_returnsExpectedName() {
        check(USER_ID, USER_ID_RESULT);
    }

    @Test
    void getFieldNameFor_prepositionWithCamelCase_returnsExpectedName() {
        check(FOR_USER, FOR_USER_RESULT);
    }

    private void check(String methodName, String expectedName) {
        var method = Arrays.stream(TestInterface.class.getMethods())
                .filter(x -> x.getName().equals(methodName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cannot find method " + methodName));

        assertThat(FieldNameHelper.getFieldNameFor(method)).isEqualTo(expectedName);
    }

    interface TestInterface {

        void a(boolean value);

        void age(int value);

        void byName(String name);

        void namedAs(String name);

        void withNameAs(String name);

        void byAs(NotKnown arg);

        void from(Object value);

        void id(String value);

        void userId(String value);

        void forUser(String value);
    }

    interface NotKnown {
    }
}
