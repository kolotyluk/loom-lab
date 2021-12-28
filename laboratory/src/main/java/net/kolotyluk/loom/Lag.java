package net.kolotyluk.loom;

import java.time.Duration;
import java.util.function.Consumer;

import kr.pe.kwonnam.slf4jlambda.LambdaLogger;
import kr.pe.kwonnam.slf4jlambda.LambdaLoggerFactory;


/**
 * <h1>Induced Lag</h1>
 * <p>
 *     There are many reasons we might want to induce lag into our code, such as for testing, benchmarking,
 *     experimenting, etc. For example
 *     <pre>
 * var minimalLag  = new Lag();
 * var definiteLag = new Lag(Duration.ofMillis(1));
 * var randomLag   = new Lag(Duration.ofMillis(1), Duration.ofMillis(10));
 * . . .
 * minimalLag.sleep();  // call yield() instead of sleep()
 * definiteLag.sleep(); // sleep for 1 millisecond
 * randomLag.sleep();   // sleep randomly between 1 and 10 milliseconds
 * randomLag.sleep(cause -> Throw cause); // handle interrupt
 *     </pre>
 *     But, we don't <em>always</em> want to deal with the normal try-catch-InterruptedException boilerplate.
 *     This boilerplate is required as a consequence of Java
 *     <a href="https://en.wikipedia.org/wiki/Exception_handling#Checked_exceptions">Checked Exceptions</a>.
 *     Without judgement on the utility of Checked Exceptions, this API simply gives the API User an easy way
 *     to bypass Checked Exceptions, and the resulting boilerplate.
 * </p>
 * <h2>Minimal Lag</h2>
 * <p>
 *     Why would we use <em>Minimal Lag</em> when we could just call {@link Thread#yield()} directly?
 * </p>
 * <p>
 *     When we create a Lag object, and pass it as an argument to some other method or function.
 * </p>
 * <h1>Records</h1>
 * <p>
 *     This API uses Java Records rather and a Java Class because
 *     <ol>
 *         <li>
 *             A Java Record is a Java Class
 *         </li>
 *         <li>
 *             But the Java Compiler does more work for us behind the scenes when creating the class
 *             <ul>
 *                 <li>
 *                     There is less boiler place to deal with
 *                 </li>
 *                 <li>
 *                     You can define state with much less effort
 *                 </li>
 *                 <li>
 *                     The objects are immutable, which is much safer when reasoning about concurrency,
 *                     because once the state is defined, it cannot be changed
 *                 </li>
 *                 <li>
 *                     Serialization is safer, which can be important when concurrent applications are
 *                     sending/receiving message over the wire
 *                 </li>
 *             </ul>
 *         </li>
 *     </ol>
 * </p>
 * <h1>Cautionary Tales</h1>
 * <p>
 *     Concurrency, including Java Concurrency, is difficult to reason about and be confident about correctness,
 *     as there are many subtle issues to consider. It took a fair amount of work to get this code correct,
 *     and maybe there are still defects. Indeed, I did not find important defects until I started writing unit
 *     tests, so please look at <tt>LagTests</tt>. This is why I strongly recommend dealing with concurrency via
 *     higher level APIs that have been designed and tested by experts, especially with higher levels of
 *     abstraction that are easier to reason about.
 * </p>
 * <p>
 *     In particular, it is important to read the documentation for {@link Thread#interrupt()} thoroughly
 *     to understand the setting and resetting of the {@link Thread#interrupted()} state. My mistake was I
 *     assumed that when interrupted on {@link Thread#sleep(Duration)}, the interrupted flag would still be
 *     set, but it's actually reset after any blocking operations.
 * </p>
 * @param minimum Duration to wait
 * @param maximum Duration to wait
 */
public record Lag(Duration minimum, Duration maximum) {
    public Lag {
        if (maximum.minus(minimum).isNegative()) throw new IllegalStateException("maximum is less than minimum");
    }

    /**
     * Construct a Lag with a definite Duration.
     * @param duration
     */
    public Lag(Duration duration) {
        this(duration,duration);
    }

    /**
     * Construct a Lag with minimal Duration. Lag.sleep() uses yield() instead of sleep().
     */
    public Lag() {
        this(Duration.ZERO);
    }

    static LambdaLogger logger = LambdaLoggerFactory.getLogger(Lag.class);


    /**
     * Compute a random Duration, unless it's definite.
     * @return new duration.
     */
    public Duration getDuration() {
        if (minimum.isZero() && maximum.isZero()) return Duration.ZERO;
        final var difference = maximum.minus(minimum);
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
        var duration = getDuration();
        try {
            if (Thread.currentThread().isInterrupted())
                duration = Duration.ZERO;
            else if (duration.isZero())
                    Thread.yield();
                else
                    Thread.sleep(duration);
        } catch (InterruptedException interruptedException) {
            if (exceptionHandler == null) {
                logger.debug("ignoring  interrupt, interrupted == {}", () -> Thread.currentThread().isInterrupted());
                // logger.atDebug().log("ignoring interrupt, interrupted == {}", Thread.currentThread().isInterrupted());
                // logger.debug("ignoring interrupt, interrupted is " + Thread.currentThread().isInterrupted());
//                System.out.println("Lag: ignoring interrupt, interrupted = " + Thread.currentThread().isInterrupted());
                Thread.currentThread().interrupt();
                logger.debug("resetting interrupt, interrupted == {}", () -> Thread.currentThread().isInterrupted());
            }
            else
                exceptionHandler.accept(interruptedException);
        }
        catch (Throwable cause) {
            // TODO implement this
        }
        finally {
            return duration;
        }
    }


}
