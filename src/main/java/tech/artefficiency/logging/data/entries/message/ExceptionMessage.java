package tech.artefficiency.logging.data.entries.message;

import tech.artefficiency.logging.api.MessageApi;
import tech.artefficiency.logging.api.StackMode;
import tech.artefficiency.logging.data.entries.base.BaseEntry;
import tech.artefficiency.logging.data.exception.ExceptionInfo;

public class ExceptionMessage extends BaseEntry implements MessageApi.ExceptionFormatter {

    private final ExceptionInfo.Builder exceptionBuilder;
    private       ExceptionInfo         exceptionInfo;

    public ExceptionMessage(BaseEntry parent, Throwable exception) {
        super(parent);

        this.exceptionBuilder = initializeBuilder(exception);
    }

    public ExceptionInfo exceptionInfo() {
        if (exceptionInfo == null) {
            exceptionInfo = exceptionBuilder.build();
        }

        return exceptionInfo;
    }

    private ExceptionInfo.Builder initializeBuilder(Throwable exception) {
        var result = ExceptionInfo.of(exception)
                .stackMode(configuration().exception().stackMode());

        if (configuration().exception().noClass()) {
            result.noClass();
        }

        if (configuration().exception().noMessage()) {
            result.noMessage();
        }

        return result;
    }

    @Override
    public MessageApi.ExceptionFormatter stackMode(StackMode mode) {

        if (mode != null) {
            this.exceptionBuilder.stackMode(mode);
        }

        return this;
    }

    @Override
    public MessageApi.ExceptionFormatter noMessage() {
        this.exceptionBuilder.noMessage();
        return this;
    }

    @Override
    public MessageApi.ExceptionFormatter noClass() {
        this.exceptionBuilder.noClass();
        return this;
    }

    @Override
    public void add(String pattern, Object... parameters) {
        commitWith(pattern, parameters);
    }
}
