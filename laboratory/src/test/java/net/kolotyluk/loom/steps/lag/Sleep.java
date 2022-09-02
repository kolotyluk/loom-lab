package net.kolotyluk.loom.steps.lag;

import io.cucumber.java8.En;
import kr.pe.kwonnam.slf4jlambda.LambdaLogger;
import kr.pe.kwonnam.slf4jlambda.LambdaLoggerFactory;
import net.kolotyluk.loom.Lag;
import net.kolotyluk.loom.LagTests;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @see <a href="https://github.com/cucumber/cucumber-expressions#readme">Cucumber Expressions</a>
 */
public class Sleep implements En {
    static final LambdaLogger logger = LambdaLoggerFactory.getLogger(LagTests.class);

    Duration minimumDuration = Duration.ofMillis(10);
    Duration maximumDuration = Duration.ofMillis(20);

    Lag lag = new Lag(minimumDuration, maximumDuration);;
    AtomicInteger value = new AtomicInteger();
    Thread thread;

    public Sleep() {

        // logger.debug("Testing sleepWithInterruptHandler");

        Given("a task {word} interrupt handler", (String handler) -> {
            var task = handler.contains("without")
                    ? new LagTests.WithoutInterruptHandler(value, lag)
                    : new LagTests.WithInterruptHandler(value, lag);
            thread = new Thread(task);
            assertEquals(0, value.get());
        });

        When("a thread is started with it", () -> {
            thread.start();
        });

        Then("the thread should start normally", () -> {
            // Wait a little time, but not after our task ends...
            Thread.sleep(minimumDuration.dividedBy(2));
            assertEquals(1, value.get());
        });

        And("the thread {string} interrupted", (String interrupted) -> {
            if (interrupted.equalsIgnoreCase("is")) {
                Thread.sleep(maximumDuration.minus(minimumDuration).dividedBy(2));
                thread.interrupt();
            }
        });

        And("the thread completes {word}", (String completion) -> {
            // Wait for our task to end...
            thread.join();
            if (completion.contains("prematurely"))
                assertEquals(2, value.get());
            else
                assertEquals(3, value.get());
        });


    }


}
