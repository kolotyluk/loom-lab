package net.kolotyluk.loom;

import java.util.concurrent.StructuredExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.stream.IntStream;

/**
 * <h1>Simple loom-lab Experiment</h1>
 * <p>
 *     This is one of the most simple Project-Loom experiments that shows something interesting. When we run it we
 *     should see something like:
 * <pre>
 * item = 3, Thread ID = Thread[#1,main,5,main]
 * item = 4, Thread ID = Thread[#1,main,5,main]
 * item = 5, Thread ID = Thread[#1,main,5,main]
 *     task = 1, Thread ID = VirtualThread[#17]/runnable@ForkJoinPool-1-worker-2
 *     task = 0, Thread ID = VirtualThread[#15]/runnable@ForkJoinPool-1-worker-9
 * item = 6, Thread ID = Thread[#1,main,5,main]
 * item = 7, Thread ID = Thread[#1,main,5,main]
 * </pre>
 *     Where we just print some information on which thread the code is running on, where the indented lines
 *     were spawned by the <tt>Thread[#1,main,5,main]</tt> lines.
 * </p>
 * <h1>Structured Concurrency</h1>
 * <p>
 *     In addition to Virtual Treads, one of the important new features Project-Loom brings to JDK-18 is
 *     Structured Concurrency. In a nutshell, Structured Concurrency is like eliminating <tt>goto</tt> in
 *     old style programming languages, creating discipline in how we fork and join, keeping all such
 *     concurrency in a hierarchy of parents and children.
 * </p>
 * https://wiki.openjdk.java.net/display/loom/Getting+started
 * @see <a href="http://openjdk.java.net/jeps/8277129">Structured Concurrency</a>
 * @see <a href="http://openjdk.java.net/jeps/8277131">Virtual Threads</a>
 */
public class Experiment00 {

    static final ThreadFactory virtualThreadFactory = Thread.ofVirtual().factory();

    public static void main(String args[]) {
        Context.printHeader(Experiment00.class.getName());

        // StructuredExecutor is the heart of Structured Concurrency which is designed to work with
        // try-with-resources blocks, where the resource is closed at the end of the block. The
        // StructuredExecutor we create here is a 'child' node of the Thread running, and each child
        // that is forked becomes a child node of the StructuredExecutor. In this way, we can manage
        // all forked threads as a group in a well-disciplined way.
        try (var executorService = StructuredExecutor.open("Experiment00")) {
            IntStream.range(0, 15).forEach(item -> {
                System.out.printf("item = %d, Thread ID = %s\n", item, Thread.currentThread());
                executorService.fork(() -> {
                    System.out.printf("\ttask = %d, Thread ID = %s\n", item, Thread.currentThread());
                    return null;
                });
            });

            // Currently, we are REQUIRED to call join() before close() is implicitly called at the end of the block.
            // If we don't call join, close() will throw an exception.
            executorService.join();
            // Generally there is some other housekeeping we might do after rejoining all the threads we forked,
            // but in this simple case we're done.
        }
        catch  (InterruptedException e) {
            System.out.println("interrupted");
        }
        // When exiting this block, executorService.close() is called to clean up.
    }
}
