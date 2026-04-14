# logging-api

A fluent API for structured logging in Java that works with any logging backend (SLF4J, Log4j, Logback, etc.).

## Features

- **Structured Fields** - Capture named fields via method calls or explicit names
- **Layer/Span Tracking** - Track nested operations with visual indentation and duration
- **Stack Trace Control** - Configurable stack trace depth with NONE, FAIR, and FULL modes
- **Field Masking** - Automatic masking of sensitive fields (passwords, secrets)
- **Sampling** - Control log volume with hit-based, time-based, or predicate-based sampling
- **Multiple Backends** - SystemOut, Standard, or SLF4J logging backends
- **Zero-Cost Logging** - Disabled log levels produce no overhead via dummy proxies

## Two Approaches to Structured Logging

| Approach | Use When |
|---------|----------|
| **Domain Interface** | Type-safe fields, compile-time checking, cleaner API |
| **FieldsApi** | Dynamic field names, runtime-determined field names |

### Domain Interface Example

```java
public interface OrderLog extends Message {
    OrderLog orderId(String id);
    OrderLog customerId(String customerId);
    OrderLog amount(double amount);
}

Log<OrderLog> log = DomainLog.within(OrderLog.class).forCurrentClass();

log.info()
    .orderId("ORD-12345")
    .customerId("CUST-001")
    .amount(99.99)
    .add("Order processed");
```

### FieldsApi Example

```java
Log<FieldsApi> log = FieldsLog.forClass(MyService.class);

log.info()
    .field("orderId").set("ORD-12345")
    .field("customerId").set("CUST-001")
    .field("amount").set(99.99)
    .add("Order processed");
```

### Output

Both approaches produce the same structured output:

```
[INFO] Order processed (orderId=ORD-12345,customerId=CUST-001,amount=99.99)
```

## Installation

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>dev.platforma.b2b</groupId>
    <artifactId>platforma-commons</artifactId>
    <version>CURRENT</version>
</dependency>
```

## Quick Start

### 1. Create a Logger

```java
// For domain interface
Log<OrderLog> log = DomainLog.within(OrderLog.class).forCurrentClass();

// For dynamic fields
Log<FieldsApi> log = FieldsLog.forClass(MyService.class);
```

### 2. Log with Structured Fields

```java
// Domain interface
log.info()
    .orderId("ORD-12345")
    .amount(99.99)
    .add("Order placed");

// Dynamic fields
log.info()
    .field("orderId").set("ORD-12345")
    .field("amount").set(99.99)
    .add("Order placed");
```

## Usage Examples

### Basic Logging

```java
// Simple log entry
log.info().add("Server started");

// With parameters
log.info().add("User {} logged in from {}", username, ipAddress);

// Parameters only (no message pattern)
log.info().add("simple value");
log.info().add(42, someObject);

// At different levels
log.trace().add("Detailed trace");
log.debug().add("Debug info");
log.info().add("Info message");
log.warn().add("Warning");
log.error().add("Error occurred");
```

### Structured Fields

Method names become field names, arguments become values:

```java
log.info()
    .userId(userId)
    .action("LOGIN")
    .ipAddress(clientIp)
    .add("User logged in");
```

### Lazy Field Values

Use `Supplier` for values that are expensive to compute. The supplier is only
evaluated if the log entry is actually recorded.

#### Domain Interface

```java
public interface OrderLog extends Message {
    OrderLog orderId(Supplier<String> id);
    OrderLog totalAmount(Supplier<Double> amount);
}

log.info()
    .orderId(() -> fetchOrderIdFromDatabase())
    .totalAmount(() -> calculateTotal())
    .add("Order processed");
```

#### FieldsApi

```java
log.info()
    .field("orderId").set(() -> fetchOrderIdFromDatabase())
    .field("totalAmount").set(() -> calculateTotal())
    .add("Order processed");
