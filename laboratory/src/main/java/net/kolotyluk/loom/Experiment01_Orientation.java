package net.kolotyluk.loom;

import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 *  <h1>Experiment Suite 01 - Orientation</h1>
 *  <p>
 *      We're going to start off by running two identical experiments, one with conventional Platform Threads
 *      and the other with Loom Virtual Threads, so we can compare the results. If you do something like
 *      <pre>
 *
 *  System.out.println(Thread.currentThread().toString()));
 *      </pre>
 *      You will get what I call a 'Thread Signature.' For Platform Threads, the Thread Signature looks like
 *      <pre>
 *
 *  Thread[#16,Thread-0,5,main]
 *      </pre>
 *  where
 *      <dl style="margin-left: 3pc">
 *          <dt><tt>Thread</tt></dt>
 *              <dd>distinguishes this from a Virtual Thread</dd>
 *          <dt><tt>#16</tt></dt>
 *              <dd>is the raw Thread ID</dd>
 *          <dt><tt>Thread-0</tt></dt>
 *              <dd>is </dd>
 *          <dt><tt>5</tt></dt>
 *              <dd>is </dd>
 *          <dt><tt>main</tt></dt>
 *              <dd>is </dd>
 *      </dl>
 *  For Virtual Threads, the Thread Signature looks like
 *      <pre>
 *
 *  VirtualThread[#31]/runnable@ForkJoinPool-1-worker-1
 *      </pre>
 *  where
 *      <dl style="margin-left: 3pc">
 *          <dt><tt>VirtualThread</tt></dt>
 *              <dd>distinguishes this from a Platform Thread</dd>
 *          <dt><tt>#16</tt></dt>
 *              <dd>is the raw Thread ID</dd>
 *          <dt><tt>runnable</tt>></dt>
 *              <dd>is </dd>
 *          <dt><tt>ForkJoinPool-1</tt></dt>
 *              <dd>is the Thread Pool the ExecutorService spawned the thread on</dd>
 *          <dt><tt>worker-1</tt></dt>
 *              <dd>is the Carrier Thread that the Virtual Thread is running on</dd>
 *     </dl>
 *  </p>
 * @see <a href="https://wiki.openjdk.java.net/display/loom/Getting+started">Loom Getting Started</a>
 * @see <a href="https://www.youtube.com/watch?v=Nb85yJ1fPXM">Java ExecutorService - Part 1</a>
 * @author eric@kolotyluk.net
 */
public class Experiment01_Orientation {

    public static void main(String ... args){
        Context.printHeader(Experiment01_Orientation.class.getName());

        System.out.println("Fiber Fun - Experiment 1");
        System.out.println("CPU Cores = " + Runtime.getRuntime().availableProcessors() + '\n');

        var platformThreadFactory = Thread.ofPlatform().factory();
        var virtualThreadFactory = Thread.ofVirtual().factory();

        experiments("Platform Threads Experiment", platformThreadFactory);
        experiments("Virtual Threads Experiment", virtualThreadFactory);
    }

    /**
     * <h1>experiments</h1>
     * <p>
     *     Notice how we use the {@link ExecutorService} to spawn tasks on worker threads. This is considered
     *     a best practice because it's a higher level of abstraction, and as I have learned, in Concurrent
     *     Programming, it's generally safer to use higher levels of abstraction, because the lower levels
     *     have been designed and implemented by experts. Also important to note is the use of
     *     {@link ExecutorService#submit(Runnable)} or {@link ExecutorService#submit(Callable)},
     *     and in particular, we use a
     *     <a href="https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html">Lambda Expression</a>
     *     because it reduces boilerplate in our code by also being a higher level abstraction.
     * </p>
     * <p>
     *     Next, notice how we use a
     *     <a href="https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html">try-with-resources</a>
     *     statement to bound the lifetime of the ExecutorService. This is also considered a best practice because it
     *     supports <a href="https://en.wikipedia.org/wiki/Structured_concurrency">Structured Concurrency</a>.
     *     Before leaving the try { } block, {@link ExecutorService#close()} is called to clean up.
     * </p>
     * <p>
     *     While the old APIs and the old methods still exist, we should all think hard about using them,
     *     and have good reason for deviating from the ExecutorService and Structure Concurrency patterns.
     *     Also, an advantage of using Thead Factories is that it gives us more flexibility where we can
     *     define the thread type in one place, and use it in many.
     * </p>
     * @param title printed before the experiments are run
     * @param threadFactory to use for spawning threads
     * @author eric@kolotyluk.net
     */
    static void experiments(String title, ThreadFactory threadFactory) {
        System.out.printf("\n%s\n%n", title);

        try (var executorService = StructuredExecutor.open("Experiment00", threadFactory)) {
            /* We start with the simple case of creating some Threads from an IntStream of items,
             * where we create a new task Thread for each item, printing out which threads we are
             * executing on. Note that the items are printed on the startup thread, the tasks are
             * printed on Worker Threads as spawned by the executorService.
             */
            IntStream.range(0, 15).forEach(item -> {
                System.out.printf("item = %d, Thread ID = %s\n", item, Thread.currentThread());
                executorService.fork(() -> {
                    System.out.printf("\ttask = %d, Thread ID = %s\n", item, Thread.currentThread());
                    return null;
                });
            });
            executorService.join();
        }
        catch  (InterruptedException e) {
            System.out.println("interrupted");
        }

//        // This try block implicitly defines the context or scope for one level of Structured Concurrency
//        try (var executorService = Executors.newThreadPerTaskExecutor(threadFactory)) {
//            /* We start with the simple case of creating some Threads from an IntStream of items,
//             * where we create a new task Thread for each item, printing out which threads we are
//             * executing on. Note that the items are printed on the startup thread, the tasks are
//             * printed on Worker Threads as spawned by the executorService.
//             */
//            IntStream.range(0, 15).forEach(item -> {
//                System.out.printf("item %s, Thread Signature %s\n", item, Thread.currentThread());
//                executorService.submit(() -> {
//                    System.out.printf("\ttask %s, Thread Signature %s\n", item, Thread.currentThread());
//                });
//            });
//        }
    }

}
