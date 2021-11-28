package net.kolotyluk.loom;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * <h1>Experiment Suite 02</h1>
 * <p>
 *     This suite of experiments aims to get some basic practice with {@link Future} results that are based on
 *     {@link Runnable} and {@link Callable} tasks, so that as we explore further we have a good sense
 *     of how these work. The generic term 'task' is used to represent some unit of computation, generally
 *     a concurrent computation. In the previous Experiments I used the term 'item' to represent the identity
 *     of a task, or more precisely, the identity of something needing the results of a task.
 * </p>
 * <p>
 *     Note: aside from returning a result, a Callable can also throw an Exception, while a Runnable cannot throw a
 *     <strong><em>Checked</em></strong> Exception.
 * </p>
 * <p>
 *     In these experiments, it is the responsibility of the {@link ExecutorService} to <em>spawn</em> a task,
 *     but it's the responsibility of a {@link Future} to interact with that task after it has been spawned.
 *     <em>I use the term <strong>spawn</strong> from my time working with Akka, where Actors would 'spawn'
 *     child actors, where the word 'spawn' implies a parent/child relationship. Akka supports Structure
 *     Concurrency via Actors in a similar way ExecutorService can, it just looks a little different, and is a
 *     little harder to manage. Note: An Akka Actor is a much higher level Concurrency Construct than an ExecutorService
 *     tasks, but even at this level, Structured Concurrency is still important.</em>
 * </p>
 * <p>
 *     In Structured Concurrency, the parent/child relationship is very important, because it implies a
 *     hierarchical structure.
 * </p>
 * <p>
 *     We may remember when we were a child, when our mom was angry, she might have said "I brought you into
 *     this world, and I can take you out." Dark humor aside, one aspect of Structured Concurrency is that: generally,
 *     the parent should be in control of its children, including monitoring them, and stopping them when the need
 *     arises, where a Future can do both.
 *      <dl>
 *          <dt>{@link ExecutorService#awaitTermination(long, TimeUnit)}</dt>
 *              <dd>Rather than just invoke {@link ExecutorService#close()} which is implicit in the
 *                  try-with-resources block, if we explicitly wait for all the children to complete,
 *                  we can define a timeout for all of them collectively. It is good practice in Concurrent
 *                  Programming to use timeouts to create more deterministic applications.</dd>
 *          <dt>{@link ExecutorService#shutdown()}</dt>
 *              <dd>If at some point the parent decides to abandon all further processing, it can stop accepting work,
 *                  but let any working tasks complete.</dd>
 *          <dt>{@link ExecutorService#shutdownNow()}</dt>
 *              <dd>A stronger form of shutdown(), this will try to cancel any tasks that are still running.
 *                  For example, we might want to do this after calling awaitTermination(timeout) if we
 *                  do hit the timout. It would be good practice to again call awaitTermination(timeout)
 *                  and do something more necessary if we hit the timeout again. Another reason to call this
 *                  is when we decided we already have enough results from workers, and don't need any more.</dd>
 *          <dt>{@link ExecutorService#invokeAny(Collection, long, TimeUnit)}</dt>
 *              <dd>Similar to the above, if you don't need the result of all tasks, if we only need the results
 *                  of one successful task, it is easy to create a collection of submittable tasks, and then
 *                  just wait for the result of the first one to succeed. When one succeeds, the ExecutorService
 *                  will do a shutdownNow(). Again, including a timeout is good practice.</dd>
 *          <dt> {@link ExecutorService#invokeAll(Collection, long, TimeUnit)}</dt>
 *              <dd>Similar to the above, if we really want the results of all child tasks, this is the most concise
 *                  way to ask for it. Unlike the above, the result is a Future result, or more specifically, a List
 *                  of Future results, so some more thought needs to be put into it.</dd>
 *          <dt>{@link Future#get(long, TimeUnit)}</dt>
 *              <dd>Rather than just invoke {@link Future#get()}, by specifying a timeout, we make our code
 *                  more deterministic by limiting how long it is reasonable to wait for a result. Unlike the above
 *                  collective methods, sometimes we want to get down to the individual task level, and so we deal
 *                  with the individual Futures for each task.</dd>
 *          <dt>{@link Future#join()}</dt>
 *              <dd>While similar to get(), there is currently no join(long, TimeUnit) that will timeout.
 *                  Without further details, I advise against it, unless there is some compelling advantage
 *                  in using it.</dd>
 *          <dt>{@link Future#cancel(boolean)}</dt>
 *              <dd>Sometimes there is good reason to cancel the execution of a task, and this is the way to do it.
 *                  Note the parameter <tt>mayInterruptIfRunning</tt>, when <tt>true</tt> can provide a more certain
 *                  cancellation if necessary. Note also, while we can cancel a Future, we cannot cancel a Thread,
 *                  whether Platform or Virtual. While we can Interrupt a Thread, we cannot Interrupt a Future.</dd>
 *      </dl>
 *      While there are more methods on ExecutorService and Future, these are a few of the most important for this
 *      level of detail.
 * </p>
 * @see <a href="https://wiki.openjdk.java.net/display/loom/Getting+started">Loom Getting Started</a>
 * @see <a href="https://www.youtube.com/watch?v=Nb85yJ1fPXM">Java ExecutorService - Part 1</a>
 */
public class Experiment02 {

    public static <R, Int> void main(String args[]){
        System.out.println("Fiber Fun - Experiment 2");
        System.out.println("CPU Cores = " + Runtime.getRuntime().availableProcessors() + '\n');

        var platformThreadFactory = Thread.ofPlatform().factory();
        var virtualThreadFactory = Thread.ofVirtual().factory();

//        Callable<String> callable = () -> "callable";
//        Runnable runnable = () -> System.out.println("runnable");

        /* Because Platform Threads and Virtual Threads share the same API, we leverage different Thread Factories
         * to verify that we get the same behaviour from each type of thread, barring performance differences such
         * as latency and throughput.
         */

        experiments("Platform Thread Experiments", platformThreadFactory);

        experiments("Virtual Thread Experiments", virtualThreadFactory);
    }

    /**
     * Framework for launching a suite of experiments with a given {@link ThreadFactory}. Note that we continue
     * with our Structured Concurrency pattern because it's just good practice, and easy to use. To not use
     * Structured Concurrency, we would need to demonstrate a compelling advantage to avoiding it.
     * @param title
     * @param threadFactory
     */
    static void experiments(String title, ThreadFactory threadFactory) {
        System.out.printf("\n%s\n%n", title);

        try (var executorService = Executors.newThreadPerTaskExecutor(threadFactory)) {

            // var task = new FutureTask<void>(() -> System.out.println());

            executorService.execute(() -> System.out.println());
            var f = executorService.submit(() -> System.out.println());
            var b = executorService.submit(() -> 2 * 3);


            /* spawn is a configurable laboratory where we can investigate the behaviour of tasks.
             * Starting with a very simple happy path experiment, let's just get the value of some task.
             */

            System.out.println("result = " + spawn(executorService, () -> "callable 1", (future) -> {}));

            /* Now, let's see what happens when try to cancel a task after it's started, but before we get the future
             * value of the task.
             */

            System.out.println("result = " + spawn(executorService, () -> "callable 2", (future) -> future.cancel(false)));
            System.out.println("result = " + spawn(executorService, () -> "callable 3", (future) -> future.cancel(true)));

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

            System.out.println("result = " + spawn(executorService, sleeper4, (future) -> {}));


            Callable<String> sleeper1 = () -> {
                Thread.sleep(Duration.ofSeconds(1));
                return "sleeper 1";
            };

            Callable<String> sleeper2 = () -> {
                Thread.sleep(Duration.ofSeconds(2));
                return "sleeper 2";
            };

            /* sleeper1 and sleeper2 both get cancelled, but sleeper2 does not get an InterruptedException because
             * it does not try to catch one. See spinner for catching InterruptedException.
             */

            System.out.println("result = " + spawn(executorService, sleeper1,
                    (future) -> System.out.println("sleeper1 cancelled = " + future.cancel(false))));

            System.out.println("result = " + spawn(executorService, sleeper2,
                    (future) -> System.out.println("sleeper2 cancelled = " + future.cancel(true))));


            Callable<String> sleeper3 = () -> {
                Thread.sleep(Duration.ofSeconds(1));
                Thread.sleep(Duration.ofSeconds(1));
                Thread.sleep(Duration.ofSeconds(1));
                Thread.sleep(Duration.ofSeconds(1));
                return "sleeper 3";
            };

            System.out.println("result = " + spawn(executorService, sleeper3,
                (future) -> {
                    try {
                        Thread.sleep(Duration.ofSeconds(1));
                        System.out.println("sleeper3 cancelled = " + future.cancel(false));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }));

            /* When cancelling a future, it could be that the underlying task is not blocking on Thread.sleep()
             * or other blocking methods, it could just be computationally busy, so the task needs to explicitly
             * check for interrupts and deal with them, but we then need to generate interrupts for this to work.
             */
            Callable<String> spinner = () -> {
                try {
                    var i = 0;
                    for (; i < 10000; i++) {
                        // This is a hint to the JVM that it might be able to optimize spinning this on some hardware
                        Thread.onSpinWait();
                        // We could call Thread.currentThread().isInterrupted() instead, but that does not reset
                        // the state of the interrupted flag. By resetting the state below, we are able to detect
                        // new interrupts after servicing this one... your choice...
                        if (Thread.interrupted()) {
                            System.err.println("spinner detected isInterrupted at " + i);
                            // We don't really need to throw an exception as we could just service the interrupt
                            // here, so it's more a matter of style and/or the overall situation we're in, but it
                            // might be better to follow a consistent pattern of always servicing interrupts in the
                            // catch clause?
                            throw new InterruptedException();
                        }
                    }
                    return "spinner completed at " + i;
                }
                catch (InterruptedException e) {
                    System.err.println("spinner caught InterruptedException");
                    return "spinner InterruptedException";
                }
            };

            /* While we can still cancel this Future, it will complete all long-running computations unless we
             * actually generate and interrupt.
             */

            System.out.println("result = " + spawn(executorService, spinner,
                    (future) -> System.out.println("spinner1 cancelled = " + future.cancel(false))));

            /* While we can still cancel this Future, it will complete all long-running computations unless we
             * actually generate and interrupt.
             */

            System.out.println("result = " + spawn(executorService, spinner,
                    (future) -> System.out.println("spinner2 cancelled = " + future.cancel(true))));

            Callable<String> malcontent = () -> {
                Thread.sleep(Duration.ofSeconds(2));
                throw new IllegalStateException("malcontent acting up");
                //return "malcontent";
            };

            System.out.println("result = " + spawn(executorService, malcontent, (future) -> {}));




        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class FooBar extends FutureTask {

        public FooBar(@NotNull Callable callable) {
            super(callable);
        }


    }


    /**
     * This is basically a lab instrument for starting experiments. It really just contrived to be able to
     * demonstrate the lessons from these experiments. Note that the 'experiments' method above defines the
     * parent context in terms of Structured Concurrency, and this method is an extension of that parent
     * context because it actually spawns the child tasks. The lesson is, when designing and implementing
     * our code, to always be cognizant of our Structured Concurrency boundaries.
     * @param executorService the parent that spawns children
     * @param callable the task we want the executorService to spawn
     * @param consumer the function we want to run after the callable has been spawned
     * @return the string result from the spawned task
     * @throws Exception
     */
    static String spawn(
            ExecutorService executorService,
            Callable<String> callable,
            Consumer<Future<String>> consumer
    ) throws Exception {

        try {
            var result = executorService.submit(callable);
            consumer.accept(result);    // Execute the consumer function with our Future result
            return result.get(3, TimeUnit.SECONDS);
        }
        catch (TimeoutException e) {
            // The timeout expired...
            return callable.call() + " - TimeoutException";
        }
        catch (ExecutionException e) {
            // Exception was thrown from our task
            return  "ExecutionException " + e.getMessage();
        }
        catch (CancellationException e) {   // future.cancel(false);
            // Exception was thrown
            return callable.call() + " - CancellationException";
        }
        catch (InterruptedException e) {    // future.cancel(true);
            // TODO for some reason we never get here, so need to investigate further
            return callable.call() + "- InterruptedException ";
        }

    }

}
