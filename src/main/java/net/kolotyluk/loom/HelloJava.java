package net.kolotyluk.loom;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * https://wiki.openjdk.java.net/display/loom/Getting+started
 */
public class HelloJava {

    public static void main(String args[]){
        System.out.println("Hello Loom");
        System.out.println("CPU Cores = " + Runtime.getRuntime().availableProcessors());

        try (var executorService = Executors.newVirtualThreadExecutor()) {

            // Submits a value-returning task and waits for the result
            Future<String> future = executorService.submit(() -> "foo");
            String result = future.join();
            System.out.println("Future result = " + result);

            // Submits two value-returning tasks to get a Stream that is lazily populated
            // with completed Future objects as the tasks complete
            Stream<Future<String>> stream = executorService.submit(List.of(() -> "foo", () -> "bar"));
            stream.filter(Future::isCompletedNormally)
                    .map(Future::join)
                    .forEach(System.out::println);

            // Executes two value-returning tasks, waiting for both to complete
            List<Future<String>> results1 = executorService.invokeAll(List.of(() -> "foo", () -> "bar"));

            // Executes two value-returning tasks, waiting for both to complete. If one of the
            // tasks completes with an exception, the other is cancelled.
            List<Future<String>> results2 = executorService.invokeAll(List.of(() -> "foo", () -> "bar"), /*waitAll*/ false);

            // Executes two value-returning tasks, returning the result of the first to
            // complete, cancelling the other.
            String first = executorService.invokeAny(List.of(() -> "foo", () -> "bar"));

        } catch (ExecutionException e) {

            e.printStackTrace();
        } catch (InterruptedException e) {

            e.printStackTrace();
        }

        try (var executor = Executors.newVirtualThreadExecutor()) {
            IntStream.range(0, 15).forEach(i -> {
                System.out.println("i = " + i + ", Thread ID = " + Thread.currentThread());
                executor.submit(() -> {
                    var thread = Thread.currentThread();
                    System.out.println("Thread ID = " + thread);
                });
            });
        }

//        var deadline = Instant.now().plusSeconds(2);
//
//        var threadFactory = Thread.ofVirtual().factory();
//
//        var e = Executors.newSingleThreadExecutor(threadFactory);
//
//        try (var executor = Executors.newThreadPerTaskExecutor(threadFactory)) {
//
//        }
//
//        try (var executor = Executors.newScheduledThreadPool(16, threadFactory)) {
//            var foo = executor.schedule(() -> {}, 1, TimeUnit.SECONDS);
//
//        }
    }

}
