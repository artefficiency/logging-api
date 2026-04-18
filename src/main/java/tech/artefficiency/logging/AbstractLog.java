package tech.artefficiency.logging;

import tech.artefficiency.logging.api.Level;
import tech.artefficiency.logging.api.Log;
import tech.artefficiency.logging.api.LogApi;
import tech.artefficiency.logging.api.Message;
import tech.artefficiency.logging.configuration.Configuration;
import tech.artefficiency.logging.data.entries.EntriesContext;
import tech.artefficiency.logging.data.entries.base.BaseEntry;
import tech.artefficiency.logging.data.handle.Handle;
import tech.artefficiency.logging.dummy.Dummy;
import tech.artefficiency.logging.exceptions.ArgumentNullException;
import tech.artefficiency.logging.implementation.logger.EntryLogger;
import tech.artefficiency.logging.implementation.samplers.EntrySampler;
import tech.artefficiency.logging.tools.stack.StackHelper;
import tech.artefficiency.logging.tools.stack.StackHelperApi;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class AbstractLog<T extends Message> implements Log<T>, EntriesContext {

    static EntrySampler   sampler = new EntrySampler();
    static StackHelperApi stack   = new StackHelper().withKnown(AbstractLog.class);

    protected static void addKnownToStackHelper(Class<?>... knownClasses) {
        Arrays.stream(knownClasses).forEach(stack::withKnown);
    }

    private final String                        loggerName;
    private final Class<T>                      messageClass;
    private final Function<String, EntryLogger> loggerFactory;

    private EntryLogger logger;

    protected AbstractLog(String loggerName, Class<T> messageClass, Function<String, EntryLogger> loggerFactory) {
        this.loggerName    = Optional.ofNullable(loggerName).orElseThrow(() -> new ArgumentNullException("loggerName"));
        this.messageClass  = Optional.ofNullable(messageClass).orElseThrow(() -> new ArgumentNullException("messageClass"));
        this.loggerFactory = Optional.ofNullable(loggerFactory).orElse(EntryLogger::new);
    }

    //<editor-fold desc="Unit test seams">
    String loggerName() {
        return loggerName;
    }

    Class<T> messageClass() {
        return messageClass;
    }
    //</editor-fold>

    @Override
    public LogApi.LevelSetter<T> every(int hits) {
        return level -> checkAndCreateLayer(level, x -> sampler.check(x, hits));
    }

    @Override
    public LogApi.LevelSetter<T> every(Duration timeout) {
        return level -> checkAndCreateLayer(level, x -> sampler.check(x, timeout));
    }

    @Override
    public LogApi.LevelSetter<T> every(Supplier<Boolean> predicate) {
        return level -> checkAndCreateLayer(level, x -> predicate.get());
    }

    @Override
    public T level(Level level) {
        return checkAndCreateLayer(level, null);
    }

    private T dummy() {
        return Dummy.proxyFor(messageClass);
    }

    private EntryLogger logger() {
        if (logger == null && EntryLogger.isInitialized()) {
            logger = loggerFactory.apply(loggerName);
        }

        return logger;
    }

    private T checkAndCreateLayer(Level level, Predicate<Handle> check) {

        if (logger() == null || !logger().isEnabled(level)) {
            return dummy();
        }

        if (check != null && !check.test(getHandle())) {
            return dummy();
        }

        return createMessage(level, messageClass);
    }

    @Override
    public void accept(BaseEntry entry) {
        logger().accept(entry);
    }

    @Override
    public Configuration.Entry entryConfiguration() {
        return logger().configuration().entry();
    }

    protected abstract T createMessage(Level level, Class<T> messageClass);

    private Handle getHandle() {
        return Handle.of(stack.getFirstUnknownFrame());
    }
}
