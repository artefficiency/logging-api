package tech.artefficiency.logging.data.entries.message;

import tech.artefficiency.logging.api.*;
import tech.artefficiency.logging.data.entries.EntriesContext;
import tech.artefficiency.logging.data.entries.base.BaseEntry;
import tech.artefficiency.logging.data.entries.layer.Layer;

public abstract class BaseMessage<M extends BaseMessage<M>> extends BaseEntry implements Message {

    public BaseMessage(BaseEntry parent) {
        super(parent);
    }

    public BaseMessage(Level level, EntriesContext context) {
        super(level, context);
    }

    @Override
    public void add(String pattern, Object... parameters) {
        super.commitWith(pattern, parameters);
    }

    @Override
    public MessageApi.ExceptionFormatter exception(Throwable exception) {
        return new ExceptionMessage(this, exception);
    }

    @Override
    public MessageApi.DefaultAdder putStack(StackMode mode) {
        return new StackMessage(this, mode);
    }

    @Override
    public LayerApi.Starter layer(String name) {
        return new Layer(this, name);
    }
}
