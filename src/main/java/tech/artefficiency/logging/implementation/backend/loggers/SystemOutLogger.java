package tech.artefficiency.logging.implementation.backend.loggers;

import tech.artefficiency.logging.api.Level;
import tech.artefficiency.logging.implementation.backend.BackendLogger;
import tech.artefficiency.logging.implementation.compilers.CompiledEntry;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

public class SystemOutLogger implements BackendLogger {

    private static final class Color {
        public static final String RESET  = "\u001B[0m";
        public static final String BLACK  = "\u001B[30m";
        public static final String RED    = "\u001B[31m";
        public static final String GREEN  = "\u001B[32m";
        public static final String YELLOW = "\u001B[33m";
        public static final String BLUE   = "\u001B[34m";
        public static final String PURPLE = "\u001B[35m";
        public static final String CYAN   = "\u001B[36m";
        public static final String WHITE  = "\u001B[37m";
    }

    private static final Map<Level, String> levelColors = Map.of(
            Level.TRACE, Color.CYAN,
            Level.DEBUG, Color.GREEN,
            Level.INFO, Color.WHITE,
            Level.WARN, Color.YELLOW,
            Level.ERROR, Color.RED
    );

    private final String            name;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss.SSS");

    public SystemOutLogger(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean isEnabled(Level level) {
        return true;
    }

    @Override
    public void write(CompiledEntry layer) {
        System.out.println(prepareMessage(layer.level(), layer.data()));
    }

    private String colorFor(Level level) {
        return Optional.ofNullable(levelColors.get(level)).orElse(Color.WHITE);
    }

    private String prepareMessage(Level level, String message) {
        return colorFor(level) +
               formatter.format(LocalDateTime.now()) +
               " [" +
               Thread.currentThread().getName() +
               "] " +
               message +
               Color.RESET;
    }
}
