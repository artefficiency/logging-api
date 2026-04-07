package tech.artefficiency.logging.data.entries;

import tech.artefficiency.logging.api.Level;
import tech.artefficiency.logging.api.Message;
import tech.artefficiency.logging.data.entries.message.BaseMessage;
import tech.artefficiency.logging.tools.FieldNameHelper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Supplier;

import static tech.artefficiency.logging.tools.Cast.cast;

public class DomainMessage<Domain extends Message> extends BaseMessage<DomainMessage<Domain>> implements InvocationHandler {

    private final Class<Domain> domainClass;

    public DomainMessage(Level level, EntriesContext context, Class<Domain> domainClass) {
        super(level, context);

        this.domainClass = domainClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(domainClass)) {

            var field = FieldNameHelper.getFieldNameFor(method);
            var value = extractFieldValue(args);

            if (value instanceof Supplier<?> supplier) {
                setField(field, supplier, null);
            } else {
                setField(field, () -> value, null);
            }

            return proxy;
        } else {
            return method.invoke(this, args);
        }
    }

    public Domain asProxy() {
        return cast(Proxy.newProxyInstance(domainClass.getClassLoader(), new Class<?>[]{domainClass}, this));
    }

    private Object extractFieldValue(Object[] args) {
        Object result = null;

        if (args != null && args.length > 0) {
            result = args[0];
        }

        return result;
    }
}
