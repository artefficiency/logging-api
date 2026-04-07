package tech.artefficiency.logging.data.entries;

import tech.artefficiency.logging.api.FieldsApi;
import tech.artefficiency.logging.api.Level;
import tech.artefficiency.logging.data.entries.base.BaseEntry;
import tech.artefficiency.logging.data.entries.layer.Layer;
import tech.artefficiency.logging.data.entries.message.BaseMessage;

public class FieldsMessage extends BaseMessage<FieldsMessage> implements FieldsApi {

    private FieldsMessage(BaseEntry parent) {
        super(parent);
    }

    public FieldsMessage(Level level, EntriesContext context) {
        super(level, context);
    }

    @Override
    public ValueSetter field(String name) {
        return value -> {
            setField(name, value, null);
            return this;
        };
    }

    public static FieldsMessage of(Layer layer, Layer.Reporter reporter) {
        var result = new FieldsMessage(layer);

        if (reporter != null) {
            result.merge(reporter);
        }

        return result;
    }
}
