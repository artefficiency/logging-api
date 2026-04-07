package tech.artefficiency.logging.implementation.backend;

import tech.artefficiency.logging.implementation.compilers.CompiledEntry;

public interface BackendLogger extends Logger {

    void write(CompiledEntry entry);
}
