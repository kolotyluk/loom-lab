package net.kolotyluk.loom;

import java.time.Instant;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * <h1>Introductory loom-lab Experiment</h1>
 *  <p>
 *      This experiment is a quick way to jump into Project Loom, because we're here to 'il-loom-inate' things 😉
 *  </p>
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
 * . . .
 * [task 0 result, task 1 result, ..., task 14 result, task 15 result]
 * </pre>
 *     Where we just print some information on which thread the code is running on; the indented lines were spawned
 *     by the unindented lines. Note how there are two types of Threads, Platform Threads such as
 *     <tt>Thread[#1,main,5,main]</tt> and Virtual Threads such as <tt>VirtualThread[#17]</tt>, where
 *     Virtual Threads are <em>the</em> new feature of Project Loom. Other new features we see here include
 *     {@link StructuredExecutor}.
 * </p>
 * @see <a href="https://kolotyluk.github.io/loom-lab/">Project Documentation</a>
 * @see <a href="https://kolotyluk.github.io/loom-lab/advantages.md">Loom Advantages</a>
 * @see <a href="https://kolotyluk.github.io/loom-lab/lexicon.md">Project Loom Lexicon</a>
 * @see <a href="https://bugs.openjdk.java.net/browse/JDK-8277129">Structured Concurrency</a>
 * @see <a href="https://bugs.openjdk.java.net/browse/JDK-8277131">Virtual Threads</a>
 */
public class Experiment00_Introduction {

    public static void main(String args[]) {
        Context.printHeader(Experiment00_Introduction.class);

        try (var structuredExecutor = StructuredExecutor.open("Experiment00")) {

            // Spawn all our worker tasks...
            var futureList = IntStream.range(0, 16).mapToObj(item -> {
                System.out.printf("item = %d, Thread ID = %s\n", item, Thread.currentThread());
                return structuredExecutor.fork(() -> {
                    System.out.printf("\ttask = %d, Thread ID = %s\n", item, Thread.currentThread());
                    // Note that task input and output can be different types
                    return "task %d result".formatted(item);
                });
            }).collect(Collectors.toList());

            // Wait for all our worker tasks to complete...
            structuredExecutor.join();

            // Note a good practices is to use a Java Stream to spawn tasks, collect their Future results,
            // then get the results of the completed Futures. Of course this is much more simple to do with
            // Parallel Stream, but as we will see later, there are cases where Virtual Threads have advantages
            // over Parallel Stream.
            var completedResults = futureList.stream().map(Future::resultNow).toList();

            System.out.println(completedResults);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
