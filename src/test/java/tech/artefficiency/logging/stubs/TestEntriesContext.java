package tech.artefficiency.logging.stubs;

import org.assertj.core.util.Lists;
import tech.artefficiency.logging.configuration.Configuration;
import tech.artefficiency.logging.data.entries.EntriesContext;
import tech.artefficiency.logging.data.entries.base.BaseEntry;

import java.util.List;

public class TestEntriesContext implements EntriesContext {

    private final TestConfiguration configuration   = new TestConfiguration();
    private final List<BaseEntry>   acceptedEntries = Lists.newArrayList();

    @Override
    public Configuration.Entry entryConfiguration() {
        return configuration.entry();
    }

    @Override
    public void accept(BaseEntry baseEntry) {
        acceptedEntries.add(baseEntry);
    }

    public TestConfiguration configuration() {
        return configuration;
    }

    public List<BaseEntry> acceptedEntries() {
        return acceptedEntries;
    }
}
