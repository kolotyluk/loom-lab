package net.kolotyluk.loom;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BinaryOperator;

public class Primes {

    public static long[] getPrimes(List<Future<Long>> primes) {
        return primes.stream().mapToLong(p -> {
            try {
                var g = p.get();
                return g != null ? g : -1;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return 0;
        }).filter(n -> n > 0).toArray();
    }

    /**
     * Basic predicate for prime numbers, with capability of simulating network overhead.
     * @param candidate number to test for factors
     * @param minimumLag minimum time in ms to wait simulating network overhead
     * @param maximumLag maximum time in ms to wait simulating network overhead
     * @return true if Prime, false if not
     * @see <a href="https://stackoverflow.com/questions/69842535/is-there-any-benefit-to-thead-onspinwait-while-doing-cpu-bound-work">Is there any benefit to Thead.onSpinWait() while doing CPU Bound work?</a>
     */
    static boolean isPrime(long candidate, long minimumLag, long maximumLag) {

        BinaryOperator<Long> lag = (minimum, maximum) -> {
            if (minimum <= 0 || maximum <= 0) return 0L;
            var approx = (long) Math.nextUp(Math.sqrt(maximum - minimum));
            try {
                var approximateLag = minimum + approx;
                Thread.sleep(approximateLag);
                return approximateLag;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                return 0L;
            }
        };

        try {
            lag.apply(minimumLag, maximumLag);  // Simulate network request overhead

            if (candidate == 2) return true;
            if ((candidate & 1) == 0) return false; // filter out even numbers

            var limit = (long) Math.nextUp(Math.sqrt(candidate));

            for (long divisor = 3; divisor <= limit; divisor += 2) {
                // Thread.onSpinWait(); // If you think this will help, it likely won't
                if (candidate % divisor == 0) return false;
            }

            return true;
        }
        finally {
            lag.apply(minimumLag, maximumLag);  // Simulate network response overhead
        }
    }
}
