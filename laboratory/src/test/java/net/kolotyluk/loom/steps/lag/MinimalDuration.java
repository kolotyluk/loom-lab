package net.kolotyluk.loom.steps.lag;

import io.cucumber.java8.En;
import net.kolotyluk.loom.Lag;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class MinimalDuration implements En {

    Lag minimalLag;
    Duration minimalDuration;

    public MinimalDuration() {

        Given("a minimal Lag", () -> {
            minimalLag = new Lag();
        });

        When("I compute the minimal duration", () -> {
            minimalDuration = minimalLag.getDuration();
        });

        Then("it should equal zero", () -> {
            assertEquals(Duration.ZERO, minimalDuration);
        });
    }
}
