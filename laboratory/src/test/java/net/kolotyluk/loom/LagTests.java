package net.kolotyluk.loom;

import org.junit.jupiter.api.Test;

import kr.pe.kwonnam.slf4jlambda.LambdaLogger;
import kr.pe.kwonnam.slf4jlambda.LambdaLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

public class LagTests {
    // static final Logger logger1 = LoggerFactory.getLogger(LagTestsK.class);
    static final LambdaLogger logger = LambdaLoggerFactory.getLogger(LagTests.class);

    public record WithInterruptHandler(AtomicInteger value, Lag lag) implements Runnable {
        @Override
        public void run() {
            value.set(1);
            lag.sleep(cause -> value.set(2));
            // if (! Thread.interrupted()) value.set(2);
            if (value.get() == 1) value.set(3);
        }
    }

    public record WithoutInterruptHandler(AtomicInteger value, Lag lag) implements Runnable {
        @Override
        public void run() {
            var count = 0;
            try {
                value.set(1);
                for (int i = 0; i < 10; i++) {
                    count++;
                    logger.debug("i = {}", i);
                    logger.debug("Thread.currentThread().isInterrupted() = {}", () -> Thread.currentThread().isInterrupted());
                    if (Thread.currentThread().isInterrupted()) {
                        logger.debug("Interrupted value = {}", () -> value.get());
                        value.set(2);
                        logger.debug("            value = {}", () -> value.get());
                        break;
                    }
                    else lag.sleep();
                }
                if (value.get() == 1) value.set(3);
                logger.debug("count = {}", count);
            } catch (Exception e) {
                logger.debug("caught InterruptedException");
            }

        }
    }

}
