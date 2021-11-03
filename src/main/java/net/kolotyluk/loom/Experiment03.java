package net.kolotyluk.loom;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * <h1>Experiment Suite 03</h1>
 * @see <a href="https://wiki.openjdk.java.net/display/loom/Getting+started">Loom Getting Started</a>
 * @see <a href="https://www.youtube.com/watch?v=Nb85yJ1fPXM">Java ExecutorService - Part 1</a>
 */
public class Experiment03 {

    public static void main(String args[]){
        System.out.println("Fiber Fun - Experiment 3");
        System.out.println("CPU Cores = " + Runtime.getRuntime().availableProcessors() + '\n');

        var virtualThreadFactory = Thread.ofVirtual().factory();


        /* A good, often best practice in Concurrent Programming is to put a time limit or timeout on operations.
         * This is especially true in distributed programming environments, such as when we are calling network
         * APIs, such as sending HTTP Requests, and blocking while we wait for results.
         *
         * A common scenario in Loom is that we might spawn a number of Virtual Threads, where each makes an HTTP
         * Request for some resource, and will block waiting for a result...
         *
         * One way to handle this is with the executorService.awaitTermination() method, where we can define a
         * timeout, block/wait, where the result indicates if the timeout was exceeded. If the timeout has expired,
         * then we can take appropriate action.
         *
         * Note that stopping threads is cooperative, where we do not just stop them, rather we interrupt them,
         * and they can decide to stop or do something else.
         */


//        try (var executorService = Executors.newThreadPerTaskExecutor(virtualThreadFactory)) {
//
//            System.out.println("\n\nresult = " + spawn(executorService, () -> "callable 1", (future) -> {}));
//            System.out.println("\n\nresult = " + spawn(executorService, () -> "callable 2", (future) -> future.cancel(true)));
//            System.out.println("\n\nresult = " + spawn(executorService, () -> "callable 3", (future) -> future.cancel(false)));
//
//            Callable<String> sleeper = () -> {
//                Thread.sleep(Duration.ofSeconds(4));
//                return "sleeper";
//            };
//
//            System.out.println("\n\nresult = " + spawn(executorService, sleeper, (future) -> {}));
//
//            Callable<String> malcontent = () -> {
//                Thread.sleep(Duration.ofSeconds(2));
//                throw new Exception("acting up");
//                //return "malcontent";
//            };
//
//            System.out.println("\n\nresult = " + spawn(executorService, malcontent, (future) -> {}));
//
//
//            // Submits a value-returning task and waits for the result
//            var callableFuture = executorService.submit(callable);
//            var runnableFuture = executorService.submit(runnable);
//
//            callableFuture.cancel(true);
//            callableFuture.cancel(false);
//
//            try {
//                var callableFutureResult = callableFuture.get(3, TimeUnit.SECONDS);
//                var runnableFutureResult = runnableFuture.get(3, TimeUnit.SECONDS);
//            }
//            catch (TimeoutException e) {
//                // The timeout expired...
//                e.printStackTrace();
//            }
//            catch (ExecutionException e) {
//                // Exception was thrown
//                e.printStackTrace();
//            }
//            catch (CancellationException e) {   // future.cancel(false);
//                // Exception was thrown
//                e.printStackTrace();
//            }
//            catch (InterruptedException e) {    // future.cancel(true);
//                e.printStackTrace();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try (var executorService = Executors.newThreadPerTaskExecutor(virtualThreadFactory)) {
//            // Spawn a bunch of tasks, where we don't know how long they will take to complete...
//            IntStream.range(0, 16).forEach(item -> {
//                // System.out.println("item = " + item + ", Thread ID = " + Thread.currentThread());
//                executorService.submit(() -> {
//                    try {
//                        var milliseconds = item * 1000;
//                        System.out.println("task " + item + " sleeping " + milliseconds + " milliseconds" + Thread.currentThread());
//                        Thread.sleep(milliseconds);
//                        System.out.println("task " + item + " awake - " + Thread.currentThread());
//                    }
//                    catch (InterruptedException e) {
//                        // This will happen if executorService.shutdownNow() is called
//                        // but not if executorService.shutdown() is called
//                        System.out.println("Interrupted task " + item + ", Thread ID = " + Thread.currentThread());
//                    }
//                });
//            });
//
//            // executorService.shutdown();      // Experiment with this
//            // executorService.shutdownNow();   // Experiment with this
//
//            // This is a blocking call
//            if (executorService.awaitTermination(10000, TimeUnit.MILLISECONDS)) {
//                System.out.println("done in time");
//            } else {
//                System.err.println("timeout expired...");
//                // This might be a good place to call executorService.shutdownNow() to get things to finish
//            }
//        }
//        catch (InterruptedException e) {
//            // This only happens if the Thread hosting the awaitTermination() call is interrupted
//            e.printStackTrace();
//            System.exit(-1);
//        }
//        catch (RuntimeException e) {
//            System.err.println(e.getMessage());
//        }
//
//        System.out.println("\n**************************\n");
//
//        try {
//            try (var executorService = Executors.newThreadPerTaskExecutor(virtualThreadFactory)) {
//                IntStream.range(0, 16).forEach(item -> {
//                    executorService.submit(() -> {
//                        try {
//                            var milliseconds = item * 1000;
//                            System.out.println("task " + item + " sleeping " + milliseconds + " milliseconds - " + Thread.currentThread());
//                            Thread.sleep(milliseconds);
//                            System.out.println("task " + item + " awake - " + Thread.currentThread());
//                            if (item == 8) {
//                                throw new RuntimeException("task 8 is acting up");
//                            }
//                        }
//                        catch (InterruptedException e) {
//                            System.out.println("Interrupted task " + item + ", Thread ID = " + Thread.currentThread());
//                        }
//                    });
//                });
//            }
//            catch (CancellationException e) {
//                // Exception was thrown
//                System.err.println("Inner... " + e.getMessage());
//            }
//            catch (RuntimeException e) {
//                System.err.println("Inner... " + e.getMessage());
//            }
//        }
//        catch (CancellationException e) {
//            // Exception was thrown
//            System.err.println("Outer... " + e.getMessage());
//        }
//        catch (RuntimeException e) {
//            System.err.println("Outer... " + e.getMessage());
//        }


        try (var executorService = Executors.newThreadPerTaskExecutor(virtualThreadFactory)) {
            IntStream.range(0, 15).forEach(i -> {
                // System.out.println("i = " + i + ", Thread ID = " + Thread.currentThread());
                executorService.submit(() -> {
                    try {
                        var milliseconds = i * 1000;
                        System.out.println(Thread.currentThread() + " sleeping " + milliseconds + " milliseconds");
                        Thread.sleep(milliseconds);
                        System.out.println(Thread.currentThread() + " awake");
                    } catch (InterruptedException e) {
                        System.out.println("Interrupted i = " + i + ", Thread ID = " + Thread.currentThread());
                    }
                });
            });
            // executorService.shutdown();
            if (executorService.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                System.out.println("done");
            } else {
                System.out.println("timeout expired...");
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(-1);
        }


        try (var executorService = Executors.newThreadPerTaskExecutor(virtualThreadFactory)) {

            // Submits a value-returning task and waits for the result
            var future = executorService.submit(() -> "foo");

            var result = future.join();
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


        var deadline = Instant.now().plusSeconds(2);

        var threadFactory = Thread.ofVirtual().factory();

        var e = Executors.newSingleThreadExecutor(threadFactory);

        try (var executor = Executors.newThreadPerTaskExecutor(threadFactory)) {

        }

//        try (var executor = Executors.newScheduledThreadPool(16, threadFactory)) {
//            var foo = executor.schedule(() -> {}, 1, TimeUnit.SECONDS);
//
//        }
    }

}
