package tech.artefficiency.logging.api;

public interface Message extends LayerApi.NameSetter, MessageApi.StackFormatter, MessageApi.Adder {

    MessageApi.ExceptionFormatter exception(Throwable exception);
}
