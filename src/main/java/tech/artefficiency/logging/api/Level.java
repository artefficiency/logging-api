package tech.artefficiency.logging.api;

public enum Level {
    ERROR(40),
    WARN(30),
    INFO(20),
    DEBUG(10),
    TRACE(0);

    private final int value;

    Level(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
