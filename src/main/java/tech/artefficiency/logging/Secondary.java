package tech.artefficiency.logging;

import tech.artefficiency.logging.api.Log;
import tech.artefficiency.logging.api.Message;
import tech.artefficiency.logging.configuration.Configuration;
import tech.artefficiency.logging.implementation.logger.EntryLogger;

import java.util.function.Supplier;

public class Secondary {

    private final static Log<LogFields> log = DomainLog.within(LogFields.class).forClass(Secondary.class);

    static {
        EntryLogger.initialize(new Configuration() {
            @Override
            public Logger logger() {
                return new Logger() {
                    @Override
                    public Configuration.Logger.Backend backend() {
                        return Backend.SYSTEM_OUT;
                    }
                };
            }
        });
    }

    public static void main(String[] args){

        System.out.println("[Named fields with message]");
        log.info().withName(()->"test").withValue(4).add("hello");

        System.out.println("[Sampling]");
        log.every(()->false).debug().withValue(5).add("bye");
    }


    public interface LogFields extends Message {
        LogFields withName(Supplier<String> name);
        LogFields withValue(int value);
    }

}
