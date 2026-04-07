package tech.artefficiency.logging.configuration;

import tech.artefficiency.logging.api.Level;
import tech.artefficiency.logging.api.StackMode;

import java.util.Set;

public interface Configuration {

    default Entry entry() {
        return new Entry() {
        };
    }

    interface Entry {

        default PatternStyle patternStyle() {
            return PatternStyle.SLF4J;
        }

        enum PatternStyle {
            /**
             * Java's {@link String#format(String, Object...)} style.
             * Placeholders: %s, %d, %f, %t, etc.
             */
            STRING_FORMAT,

            /**
             * SLF4J style (also used by Log4j2, Logback).
             * Placeholders: {} only.
             */
            SLF4J
        }

        default Layer layer() {
            return new Layer() {
            };
        }

        interface Layer {
            default boolean calculateDuration() {
                return true;
            }
        }

        default Exception exception() {
            return new Exception() {
            };
        }

        interface Exception {

            default StackMode stackMode() {
                return StackMode.FAIR;
            }

            default boolean noClass() {
                return false;
            }

            default boolean noMessage() {
                return false;
            }
        }

        default Stack stack() {
            return new Stack() {
            };
        }

        interface Stack {
            default StackMode stackMode() {
                return StackMode.FAIR;
            }
        }

        default Default defaults() {
            return new Default() {
            };
        }

        interface Default {

            default Level level() {
                return Level.INFO;
            }

            default Token token() {
                return new Token() {
                };
            }

            interface Token {

                default String message() {
                    return "No message";
                }

                default String layerName() {
                    return "No layer name";
                }
            }
        }
    }

    default Preprocessors preprocessors() {
        return new Preprocessors() {
        };
    }

    interface Preprocessors {

        default Depth depth() {
            return new Depth() {
            };
        }

        interface Depth {

            default boolean enabled() {
                return true;
            }
        }

        default Masked masked() {
            return new Masked() {
            };
        }

        interface Masked {

            default Set<String> fields() {
                return Set.of("password", "secret");
            }

            default String replacement() {
                return "******";
            }

            default boolean enabled() {
                return true;
            }
        }
    }

    default Compiler compiler() {
        return new Compiler() {
        };
    }

    interface Compiler {

        default boolean printMarks() {
            return true;
        }

        default boolean printOffsets() {
            return true;
        }

        default String offsetToken() {
            return "   ";
        }

        default boolean printDuration() {
            return true;
        }
    }

    default Logger logger() {
        return new Logger() {
        };
    }

    interface Logger {

        default Backend backend() {
            return Backend.SLF4J;
        }

        enum Backend {
            SYSTEM_OUT,
            STANDARD,
            SLF4J
        }
    }
}
