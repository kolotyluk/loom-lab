package net.kolotyluk.loom.steps.lag;

import io.cucumber.java8.En;

import net.kolotyluk.loom.Lag;
import net.kolotyluk.loom.LagTests;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

import kr.pe.kwonnam.slf4jlambda.LambdaLogger;
import kr.pe.kwonnam.slf4jlambda.LambdaLoggerFactory;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class SleepWithInterruptHandler implements En {
    static final LambdaLogger logger = LambdaLoggerFactory.getLogger(LagTests.class);

    Duration minimumDuration = Duration.ofMillis(10);
    Duration maximumDuration = Duration.ofMillis(20);

    Lag randomLag;
    Duration randomDuration;

    AtomicInteger value = new AtomicInteger();
    Lag lag = new Lag(minimumDuration, maximumDuration);
    Runnable withInterruptHandler = new LagTests.WithInterruptHandler(value, lag);
    Thread regularThread = new Thread(withInterruptHandler);
    Thread interruptedThread = new Thread(withInterruptHandler);

    SleepWithInterruptHandler() {

        // logger.debug("Testing sleepWithInterruptHandler");

        Given("a task with random Lag", () -> {
            assertEquals(0, value.get());
        });

        When("I start it", () -> {
            regularThread.start();
        });

        Then("it should start normally", () -> {
            // Wait a little time, but not after our task ends...
            Thread.sleep(minimumDuration.dividedBy(2));
            assertEquals(1, value.get());
        });

        And("it should complete normally without interrupt", () -> {
            // Wait for our task to end...
            regularThread.join();
            assertEquals(3, value.get());
        });

//        Given("another task with random Lag", () -> {
//            assertEquals(0, value.get());
//        });
//
//        When("I start it too", () -> {
//            interruptedThread.start();
//        });
//
//        Then("it should start normally as well", () -> {
//            // Wait a little time, but not after our task ends...
//            Thread.sleep(minimumDuration.dividedBy(2));
//            assertEquals(1, value.get());
//        });
//
//        And("it should complete prematurely when interrupted", () -> {
//            interruptedThread.interrupt();
//            interruptedThread.join();
//            assertEquals(2, value.get());
//        });

    }

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
        logger.debug("Testing sleepWithInterruptHandler");

        Supplier task = () -> {System.out.println("task evaluated"); return "called";};
        logger.debug("Lazy logging {}", () -> "called");
        // logger1.atDebug().addArgument(() -> "called").log("Lazy logging {}");

        AtomicInteger value = new AtomicInteger();

        assertEquals(0, value.get());

        var lag = new Lag(minimumDuration, maximumDuration);

        var withInterruptHandler = new LagTests.WithInterruptHandler(value, lag);

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


}
