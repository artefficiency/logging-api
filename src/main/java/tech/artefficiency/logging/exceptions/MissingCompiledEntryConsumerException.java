package tech.artefficiency.logging.exceptions;

public class MissingCompiledEntryConsumerException extends RuntimeException {

    public MissingCompiledEntryConsumerException(Class<?> compiledDataClass) {
        super("No consumer for " + compiledDataClass + " registered");
    }
}
