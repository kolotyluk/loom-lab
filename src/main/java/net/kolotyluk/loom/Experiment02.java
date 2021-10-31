package net.kolotyluk.loom;

import javax.management.InvalidApplicationException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * <h1>Experiment Suite 02</h1>
 * <p>
 *     This suite of experiments aims to get some basic practice with Futures that are based on Runnable and Callable
 *     tasks, so that as we explore further we have a good sense of how these work. The generic term 'task' is used
 *     to represent some unit of computation, generally concurrent computation. In the previous Experiments I used the
 *     term 'item' to represent the identity of a task...
 * </p>
 */
public class Experiment02 {

    public static <R, Int> void main(String args[]){
        System.out.println("Fiber Fun - Experiment 2");
        System.out.println("CPU Cores = " + Runtime.getRuntime().availableProcessors() + '\n');

        var platformThreadFactory = Thread.ofPlatform().factory();
        var virtualThreadFactory = Thread.ofVirtual().factory();

        Callable<String> callable = () -> "callable";
        Runnable runnable = () -> System.out.println("runnable");

        /* Because Platform Threads and Virtual Threads share the same API, we leverage different Thread Factories
         * to verify that we get the same behaviour from each type of thread, barring performance differences such
         * as latency and throughput.
         */

        System.out.println("\nPlatform Thread Experiments");
        experiments(platformThreadFactory);

        System.out.println("\nVirtual Thread Experiments");
        experiments(virtualThreadFactory);
    }

    static void experiments(ThreadFactory threadFactory) {

        try (var executorService = Executors.newThreadPerTaskExecutor(threadFactory)) {

            /* spawn is a configurable laboratory where we can investigate the behaviour of tasks.
             * Starting with a very simple happy path experiment, let's just get the value of some task.
             */

            System.out.println("\n\nresult = " + spawn(executorService, () -> "callable 1", (future) -> {}));

            /* Now, let's see what happens when try to cancel a task after it's started, but before we get the future
             * value of the task.
             */

            System.out.println("\n\nresult = " + spawn(executorService, () -> "callable 2", (future) -> future.cancel(false)));
            System.out.println("\n\nresult = " + spawn(executorService, () -> "callable 3", (future) -> future.cancel(true)));

            /* In most cases we will get a CancellationException for both, but not always. Sometime we will actually
             * get the value of the future without a CancellationException based on the "Uncertainty Principle" of
             * Concurrent Computing.
             */

            Callable<String> sleeper4 = () -> {
                Thread.sleep(Duration.ofSeconds(4));
                return "sleeper 4";
            };

            /* spawn has a built-in timeout of 3 seconds, so let's see what happens if we wait too long...
             * We get a TimeoutException as expected. Note: this exception is just a notification, if we want
             * more to happen, we need to take action...
             */

            System.out.println("\n\nresult = " + spawn(executorService, sleeper4, (future) -> {}));


            Callable<String> sleeper1 = () -> {
                Thread.sleep(Duration.ofSeconds(2));
                return "sleeper 1";
            };

            Callable<String> sleeper2 = () -> {
                Thread.sleep(Duration.ofSeconds(2));
                return "sleeper 2";
            };

            System.out.println("\n\nresult = " + spawn(executorService, sleeper1, (future) -> future.cancel(false)));

            /* sleeper1 gets a CancellationException as expected...
             * sleeper2 does not get an InterruptedException as expected... but the documentation says
             * "If the task has already started, then the mayInterruptIfRunning parameter determines whether the
             * thread executing this task (when known by the implementation) is interrupted in an attempt to stop
             * the task." I don't understand...
             */

            System.out.println("\n\nresult = " + spawn(executorService, sleeper2, (future) -> future.cancel(true)));

            /* Based on my understanding of Future.get(), spawn should catch a "java.util.concurrent.ExecutionException
             * – if the computation threw an exception." However, it doesn't, rather we catch a generic Exception
             * out here. Is this a defect in the design/implementation/documentation, or some misunderstanding on
             * my part?
             */

            Callable<String> malcontent = () -> {
                Thread.sleep(Duration.ofSeconds(2));
                throw new IllegalStateException("malcontent acting up");
                //return "malcontent";
            };

            System.out.println("\n\nresult = " + spawn(executorService, malcontent, (future) -> {}));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    static String spawn(
            ExecutorService executorService,
            Callable<String> callable,
            Consumer<Future<String>> consumer
    ) throws Exception {

        try {
            var result = executorService.submit(callable);
            consumer.accept(result);
            return result.get(3, TimeUnit.SECONDS);
        }
        catch (TimeoutException e) {
            // The timeout expired...
            //e.printStackTrace();
            return callable.call() + " - TimeoutException";
        }
        catch (ExecutionException e) {
            // Exception was thrown
            // e.printStackTrace();
            return callable.call() + " - ExecutionException";

        }
        catch (CancellationException e) {   // future.cancel(false);
            // Exception was thrown
            //e.printStackTrace();
            return callable.call() + " - CancellationException";

        }
        catch (InterruptedException e) {    // future.cancel(true);
            //e.printStackTrace();
            return callable.call() + "- InterruptedException ";

        }

    }

}
