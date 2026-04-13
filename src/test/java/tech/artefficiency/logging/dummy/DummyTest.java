package tech.artefficiency.logging.dummy;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.artefficiency.logging.dummy.DummyTest.Data.*;

public class DummyTest {

    interface Data {
        String BYTE_EXPECTED   = "0";
        String SHORT_EXPECTED  = "0";
        String INT_EXPECTED    = "0";
        String LONG_EXPECTED   = "0";
        String FLOAT_EXPECTED  = "0.0";
        String DOUBLE_EXPECTED = "0.0";
        String BOOL_EXPECTED   = "false";
        String CHAR_EXPECTED   = "\u0000";
    }

    interface TestInterface {
        void voidMethod();

        byte byteMethod();

        short shortMethod();

        int intMethod();

        long longMethod();

        float floatMethod();

        double doubleMethod();

        boolean booleanMethod();

        char charMethod();

        String stringMethod();

        TestInterface nestedInterfaceMethod();

        Object objectMethod();

        SomeClass classMethod();
    }

    static class SomeClass {
    }

    TestInterface proxy;
    Object        result;
    String        stringResult;

    @Test
    public void proxyFor_interface_returnsProxy() {
        when:
        {
            proxy = Dummy.proxyFor(TestInterface.class);
        }
        then:
        {
            assertThat(proxy).isNotNull();
        }
    }

    @Test
    public void proxyFor_sameClass_returnsCachedInstance() {
        when:
        {
            TestInterface proxy1 = Dummy.proxyFor(TestInterface.class);
            TestInterface proxy2 = Dummy.proxyFor(TestInterface.class);
            result = proxy1 == proxy2 ? Boolean.TRUE : Boolean.FALSE;
        }
        then:
        {
            assertThat(result).isEqualTo(Boolean.TRUE);
        }
    }

    @Test
    public void invoke_voidMethod_doesNotThrow() {
        given:
        {
            proxy = Dummy.proxyFor(TestInterface.class);
        }
        when:
        {
            proxy.voidMethod();
        }
    }

    @Test
    public void invoke_byteMethod_returnsZero() {
        given:
        {
            proxy = Dummy.proxyFor(TestInterface.class);
        }
        when:
        {
            result = proxy.byteMethod();
        }
        then:
        {
            assertThat(result).isEqualTo(Byte.parseByte(BYTE_EXPECTED));
        }
    }

    @Test
    public void invoke_shortMethod_returnsZero() {
        given:
        {
            proxy = Dummy.proxyFor(TestInterface.class);
        }
        when:
        {
            result = proxy.shortMethod();
        }
        then:
        {
            assertThat(result).isEqualTo(Short.parseShort(SHORT_EXPECTED));
        }
    }

    @Test
    public void invoke_intMethod_returnsZero() {
        given:
        {
            proxy = Dummy.proxyFor(TestInterface.class);
        }
        when:
        {
            result = proxy.intMethod();
        }
        then:
        {
            assertThat(result).isEqualTo(Integer.parseInt(INT_EXPECTED));
        }
    }

    @Test
    public void invoke_longMethod_returnsZero() {
        given:
        {
            proxy = Dummy.proxyFor(TestInterface.class);
        }
        when:
        {
            result = proxy.longMethod();
        }
        then:
        {
            assertThat(result).isEqualTo(Long.parseLong(LONG_EXPECTED));
        }
    }

    @Test
    public void invoke_floatMethod_returnsZero() {
        given:
        {
            proxy = Dummy.proxyFor(TestInterface.class);
        }
        when:
        {
            result = proxy.floatMethod();
        }
        then:
        {
            assertThat(result).isEqualTo(Float.parseFloat(FLOAT_EXPECTED));
        }
    }

    @Test
    public void invoke_doubleMethod_returnsZero() {
        given:
        {
            proxy = Dummy.proxyFor(TestInterface.class);
        }
        when:
        {
            result = proxy.doubleMethod();
        }
        then:
        {
            assertThat(result).isEqualTo(Double.parseDouble(DOUBLE_EXPECTED));
        }
    }

    @Test
    public void invoke_booleanMethod_returnsFalse() {
        given:
        {
            proxy = Dummy.proxyFor(TestInterface.class);
        }
        when:
        {
            result = proxy.booleanMethod();
        }
        then:
        {
            assertThat(result).isEqualTo(Boolean.parseBoolean(BOOL_EXPECTED));
        }
    }

    @Test
    public void invoke_charMethod_returnsZeroChar() {
        given:
        {
            proxy = Dummy.proxyFor(TestInterface.class);
        }
        when:
        {
            result = proxy.charMethod();
        }
        then:
        {
            assertThat(result).isEqualTo(CHAR_EXPECTED.charAt(0));
        }
    }

    @Test
    public void invoke_stringMethod_returnsNull() {
        given:
        {
            proxy = Dummy.proxyFor(TestInterface.class);
        }
        when:
        {
            result = proxy.stringMethod();
        }
        then:
        {
            assertThat(result).isNull();
        }
    }

    @Test
    public void invoke_nestedInterfaceMethod_returnsNestedProxy() {
        given:
        {
            proxy = Dummy.proxyFor(TestInterface.class);
        }
        when:
        {
            result = proxy.nestedInterfaceMethod();
        }
        then:
        {
            assertThat(result).isNotNull();
        }
    }

    @Test
    public void invoke_nestedInterfaceMethod_returnsCachedInstance() {
        given:
        {
            proxy = Dummy.proxyFor(TestInterface.class);
        }
        when:
        {
            TestInterface nested1 = proxy.nestedInterfaceMethod();
            TestInterface nested2 = proxy.nestedInterfaceMethod();
            result = nested1 == nested2 ? Boolean.TRUE : Boolean.FALSE;
        }
        then:
        {
            assertThat(result).isEqualTo(Boolean.TRUE);
        }
    }

    @Test
    public void invoke_objectMethod_returnsNull() {
        given:
        {
            proxy = Dummy.proxyFor(TestInterface.class);
        }
        when:
        {
            result = proxy.objectMethod();
        }
        then:
        {
            assertThat(result).isNull();
        }
    }

    @Test
    public void invoke_classMethod_returnsNull() {
        given:
        {
            proxy = Dummy.proxyFor(TestInterface.class);
        }
        when:
        {
            result = proxy.classMethod();
        }
        then:
        {
            assertThat(result).isNull();
        }
    }

    @Test
    public void invoke_toString_returnsDummyHash() {
        given:
        {
            proxy = Dummy.proxyFor(TestInterface.class);
        }
        when:
        {
            stringResult = proxy.toString();
        }
        then:
        {
            assertThat(stringResult).matches("\\(Dummy\\) hash: -?\\d+");
        }
    }

    @Test
    public void invoke_chainedMethods_returnProxies() {
        given:
        {
            proxy = Dummy.proxyFor(TestInterface.class);
        }
        when:
        {
            result = proxy.nestedInterfaceMethod().intMethod();
        }
        then:
        {
            assertThat(result).isEqualTo(Integer.parseInt(INT_EXPECTED));
        }
    }
}
