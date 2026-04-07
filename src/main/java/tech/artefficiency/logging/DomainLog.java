package tech.artefficiency.logging;

import tech.artefficiency.logging.api.Level;
import tech.artefficiency.logging.api.Log;
import tech.artefficiency.logging.api.Message;
import tech.artefficiency.logging.data.entries.DomainMessage;

public class DomainLog<L extends Message> extends AbstractLog<L> {

    static {
        addKnownToStackHelper(DomainLog.class, LoggerClassSetter.class);
    }

    protected DomainLog(String logger, Class<L> messageCLass) {
        super(logger, messageCLass);
    }

    @Override
    protected L createMessage(Level level, Class<L> messageClass) {
        return new DomainMessage<>(level, this, messageClass).asProxy();
    }

    public static <L extends Message> LoggerClassSetter<L> within(Class<L> domainClass) {
        return loggerClass -> new DomainLog<>(loggerClass, domainClass);
    }

    public interface LoggerClassSetter<L extends Message> {

        default Log<L> forCurrentClass() {
            return forClass(StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass());
        }

        default Log<L> forClass(Class<?> loggerClass) {
            return forClass(loggerClass.getName());
        }

        Log<L> forClass(String loggerClass);
    }
}