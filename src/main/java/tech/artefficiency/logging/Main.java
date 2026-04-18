package tech.artefficiency.logging;

import tech.artefficiency.logging.api.FieldsApi;
import tech.artefficiency.logging.api.Log;
import tech.artefficiency.logging.api.StackMode;
import tech.artefficiency.logging.configuration.Configuration;
import tech.artefficiency.logging.implementation.logger.EntryLogger;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public class Main {
    static Log<FieldsApi> log = FieldsLog.forCurrentClass();

    static {
        EntryLogger.initialize(new Configuration() {
            @Override
            public Logger logger() {
                return new Logger() {
                    @Override
                    public Configuration.Logger.Backend backend() {
                        return Backend.SYSTEM_OUT;
                    }
                };
            }
        });
    }

    private final static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);

        }
    }

    private static void stacked(int depth) {
        if (depth == 0) {
            throw new NotFoundException("");
        }

        stacked(--depth);
    }

    public static void main(String[] args) {

        Exception e = null;

        try{
            stacked(10);
        }catch (Exception exception){
            e = exception;
        }

        log.error().exception(e).noMessage().stackMode(StackMode.FAIR).add(new Object());

        try(var l1 = log.info().layer("l1").start()){
            try(var l2 = log.info().layer("l2").start()){
                try(var l3 = log.info().layer("l3").start()){
                    try(var l4 = log.info().layer("l4").start()){
                        l4.report("success");
                    }
                }
            }
        }

        try(var l1 = log.info().layer("l1").start()){
            l1.skip();
        }

        try(var l1 = log.info().layer("l1").start()){
            log.info().add("Plain message");
            l1.skip();
        }


//        System.out.println("[Put stack]");
//        log.trace().putStack(StackMode.FAIR).add();
//
//        System.out.println("[Just message]");
//        log.trace().add("Message");
//
//        System.out.println("[Message with parameters]");
//        log.debug().add("Message with %s at %s", 4, LocalDateTime.now());
//
//        System.out.println("[Stack + message with parameters]");
//        log.info().putStack().add("Message with %s at %s", 4, LocalDateTime.now());
//
//        System.out.println("[Stack]");
//        log.warn().putStack().add();
//
//        System.out.println("[Exception + message with parameters]");
//        log.error().exception(new RuntimeException("Some exception")).add("Message with %s at %s", 4, LocalDateTime.now());
//
//        System.out.println("[Exception full stack + message with parameters]");
//        log.error().exception(new RuntimeException("Some exception")).fullStack().add("Message with %s at %s", 4, LocalDateTime.now());
//
//        System.out.println("[Exception no stack + message with parameters]");
//        log.trace().exception(new RuntimeException("Some exception")).noStack().add("Message with %s at %s", 4, LocalDateTime.now());
//
//        System.out.println("[Exception no class + message with parameters]");
//        log.debug().exception(new RuntimeException("Some exception")).noClass().add("Message with %s at %s", 4, LocalDateTime.now());
//
//        System.out.println("[Exception no message + message with parameters]");
//        log.info().exception(new RuntimeException("Some exception")).noMessage().add("Message with %s at %s", 4, LocalDateTime.now());
//
//        System.out.println("[Exception]");
//        log.error().exception(new RuntimeException("Some exception")).add();
//
//        System.out.println("[Field + message with parameters]");
//        log.error().field("one").set(()->1).add("Message with %s at %s", 4, LocalDateTime.now());
//
//        System.out.println("[Fields + message with parameters]");
//        log.trace().field("one").set(UUID::randomUUID).field("two").set(()-> Duration.ofSeconds(2)).add("Message with %s at %s", 4, LocalDateTime.now());
//
//        System.out.println("[Layer top with name]");
//        try (var layer = log.debug().layer("test").start()) {
//
//            System.out.println("[Layer bottom report null]");
//            layer.report(null);
//        }
//
//        System.out.println("[Layer top for method]");
//        try (var layer = log.debug().methodLayer().start()) {
//
//            log.warn().add("In-layer warning message");
//
//            System.out.println("[Layer bottom report new Object()]");
//            layer.report(new Object());
//        }
//
//        try (var l1 = log.debug().field("f1").set(1).field("f2").set(()->2).layer("l1").start("Layer l1 started")) {
//            try (var l2 = log.debug().field("f2").set(()->2).field("f3").set(()->3).layer("l2").start("Layer l2 started")) {
//                try (var l3 = log.debug().field("f3").set(()->3).field("f4").set(()->4).layer("l4").start("Layer l3 started")) {
//                    log.warn().add("In-layer warning message");
//                    l3.report(3);
//                }
//                l2.report(2);
//            }
//            l1.report(1);
//        }
//
//        System.out.println("[Secret field]");
//        log.warn().field("password").set(()->"hello").add("In-layer warning message");
//
//
//        System.out.println("[Three layers with last skipped]");
//        try (var l1 = log.debug().field("f1").set(()->1).field("f2").set(()->2).layer("l1").start("Layer l1 started")) {
//            try (var l2 = log.debug().field("f2").set(()->2).field("f3").set(()->3).layer("l2").start("Layer l2 started")) {
//                try (var l3 = log.debug().field("f3").set(()->3).field("f4").set(()->4).layer("l4").start("Layer l3 started")) {
//                    l3.skip();
//                }
//                l2.report(2);
//            }
//            l1.report(1);
//        }
//
//        System.out.println("[Skipped layers]");
//        try (var l1 = log.debug().field("f1").set(()->1).field("f2").set(()->2).layer("l1").start("Layer l1 started")) {
//            try (var l2 = log.debug().field("f2").set(()->2).field("f3").set(()->3).layer("l2").start("Layer l2 started")) {
//                try (var l3 = log.debug().field("f3").set(()->3).field("f4").set(()->4).layer("l4").start("Layer l3 started")) {
//                    l3.skip();
//                }
//                l2.skip();
//            }
//            l1.skip();
//        }
//
//        System.out.println("[Three skipped layers with message on the second layer]");
//        try (var l1 = log.debug().field("f1").set(()->1).field("f2").set(()->2).layer("l1").start("Layer l1 started")) {
//            try (var l2 = log.debug().field("f2").set(()->2).field("f3").set(()->3).layer("l2").start("Layer l2 started")) {
//                try (var l3 = log.debug().field("f3").set(()->3).field("f4").set(()->4).layer("l4").start("Layer l3 started")) {
//                    l3.skip();
//                }
//                log.warn().add("In-layer warning message");
//                l2.skip();
//            }
//            l1.skip();
//        }
//
//        System.out.println("[No found]");
//        try (var l1 = log.debug().field("f1").set(1).field("f2").set(2).layer("l1").start("Layer l1 started")) {
//            l1.reportInfo("No found");
//        }
//
//        System.out.println("[Data without mesasge]");
//        try (var l1 = log.debug().layer("l1").start(LocalDateTime.now(), Instant.now())) {
//            l1.reportWarn("result");
//        }
//
//        System.out.println("[No report]");
//        try (var ignored = log.info().layer("ensureSellersAndInitializeResolver").start()) {
//            try (var ignored1 = log.info().layer("ensureSellersAndInitializeResolver").start()) {
//
//                log.info().add("Sellers resolved : %s", 4);
//            }
//            try (var ignored1 = log.info().layer("corrupted").start()) {
//
//                log.info().add("Sellers resolved : %s", 4);
//            }
//        }
    }

}
