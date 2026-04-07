package tech.artefficiency.logging.exceptions;

public class UnknownEntryClassException extends RuntimeException {

    public UnknownEntryClassException(Class<?> type) {
        super(type + " is not valid entry implementation");
    }
}
