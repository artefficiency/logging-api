package tech.artefficiency.logging.exceptions;

public class EntryLoggerNotInitializedException extends RuntimeException {

    public EntryLoggerNotInitializedException() {
        super("Entry logger not initialized. Call EntryLogger.initialize(Configuration)!");
    }
}
