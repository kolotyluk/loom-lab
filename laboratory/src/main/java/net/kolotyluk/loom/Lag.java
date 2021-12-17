package net.kolotyluk.loom;

import java.time.Duration;
import java.util.function.BinaryOperator;

/**
 * <h1>Induce Lag</h1>
 * <p>
 *     There are many reasons we might want to induce lag into our code, such as for testing, benchmarking,
 *     experimenting, etc. For example
 *     <pre>
 * var simpleLag = new Lag(Duration.ofMillis(1));
 * var randomLag = new Lag(Duration.ofMillis(1), Duration.ofMillis(10));
 * . . .
 * simpleLag.sleep();  // sleep for 1 millisecond
 * randomLag.sleep();  // sleep randomly between 1 and 10 milliseconds
 *     </pre>
 * </p>
 * @param minimum Duration to wait
 * @param maximum Duration to wait
 */
public record Lag(Duration minimum, Duration maximum) {

    Lag(Duration duration) {
        this(duration,duration);
    }

    /**
     * Call {@link Thread#sleep(Duration)} for the chosen duration.
     * @return Duration of time chosen to sleep for.
     * @throws IllegalArgumentException if maximum is less than minimum
     */
    Duration sleep() {
        if (minimum.isZero() && maximum.isZero()) return Duration.ZERO;
        var difference = maximum.minus(minimum);
        if (difference.isNegative()) throw new IllegalArgumentException("maximum is less than minimum");
        var lag = difference.isZero()
                ? minimum
                : minimum.plus(Duration.ofNanos((long) Math.nextUp(Math.random() * difference.getNano())));
        try {
            Thread.sleep(lag);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            return lag;
        }
    }

}
