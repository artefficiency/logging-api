package tech.artefficiency.logging.implementation.preprocessors.elements;

import tech.artefficiency.logging.configuration.Configuration;
import tech.artefficiency.logging.data.entries.base.BaseEntry;
import tech.artefficiency.logging.data.entries.layer.Layer;
import tech.artefficiency.logging.implementation.preprocessors.base.BasePreprocessor;

import java.util.Stack;
import java.util.function.Consumer;

public class DepthPreprocessor extends BasePreprocessor<Configuration.Preprocessors.Depth> {

    private final static ThreadLocal<Stack<Element>> pending = new ThreadLocal<>();

    public DepthPreprocessor(Configuration configuration, Consumer<BaseEntry> nextConsumer) {
        super(configuration, c -> c.preprocessors().depth(), nextConsumer);
    }

    private Stack<Element> pending() {
        var result = pending.get();

        if (result == null) {
            pending.set(result = new Stack<>());
        }

        return result;
    }

    private int depth() {
        return pending().size();
    }

    @Override
    protected boolean preprocess(BaseEntry entry) {
        return switch (entry) {
            case Layer layer -> preprocessLayer(layer);
            case Layer.Reporter reporter -> preprocessReporter(reporter);
            default -> preprocessEntry(entry);
        };
    }

    protected boolean preprocessLayer(Layer layer) {
        pending().add(new Element(layer));
        return false;
    }

    protected boolean preprocessReporter(Layer.Reporter reporter) {
        var element = pending().pop();

        if (element.pending) {
            if (!reporter.isSkipped()) {
                proceedPending();
                passToNext(reporter.flatten());
            }
            return false;
        } else {
            reporter.setDepth(element.layer.depth());
            return true;
        }
    }

    protected boolean preprocessEntry(BaseEntry entry) {

        entry.setDepth(depth());
        proceedPending();

        return true;
    }

    private boolean pendingProceeded() {
        return pending().isEmpty() || !pending().getLast().pending;
    }

    private void proceedPending() {

        if (pendingProceeded()) {
            return;
        }

        for (Element element : pending()) {
            element.proceed();
        }
    }

    @Override
    protected boolean enabled() {
        return configuration().enabled();
    }

    private final class Element {

        private final Layer   layer;
        private       boolean pending = true;

        private Element(Layer layer) {
            (this.layer = layer).setDepth(depth());
        }

        public void proceed() {
            if (pending) {
                passToNext(layer);
                pending = false;
            }
        }
    }
}