```

Both approaches support lazy evaluation, preventing unnecessary computation
when logging is disabled.

### Exception Logging

```java
try {
    processOrder(orderId);
} catch (Exception e) {
    // Default (FAIR mode)
    log.error().exception(e).add("Order processing failed");

    // Without stack trace
    log.warn().exception(e).noStack().add("Minor issue");

    // Full stack trace
    log.error().exception(e).fullStack().add("Critical error");
}
```

### Layer/Span Tracking

Layers provide visual indentation and duration tracking for nested operations.

#### Basic Usage

```java
try (var ignored = log.info().layer("operation").start()) {
    doWork();
}
```

#### Nested Layers with Visual Indentation

```java
try (var l1 = log.info().layer("l1").start()) {
    doWork();
    try (var l2 = log.info().layer("l2").start()) {
        doMoreWork();
        try (var l3 = log.info().layer("l3").start()) {
            doEvenMore();
        }
    }
}
```

**Output:**

```
09:34:25 {+} l1:
09:34:25    {+} l2:
09:34:25       {+} l3:
09:34:27       {-} l3: [PT2.001S]
09:34:27    {-} l2: [PT2.500S]
09:34:27 {-} l1: [PT3.100S]
```

- `{+}` - Layer entry marker
- `{-}` - Layer exit marker with duration

#### Reporting Results

```java
try (var ignored = log.info().layer("query").start()) {
    Result result = executeQuery();
    ignored.report(result);
}
```

#### Skipping Layers

If no logs were written after `start()`, the layer can be skipped entirely:

```java
// Nothing printed - no logs after start()
try (var l = log.info().layer("optional").start()) {
    if (shouldSkip()) {
        l.skip();
    }
}
```

If any log entry was written, `skip()` has no effect:

```java
// Layer logged normally
try (var l = log.info().layer("operation").start()) {
    log.info().add("Something happened");
    l.skip(); // No effect
}
```

### Sampling

Control log volume with sampling strategies:

```java
// Log every 100th occurrence
log.every(100).info().add("Sampled log");

// Log at most once per minute
log.every(Duration.ofMinutes(1)).warn().add("Throttled warning");

// Custom predicate
log.every(() -> isDebugEnabled).debug().add("Conditional debug");
```

### Field Masking

Sensitive fields are automatically masked:

```java
log.info()
    .username("alice")
    .password("secret123")
    .add("User authenticated");
```

**Output:**

```
[INFO] User authenticated (username=alice,password=******)
```

## Configuration

Initialize the logger at application startup:

```java
EntryLogger.initialize(new Configuration() {
    @Override
    public Logger logger() {
        return new Logger() {
            @Override
            public Backend backend() {
                return Backend.SLF4J; // SYSTEM_OUT, STANDARD, or SLF4J
            }
        };
    }
});
```

### Full Configuration Example

```java
EntryLogger.initialize(new Configuration() {

    @Override
    public Entry entry() {
        return new Entry() {
            @Override
            public PatternStyle patternStyle() {
                return PatternStyle.SLF4J; // or STRING_FORMAT
            }

            @Override
            public Layer layer() {
                return new Layer() {
                    @Override
                    public boolean calculateDuration() {
                        return true;
                    }
                };
            }

            @Override
            public Exception exception() {
                return new Exception() {
                    @Override
                    public StackMode stackMode() {
                        return StackMode.FAIR;
                    }
                };
            }

            @Override
            public Default defaults() {
                return new Default() {
                    @Override
                    public Level level() {
                        return Level.INFO;
                    }
                };
            }
        };
    }

    @Override
    public Preprocessors preprocessors() {
        return new Preprocessors() {
            @Override
            public Depth depth() {
                return new Depth() {
                    @Override
                    public boolean enabled() {
                        return true;
                    }
                };
            }

            @Override
            public Masked masked() {
                return new Masked() {
                    @Override
                    public Set<String> fields() {
                        return Set.of("password", "secret", "token");
                    }

                    @Override
                    public String replacement() {
                        return "******";
                    }
                };
            }
        };
    }

    @Override
    public Compiler compiler() {
        return new Compiler() {
            @Override
            public boolean printOffsets() {
                return true;
            }

            @Override
            public String offsetToken() {
                return "   ";
            }
        };
    }
});
```

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         API Layer                               │
│  Log, Message, Level, LogApi, LayerApi, MessageApi              │
└─────────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────────┐
│                    AbstractLog (Base)                           │
│  DomainLog, FieldsLog                                           │
└─────────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────────┐
│                     EntryLogger (Core)                          │
│  EntryPreprocessor → EntryCompiler → Backend                    │
└─────────────────────────────────────────────────────────────────┘
                              │
         ┌────────────────────┼────────────────────┐
         ▼                    ▼                    ▼
   Preprocessors         Compilers            Backends
   - DepthPreprocessor   - EntryCompiler     - Slf4jLogger
   - MaskedPreprocessor  - MessageCompiler   - StandardLogger
                         - LayerCompiler     - SystemOutLogger
```

## License

Apache License 2.0 - see [LICENSE](LICENSE)
