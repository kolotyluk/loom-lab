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

    Duration minimumDuration = Duration.ofMillis(10);
    Duration maximumDuration = Duration.ofMillis(20);


    @Test
    void durationDefinite() {
        var lag = new Lag(minimumDuration);
        var computedDuration = lag.getDuration();
        assertEquals(minimumDuration, computedDuration);
    }

    @Test
    void durationIndefinite() {
        var lag = new Lag(minimumDuration, maximumDuration);
        var computedDuration = lag.getDuration();
        assertNotEquals(minimumDuration, computedDuration);
        assertNotEquals(maximumDuration, computedDuration);
        assertTrue(computedDuration.minus(minimumDuration).isPositive());
        assertTrue(computedDuration.minus(maximumDuration).isNegative());
    };

    /**
     * <h1>Sleep Tests</h1>
     * <p>
     *     There are many ways to deal with interrupted threads, but one feature of {@link Lag#sleep(Consumer)} is that
     *     we can pass in an interrupt handler so we don't have to use all the try-catch-InterruptedException
     *     boilerplate.
     * </p>
     * @throws InterruptedException
     */
    @Test
    void sleepWithInterruptHandler() throws Exception {
        logger.info("Testing sleepWithInterruptHandler");

        Supplier task = () -> {System.out.println("task evaluated"); return "called";};
        logger.debug("Lazy logging {}", () -> "called");
        // logger1.atDebug().addArgument(() -> "called").log("Lazy logging {}");

        AtomicInteger value = new AtomicInteger();

        assertEquals(0, value.get());

        var lag = new Lag(minimumDuration, maximumDuration);

        var withInterruptHandler = new WithInterruptHandler(value, lag);

        var regularThread = new Thread(withInterruptHandler);
        regularThread.start();

        // Wait a little time, but not after our task ends...
        Thread.sleep(minimumDuration.dividedBy(2));
        assertEquals(1, value.get());

        // Wait for our task to end...
        regularThread.join();
        assertEquals(3, value.get());

        var interruptedThread = new Thread(withInterruptHandler);
        interruptedThread.start();
        Thread.sleep(Duration.ofMillis(5));

        assertEquals(1, value.get());

        interruptedThread.interrupt();

        Thread.sleep(Duration.ofMillis(15));
        assertEquals(2, value.get());
    }

    @Test
    void sleepWithoutInterruptHandler() throws InterruptedException {
        logger.info("Testing sleepWithoutInterruptHandler");

        AtomicInteger value = new AtomicInteger();

        assertEquals(0, value.get());

        var lag = new Lag(minimumDuration, maximumDuration);

        var withoutInterruptHandler = new WithoutInterruptHandler(value, lag);

        var thread3 = new Thread(withoutInterruptHandler);
        thread3.start();
        thread3.join();

        assertEquals(3, value.get());

        var thread4 = new Thread(withoutInterruptHandler);
        thread4.start();

        Thread.sleep(minimumDuration.multipliedBy(2));
        thread4.interrupt();
        thread4.join();

        logger.debug("value = {}", () -> value.get());

        assertEquals(2, value.get());
    }

    record WithInterruptHandler(AtomicInteger value, Lag lag) implements Runnable {
        @Override
        public void run() {
            value.set(1);
            lag.sleep(cause -> value.set(2));
            // if (! Thread.interrupted()) value.set(2);
            if (value.get() == 1) value.set(3);
        }
    }

    record WithoutInterruptHandler(AtomicInteger value, Lag lag) implements Runnable {
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
