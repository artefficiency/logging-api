package tech.artefficiency.logging.exceptions;

public class ArgumentNullException extends RuntimeException {

    public ArgumentNullException(String name) {
        super("Argument '" + name + "' cannot be null");
    }
}
