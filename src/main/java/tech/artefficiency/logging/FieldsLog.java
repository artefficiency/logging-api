package tech.artefficiency.logging;

import tech.artefficiency.logging.api.FieldsApi;
import tech.artefficiency.logging.api.Level;
import tech.artefficiency.logging.api.Log;
import tech.artefficiency.logging.data.entries.FieldsMessage;

public class FieldsLog extends AbstractLog<FieldsApi> {

    static {
        addKnownToStackHelper(FieldsLog.class);
    }

    protected FieldsLog(String logger) {
        super(logger, FieldsApi.class);
    }

    public static Log<FieldsApi> forCurrentClass() {
        return forClass(StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass());
    }

    public static Log<FieldsApi> forClass(Class<?> loggerClass) {
        return forClass(loggerClass.getName());
    }

    public static Log<FieldsApi> forClass(String loggerClass) {
        return new FieldsLog(loggerClass);
    }

    @Override
    protected FieldsApi createMessage(Level level, Class<FieldsApi> messageClass) {
        return new FieldsMessage(level, this);
    }
}