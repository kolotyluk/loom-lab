package net.kolotyluk.loom.steps.lag;

import io.cucumber.java8.En;
import net.kolotyluk.loom.Lag;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;


public class DefiniteDuration implements En {

    Duration minimumDuration = Duration.ofMillis(10);
    Duration maximumDuration = Duration.ofMillis(20);

    Lag definiteLag;
    Duration definiteDuration;

    public DefiniteDuration() {

        Given("a definite Lag", () -> {
            definiteLag = new Lag(minimumDuration);
        });

        When("I compute the definite duration", () -> {
            definiteDuration = definiteLag.getDuration();
        });

        Then("it should equal the minimum duration", () -> {
            assertEquals(minimumDuration, definiteDuration);
        });
    }
}
