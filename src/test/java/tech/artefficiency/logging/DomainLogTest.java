package tech.artefficiency.logging;

import tech.artefficiency.logging.api.Log;
import tech.artefficiency.logging.api.Message;
import tech.artefficiency.logging.implementation.logger.EntryLogger;

import java.util.function.Function;

public class DomainLogTest extends AbstractLogTest<DomainLogTest.DomainFields> {

    @Override
    protected Log<DomainFields> createTarget(String loggerName, Function<String, EntryLogger> loggerFactory) {
        return new DomainLog<>(loggerName, DomainFields.class, loggerFactory);
    }

    @Override
    protected Class<DomainFields> messageClass() {
        return DomainFields.class;
    }

    public interface DomainFields extends Message {

    }
}