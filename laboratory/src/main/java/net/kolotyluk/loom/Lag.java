package net.kolotyluk.loom;

import java.time.Duration;
import java.util.function.BinaryOperator;

public record Lag(Duration minimum, Duration maximum) {

    Lag(Duration duration) {
        this(duration,duration);
    }

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
