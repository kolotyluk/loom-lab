package net.kolotyluk.loom.steps.lag;

import io.cucumber.java8.En;
import net.kolotyluk.loom.Lag;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RandomDuration implements En {

    Duration minimumDuration = Duration.ofMillis(10);
    Duration maximumDuration = Duration.ofMillis(20);

    Lag randomLag;
    Duration randomDuration;

    public RandomDuration() {

        Given("a random Lag", () -> {
            randomLag = new Lag(minimumDuration, maximumDuration);
        });

        When("I compute the random duration", () -> {
            randomDuration = randomLag.getDuration();
        });

        Then("it should not equal either duration", () -> {
            assertNotEquals(minimumDuration, randomDuration);
            assertNotEquals(maximumDuration, randomDuration);
        });

        And("it should not be outside the range", () -> {
            assertTrue(randomDuration.minus(minimumDuration).isPositive());
            assertTrue(randomDuration.minus(maximumDuration).isNegative());
        });
    }

}
