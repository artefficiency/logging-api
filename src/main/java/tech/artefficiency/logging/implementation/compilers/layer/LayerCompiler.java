package tech.artefficiency.logging.implementation.compilers.layer;

import tech.artefficiency.logging.configuration.Configuration;
import tech.artefficiency.logging.data.entries.layer.Layer;
import tech.artefficiency.logging.implementation.compilers.base.BaseTextCompiler;

import java.util.List;
import java.util.function.BiPredicate;

public final class LayerCompiler extends BaseTextCompiler<Layer> {

    public LayerCompiler(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected List<BiPredicate<StringBuilder, Layer>> initializePipeline() {
        return List.of(
                this::printOffset,
                this::printLayerIn,
                this::printName,
                this::printMessage,
                this::printFields,
                this::printDuration
        );
    }

    private boolean printLayerIn(StringBuilder builder, Layer ignored) {
        if (configuration().printMarks()) {
            builder.append(Token.LAYER_IN);
            return true;
        }
        return false;
    }
}