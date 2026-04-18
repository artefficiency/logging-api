package tech.artefficiency.logging;

import tech.artefficiency.logging.api.FieldsApi;
import tech.artefficiency.logging.api.Log;
import tech.artefficiency.logging.implementation.logger.EntryLogger;

import java.util.function.Function;

public class FieldsLogTest extends AbstractLogTest<FieldsApi> {

    @Override
    protected Log<FieldsApi> createTarget(String loggerName, Function<String, EntryLogger> loggerFactory) {
        return new FieldsLog(loggerName, loggerFactory);
    }

    @Override
    protected Class<FieldsApi> messageClass() {
        return FieldsApi.class;
    }
}
