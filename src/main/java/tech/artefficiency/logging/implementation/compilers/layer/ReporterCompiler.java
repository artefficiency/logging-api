package tech.artefficiency.logging.implementation.compilers.layer;

import tech.artefficiency.logging.configuration.Configuration;
import tech.artefficiency.logging.data.entries.layer.Layer;
import tech.artefficiency.logging.implementation.compilers.base.BaseTextCompiler;

import java.util.List;
import java.util.function.BiPredicate;

public final class ReporterCompiler extends BaseTextCompiler<Layer.Reporter> {

    public ReporterCompiler(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected List<BiPredicate<StringBuilder, Layer.Reporter>> initializePipeline() {
        return List.of(
                this::printOffset,
                this::printLayerOut,
                this::printName,
                this::printMessage,
                this::printDuration
        );
    }

    private boolean printLayerOut(StringBuilder builder, Layer.Reporter ignored) {
        if (configuration().printMarks()) {
            builder.append(Token.LAYER_OUT);
            return true;
        }
        return false;
    }
}