package tech.artefficiency.logging.tools;

public class Cast {

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object instance) {
        return (T) instance;
    }
}
