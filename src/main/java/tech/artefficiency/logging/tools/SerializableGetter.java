package tech.artefficiency.logging.tools;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

@FunctionalInterface
public interface SerializableGetter<T, R> extends Function<T, R>, Serializable {

    String WRITE_REPLACE_METHOD = "writeReplace";

    static <T, R> String getMethodName(SerializableGetter<T, R> lambda) {

        try {
            var writeReplaceMethod = lambda.getClass().getDeclaredMethod(WRITE_REPLACE_METHOD);
            writeReplaceMethod.setAccessible(true);
            var serializedLambda = (SerializedLambda) writeReplaceMethod.invoke(lambda);

            return serializedLambda.getImplMethodName();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
