package tech.artefficiency.logging.implementation.backend;

import tech.artefficiency.logging.api.Level;

public interface Logger {

    String name();

    boolean isEnabled(Level level);
}
