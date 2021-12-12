package net.kolotyluk.loom;

import java.time.Duration;
import java.util.function.BinaryOperator;

public class Lag {

    static BinaryOperator<Long> lag = (minimum, maximum) -> {
        if (minimum <= 0 || maximum <= 0) return 0L;
        var approx = (long) Math.nextUp(Math.random() * (maximum - minimum));
        try {
            var approximateLag = minimum + approx;
            Thread.sleep(approximateLag);
            return approximateLag;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return 0L;
        }
    };

    /**
     * Sleep a random amount of time.
     * @param minimum
     * @param maximum
     * @return
     */
    static Duration sleep(Duration minimum, Duration maximum) {
        if (minimum.isZero() && maximum.isZero()) return Duration.ZERO;
        var difference = maximum.minus(minimum);
        var random = Duration.ofNanos((long) Math.nextUp(Math.random() * difference.getNano()));
        var lag = minimum.plus(random);
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
