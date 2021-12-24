package net.kolotyluk.loom;

import java.time.Duration;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <h1>Induced Lag</h1>
 * <p>
 *     There are many reasons we might want to induce lag into our code, such as for testing, benchmarking,
 *     experimenting, etc. For example
 *     <pre>
 * var definiteLag = new Lag(Duration.ofMillis(1));
 * var randomLag   = new Lag(Duration.ofMillis(1), Duration.ofMillis(10));
 * . . .
 * definiteLag.sleep(); // sleep for 1 millisecond
 * randomLag.sleep();   // sleep randomly between 1 and 10 milliseconds
 * randomLag.sleep(cause -> Throw cause); // handle interrupt
 *     </pre>
 *     But, we don't <em>always</em> want to deal with the normal try-catch-InterruptedException.
 * </p>
 * @param minimum Duration to wait
 * @param maximum Duration to wait
 */
public record Lag(Duration minimum, Duration maximum) {
    static Logger logger = LoggerFactory.getLogger(Lag.class);

    /**
     * Construct a Lag with a definite Duration.
     * @param duration
     */
    public Lag(Duration duration) {
        this(duration,duration);
    }

    /**
     * Compute a random Duration, unless it's definite.
     * @return new duration.
     */
    public Duration getDuration() {
        if (minimum.isZero() && maximum.isZero()) return Duration.ZERO;
        final var difference = maximum.minus(minimum);
        if (difference.isNegative()) throw new IllegalArgumentException("maximum is less than minimum");
        return difference.isZero()
                ? minimum
                : minimum.plus(Duration.ofNanos((long) Math.nextUp(Math.random() * difference.getNano())));
    }

    /**
     * <p>
     *     Call {@link Thread#sleep(Duration)} for the chosen duration.
     * </p>
     * <p>
     *     Note: this does not throw InterruptedException.
     * </p>
     * @return Duration of time chosen to sleep for.
     * @throws IllegalArgumentException if maximum is less than minimum
     */
    public Duration sleep() { return sleep(null); }

    /**
     * <p>
     * Call {@link Thread#sleep(Duration)} for the chosen duration and handle any InterruptedException if,
     * and only if an exceptionHandler is given.
     * <pre>
     * </p>
     * lag.sleep(cause -> Throw cause);
     * </pre>
     * @param  exceptionHandler
     * @return Duration of time chosen to sleep for.
     * @throws IllegalArgumentException if maximum is less than minimum
     */
    public Duration sleep(Consumer<InterruptedException> exceptionHandler) {
        final var duration = getDuration();
        try {
            if (Thread.currentThread().isInterrupted()) duration.minus(duration);
            else Thread.sleep(duration);
        } catch (InterruptedException interruptedException) {
            if (exceptionHandler == null) {
                logger.atDebug().log("ignoring interrupt, interrupted == {}", Thread.currentThread().isInterrupted());
                // logger.debug("ignoring interrupt, interrupted is " + Thread.currentThread().isInterrupted());
//                System.out.println("Lag: ignoring interrupt, interrupted = " + Thread.currentThread().isInterrupted());
                Thread.currentThread().interrupt();
            }
            else
                exceptionHandler.accept(interruptedException);
        }
        finally {
            return duration;
        }
    }


}
