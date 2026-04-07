package tech.artefficiency.logging.dummy;

import com.google.common.collect.Maps;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import static tech.artefficiency.logging.tools.Cast.cast;

public final class Dummy implements InvocationHandler {

    static final class Default {
        public static final byte    BYTE    = 0;
        public static final short   SHORT   = 0;
        public static final int     INT     = 0;
        public static final long    LONG    = 0;
        public static final float   FLOAT   = 0;
        public static final double  DOUBLE  = 0;
        public static final boolean BOOLEAN = false;
        public static final char    CHAR    = 0;
    }

    private static final Map<Class<?>, Object> dummyProxies = Maps.newConcurrentMap();

    public static <T> T proxyFor(Class<?> targetClass) {
        return cast(dummyProxies.computeIfAbsent(targetClass, x -> new Dummy().toProxy(x)));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (method.getName().equals("toString")) {
            return "(Dummy) hash: %s".formatted(super.hashCode());
        } else {
            return resultFor(method.getReturnType());
        }
    }

    public Object toProxy(Class<?> targetClass) {
        return Proxy.newProxyInstance(targetClass.getClassLoader(), new Class<?>[]{targetClass}, this);
    }

    private static Object resultFor(Class<?> resultClass) {
        if (resultClass.isPrimitive()) {
            return resultForPrimitive(resultClass);
        } else {
            return resultForClass(resultClass);
        }
    }

    private static Object resultForPrimitive(Class<?> primitiveClass) {
        if (primitiveClass == byte.class) {
            return Default.BYTE;
        } else if (primitiveClass == short.class) {
            return Default.SHORT;
        } else if (primitiveClass == int.class) {
            return Default.INT;
        } else if (primitiveClass == long.class) {
            return Default.LONG;
        } else if (primitiveClass == float.class) {
            return Default.FLOAT;
        } else if (primitiveClass == double.class) {
            return Default.DOUBLE;
        } else if (primitiveClass == boolean.class) {
            return Default.BOOLEAN;
        } else if (primitiveClass == char.class) {
            return Default.CHAR;
        } else {
            return null;
        }
    }

    private static Object resultForClass(Class<?> resultClass) {
        if (resultClass.isInterface()) {
            return proxyFor(resultClass);
        } else {
            return null;
        }
    }
}
