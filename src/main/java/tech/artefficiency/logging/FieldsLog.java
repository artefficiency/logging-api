package tech.artefficiency.logging;

import tech.artefficiency.logging.api.FieldsApi;
import tech.artefficiency.logging.api.Level;
import tech.artefficiency.logging.api.Log;
import tech.artefficiency.logging.data.entries.FieldsMessage;
import tech.artefficiency.logging.implementation.logger.EntryLogger;

import java.util.function.Function;

public class FieldsLog extends AbstractLog<FieldsApi> {

    static {
        addKnownToStackHelper(FieldsLog.class);
    }

    protected FieldsLog(String logger, Function<String, EntryLogger> loggerFactory) {
        super(logger, FieldsApi.class, loggerFactory);
    }

    public static Log<FieldsApi> forCurrentClass() {
        return forClass(StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass());
    }

    public static Log<FieldsApi> forClass(Class<?> loggerClass) {
        return forClass(loggerClass.getName());
    }

    public static Log<FieldsApi> forClass(String loggerClass) {
        return new FieldsLog(loggerClass, null);
    }

    @Override
    protected FieldsApi createMessage(Level level, Class<FieldsApi> messageClass) {
        return new FieldsMessage(level, this);
    }
}