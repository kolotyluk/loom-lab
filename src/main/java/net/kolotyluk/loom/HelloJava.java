package net.kolotyluk.loom;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * https://wiki.openjdk.java.net/display/loom/Getting+started
 */
public class HelloJava {

    public static void main(String args[]){
        System.out.println("Hello World");
        System.out.println("CPU Cores = " + Runtime.getRuntime().availableProcessors());

        try (var executor = Executors.newVirtualThreadExecutor()) {

            // Submits a value-returning task and waits for the result
            Future<String> future = executor.submit(() -> "foo");
            String result = future.join();

            // Submits two value-returning tasks to get a Stream that is lazily populated
            // with completed Future objects as the tasks complete
            Stream<Future<String>> stream = executor.submit(List.of(() -> "foo", () -> "bar"));
            stream.filter(Future::isCompletedNormally)
                    .map(Future::join)
                    .forEach(System.out::println);

            // Executes two value-returning tasks, waiting for both to complete
            List<Future<String>> results1 = executor.invokeAll(List.of(() -> "foo", () -> "bar"));

            // Executes two value-returning tasks, waiting for both to complete. If one of the
            // tasks completes with an exception, the other is cancelled.
            List<Future<String>> results2 = executor.invokeAll(List.of(() -> "foo", () -> "bar"), /*waitAll*/ false);

            // Executes two value-returning tasks, returning the result of the first to
            // complete, cancelling the other.
            String first = executor.invokeAny(List.of(() -> "foo", () -> "bar"));

        } catch (ExecutionException e) {

            e.printStackTrace();
        } catch (InterruptedException e) {

            e.printStackTrace();
        }

        try (ExecutorService executor = Executors.newVirtualThreadExecutor()) {
            IntStream.range(0, 15).forEach(i -> {
                System.out.println("i = " + i + ", Thread ID = " + Thread.currentThread());
                executor.submit(() -> {
                    var thread = Thread.currentThread();
                    System.out.println("Thread ID = " + thread);
                });
            });
        }
    }

}
