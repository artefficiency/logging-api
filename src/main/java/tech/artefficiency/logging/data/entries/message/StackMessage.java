package tech.artefficiency.logging.data.entries.message;

import tech.artefficiency.logging.api.MessageApi;
import tech.artefficiency.logging.api.StackMode;
import tech.artefficiency.logging.data.entries.base.BaseEntry;
import tech.artefficiency.logging.data.stack.StackInfo;
import tech.artefficiency.logging.tools.stack.StackHelper;
import tech.artefficiency.logging.tools.stack.StackHelperApi;

import java.util.Optional;

public class StackMessage extends BaseEntry implements MessageApi.DefaultAdder {

    static StackHelperApi STACK_GETTER = new StackHelper()
            .withKnown(BaseEntry.class)
            .withKnown(MessageApi.StackFormatter.class);

    private final StackInfo stackInfo;

    public StackMessage(BaseEntry parent, StackMode stackMode) {
        super(parent);

        stackMode = Optional.ofNullable(stackMode)
                .orElse(defaultStackMode());

        this.stackInfo = extractStackinfo(stackMode);
    }

    public StackInfo stackInfo() {
        return stackInfo;
    }

    private StackMode defaultStackMode() {
        return configuration().stack().stackMode();
    }

    private StackInfo extractStackinfo(StackMode stackMode) {

        return StackHelper.toInfo(STACK_GETTER.getUnknown())
                .mode(stackMode);
    }

    @Override
    public void add(String pattern, Object... parameters) {
        commitWith(pattern, parameters);
    }
}
