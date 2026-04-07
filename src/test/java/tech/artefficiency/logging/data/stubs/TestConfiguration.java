package tech.artefficiency.logging.data.stubs;

import tech.artefficiency.logging.api.Level;
import tech.artefficiency.logging.api.StackMode;
import tech.artefficiency.logging.configuration.Configuration;

import java.util.Set;

public class TestConfiguration implements Configuration {

    private final TestEntry         entry         = new TestEntry();
    private final TestPreprocessors preprocessors = new TestPreprocessors();
    private final TestCompiler      compiler      = new TestCompiler();
    private final TestLogger        logger        = new TestLogger();

    @Override
    public TestEntry entry() {
        return entry;
    }

    @Override
    public TestPreprocessors preprocessors() {
        return preprocessors;
    }

    @Override
    public TestCompiler compiler() {
        return compiler;
    }

    @Override
    public TestLogger logger() {
        return logger;
    }

    public static final class TestEntry implements Entry {

        private final TestLayer     layer     = new TestLayer();
        private final TestException exception = new TestException();
        private final TestStack     stack     = new TestStack();
        private final TestDefault   defaults  = new TestDefault();

        @Override
        public TestLayer layer() {
            return layer;
        }

        @Override
        public TestException exception() {
            return exception;
        }

        @Override
        public TestStack stack() {
            return stack;
        }

        @Override
        public TestDefault defaults() {
            return defaults;
        }

        public static final class TestLayer implements Layer {

            private boolean calculateDuration = Layer.super.calculateDuration();

            @Override
            public boolean calculateDuration() {
                return calculateDuration;
            }

            public void setCalculateDuration(boolean calculateDuration) {
                this.calculateDuration = calculateDuration;
            }
        }

        public static final class TestException implements Exception {

            private StackMode stackMode = Exception.super.stackMode();
            private boolean   noClass   = Exception.super.noClass();
            private boolean   noMessage = Exception.super.noMessage();

            @Override
            public StackMode stackMode() {
                return stackMode;
            }

            @Override
            public boolean noClass() {
                return noClass;
            }

            @Override
            public boolean noMessage() {
                return noMessage;
            }

            public void setStackMode(StackMode stackMode) {
                this.stackMode = stackMode;
            }

            public void setNoClass(boolean noClass) {
                this.noClass = noClass;
            }

            public void setNoMessage(boolean noMessage) {
                this.noMessage = noMessage;
            }
        }

        public static final class TestStack implements Stack {

            private StackMode stackMode = Stack.super.stackMode();

            @Override
            public StackMode stackMode() {
                return stackMode;
            }

            public void setStackMode(StackMode stackMode) {
                this.stackMode = stackMode;
            }
        }

        public static final class TestDefault implements Default {

            private Level     level = Default.super.level();
            private TestToken token = new TestToken();

            @Override
            public Level level() {
                return level;
            }

            public void setLevel(Level level) {
                this.level = level;
            }

            @Override
            public Token token() {
                return token;
            }

            public static final class TestToken implements Token {

                private String message   = Token.super.message();
                private String layerName = Token.super.layerName();

                @Override
                public String message() {
                    return message;
                }

                @Override
                public String layerName() {
                    return layerName;
                }

                public void setMessage(String message) {
                    this.message = message;
                }

                public void setLayerName(String layerName) {
                    this.layerName = layerName;
                }
            }
        }
    }

    public static final class TestPreprocessors implements Preprocessors {

        private final TestDepth  depth  = new TestDepth();
        private final TestMasked masked = new TestMasked();

        @Override
        public TestDepth depth() {
            return depth;
        }

        @Override
        public TestMasked masked() {
            return masked;
        }

        public static final class TestDepth implements Depth {

            private boolean enabled = Depth.super.enabled();

            @Override
            public boolean enabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }
        }

        public static final class TestMasked implements Masked {

            private Set<String> fields      = Masked.super.fields();
            private String      replacement = Masked.super.replacement();
            private boolean     enabled     = Masked.super.enabled();

            @Override
            public Set<String> fields() {
                return fields;
            }

            @Override
            public String replacement() {
                return replacement;
            }

            @Override
            public boolean enabled() {
                return enabled;
            }

            public void setFields(Set<String> fields) {
                this.fields = fields;
            }

            public void setReplacement(String replacement) {
                this.replacement = replacement;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }
        }
    }

    public static final class TestCompiler implements Compiler {

        private boolean printMarks    = Compiler.super.printMarks();
        private boolean printOffsets  = Compiler.super.printOffsets();
        private String  offsetToken   = Compiler.super.offsetToken();
        private boolean printDuration = Compiler.super.printDuration();

        @Override
        public boolean printMarks() {
            return printMarks;
        }

        @Override
        public boolean printOffsets() {
            return printOffsets;
        }

        @Override
        public String offsetToken() {
            return offsetToken;
        }

        @Override
        public boolean printDuration() {
            return printDuration;
        }

        public void setPrintMarks(boolean printMarks) {
            this.printMarks = printMarks;
        }

        public void setPrintOffsets(boolean printOffsets) {
            this.printOffsets = printOffsets;
        }

        public void setOffsetToken(String offsetToken) {
            this.offsetToken = offsetToken;
        }

        public void setPrintDuration(boolean printDuration) {
            this.printDuration = printDuration;
        }
    }

    public static final class TestLogger implements Logger {

        private Backend backend = Logger.super.backend();

        @Override
        public Backend backend() {
            return backend;
        }

        public void setBackend(Backend backend) {
            this.backend = backend;
        }
    }
}
