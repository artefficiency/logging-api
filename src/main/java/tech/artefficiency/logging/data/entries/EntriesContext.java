package tech.artefficiency.logging.data.entries;

import tech.artefficiency.logging.configuration.Configuration;
import tech.artefficiency.logging.data.entries.base.BaseEntry;

import java.util.function.Consumer;

public interface EntriesContext extends Consumer<BaseEntry> {

    Configuration.Entry entryConfiguration();
}
