package tech.artefficiency.logging.exceptions;

public class NoCompilersConfiguredException extends RuntimeException {
    public NoCompilersConfiguredException() {
        super("No compilers configured for logger");
    }
}
