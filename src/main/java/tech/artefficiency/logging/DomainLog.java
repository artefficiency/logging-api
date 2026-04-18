package tech.artefficiency.logging;

import tech.artefficiency.logging.api.Level;
import tech.artefficiency.logging.api.Log;
import tech.artefficiency.logging.api.Message;
import tech.artefficiency.logging.data.entries.DomainMessage;
import tech.artefficiency.logging.implementation.logger.EntryLogger;

import java.util.function.Function;

public class DomainLog<L extends Message> extends AbstractLog<L> {

    static {
        addKnownToStackHelper(DomainLog.class, LoggerClassSetter.class);
    }

    protected DomainLog(String logger, Class<L> messageCLass, Function<String, EntryLogger> loggerFactory) {
        super(logger, messageCLass, loggerFactory);
    }

    @Override
    protected L createMessage(Level level, Class<L> messageClass) {
        return new DomainMessage<>(level, this, messageClass).asProxy();
    }

    public static <L extends Message> LoggerClassSetter<L> within(Class<L> domainClass) {
        return loggerClass -> new DomainLog<>(loggerClass, domainClass, null);
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