package tech.artefficiency.logging.stubs;

import tech.artefficiency.logging.api.Level;
import tech.artefficiency.logging.data.entries.EntriesContext;
import tech.artefficiency.logging.data.entries.base.BaseEntry;

public final class TestEntry extends BaseEntry {

    public TestEntry(Level level, EntriesContext context) {
        super(level, context);
    }

    public TestEntry(BaseEntry parent) {
        super(parent);
    }
}
