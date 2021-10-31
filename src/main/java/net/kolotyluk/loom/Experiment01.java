package net.kolotyluk.loom;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * https://wiki.openjdk.java.net/display/loom/Getting+started
 */
public class Experiment01 {

    static long timestamp = 0;

    public static void main(String args[]){
        System.out.println("Fiber Fun - Experiment 1");
        System.out.println("CPU Cores = " + Runtime.getRuntime().availableProcessors() + '\n');

        /* Because we're here to 'il-loom-inate' things ;-)
         *
         * We're going to start right away by creating some Virtual Threads. One way to do this is via
         * Executors.newVirtualThreadExecutor() but that may be deprecated soon, so we're going to use
         * Executors.newThreadPerTaskExecutor(virtualThreadFactory) which needs a ThreadFactory. We could
         * use Thread.ofPlatform() instead if we want to stay old school.
         */
        var virtualThreadFactory = Thread.ofVirtual().factory();

        System.out.println("virtualThreadFactory = " + virtualThreadFactory + '\n');

        /* We start with the simple case of creating some Virtual Threads from an IntStream of items,
         * where we create a new Virtual Thread task for each item, printing out which threads we are
         * executing on. Note that the items are printed on the startup thread, which is a Java Platform
         * Thread. However, the tasks are printed on Virtual Threads, where each Virtual Thread is being
         * run on a Platform Carrier Thread from the ForkJoinPool.
         *
         * For a first run of this experiment, you may notice that all the println output comes out orderly,
         * and sequentially. This is because at startup, things are fairly stable, but it will soon appear
         * more chaotic, less deterministic, more normal in a concurrent runtime environment.
         */
        try (var executorService = Executors.newThreadPerTaskExecutor(virtualThreadFactory)) {
            IntStream.range(0, 15).forEach(item -> {
                System.out.println("item = " + item + ", Thread ID = " + Thread.currentThread());
                executorService.submit(() -> {
                    System.out.println("\ttask = " + item + ", Thread ID = " + Thread.currentThread());
                });
            });
        }

        /* Just to show that we can get the same results two different ways...
         */
        try (var executorService = Executors.newVirtualThreadExecutor()) {
            IntStream.range(0, 15).forEach(i -> {
                System.out.println("i = " + i + ", Thread ID = " + Thread.currentThread());
                executorService.submit(() -> {
                    var thread = Thread.currentThread();
                    System.out.println("Thread ID = " + thread);
                });
            });
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        /* Now let's pause to think about this... Notice how we created an ExecutorService in order to spawn
         * tasks... this is considered a best practice now in Java, whether you are using Platform Threads or
         * Virtual Threads. With this pattern, the Executor, spawns tasks, and they can be spawned with either
         * Platform Threads or Virtual Threads.
         *
         * While the old APIs and the old methods still exist, we should all think hard about using them,
         * and have good reason for deviating from the Executor pattern.
         *
         * Another advantage of using Thead Factories is that it gives us more flexibility where we can define
         * the thread type in one place, and use it in many.
         */

        /* Up until now we always spawned our tasks inside a Java Try-With-Resources block. This is another best
         * practice, because when we exit the block, all the threads spawned with be asked to stop, and we will
         * wait for them to stop. If we uncomment the following code and run it, we can see that it can take quite
         * a long time for the program to stop.
         */

        var executorService = Executors.newVirtualThreadExecutor();
        IntStream.range(0, 15).forEach(i -> {
            System.out.println("i = " + i + ", Thread ID = " + Thread.currentThread());
            executorService.submit(() -> {
                var thread = Thread.currentThread();
                System.out.println("Thread ID = " + thread);
            });
        });

        executorService.shutdownNow(); // 80 seconds



    }

}
