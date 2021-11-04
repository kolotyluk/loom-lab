package net.kolotyluk.loom;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * https://wiki.openjdk.java.net/display/loom/Getting+started
 */
public class Primes {
    public static void main(String args[]) {
        System.out.println("Hello Primes");
        System.out.println("PID = " + ProcessHandle.current().pid());

        var count = 10_000_000;

        var candidates1 = LongStream.iterate(3, x -> x < count, x -> x + 2);


        var time1 = System.currentTimeMillis();

        var primes1 = candidates1
                .filter(candidate -> isPrime(candidate)).toArray();

        var candidates2 = LongStream.iterate(3, x -> x < count, x -> x + 2);

        var time2 = System.currentTimeMillis();

        var primes2 = candidates2.parallel()
                .filter(candidate -> isPrime(candidate)).toArray();

        var time3 = System.currentTimeMillis();

        var time4 = 0L;
        var time5 = 0L;
        var time6 = 0L;
        var time7 = 0L;

        var platformThreadFactory = Thread.ofPlatform().factory();
        var virtualThreadFactory = Thread.ofVirtual().factory();

        try (var executorService = Executors.newThreadPerTaskExecutor(virtualThreadFactory)) {

            var candidates3 = LongStream.iterate(3, x -> x < count, x -> x + 2);

            time4 = System.currentTimeMillis();

            var primes3 = executorService.submit(() ->
                candidates3.parallel()
                        .filter(candidate -> isPrime(candidate)).toArray()
            ).get();

            time5 = System.currentTimeMillis();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        try (var executorService = Executors.newThreadPerTaskExecutor(virtualThreadFactory)) {

            var candidates3 = LongStream.iterate(3, x -> x < count, x -> x + 2);

            var tasks = candidates3.mapToObj(candidate -> {
                Callable<Optional<Long>> l = () -> {
                    if (isPrime(candidate)) return Optional.of(candidate);
                    else return Optional.empty();
                };
                return l;
            }).collect(Collectors.toList());

            time6 = System.currentTimeMillis();

            var results = executorService.invokeAll(tasks);

            time7 = System.currentTimeMillis();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        var time8 = 0L;
        var time9 = 0L;

        try (var executorService = Executors.newThreadPerTaskExecutor(virtualThreadFactory)) {

            var candidates3 = LongStream.iterate(3, x -> x < count, x -> x + 2);

            time8 = System.currentTimeMillis();

            var tasks = candidates3.mapToObj(candidate -> {
                    return executorService.submit(() -> isPrime(candidate) ? candidate : null);
                }).collect(Collectors.toList());

            executorService.shutdown();
            executorService.awaitTermination(100, TimeUnit.SECONDS);

            time9 = System.currentTimeMillis();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        System.out.println("sequential time = " + (time2 - time1));
        System.out.println("parallel   time = " + (time3 - time2));
        System.out.println("virtual    time = " + (time5 - time4));
        System.out.println("futures1   time = " + (time7 - time6));
        System.out.println("futures2   time = " + (time9 - time8));
    }

    static boolean isPrime(long candidate) {
        if ((candidate & 1) == 0)  // filter out even numbers
            return (candidate == 2);  // except for 2

        var limit = (long) Math.nextUp(Math.sqrt(candidate));

        for (long divisor = 3; divisor <= limit; divisor += 2) {
            if (candidate % divisor == 0) return false;
        }
        return true;
    }
}
