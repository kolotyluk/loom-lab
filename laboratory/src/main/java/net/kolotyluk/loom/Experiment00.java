package net.kolotyluk.loom;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * <h1>Simple loom-lab Experiment</h1>
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
 * </pre>
 *     Where we just print some information on which thread the code is running on; the indented lines were spawned
 *     by the unindented lines. While this is a very simple experiment, we will look at many of the new Project Loom
 *     bells and whistles, using the best new practices, so that we are well grounded for further experiments.
 *     <em>Feel free to ignore most of this for now, but remember it's here to get grounded again.</em>
 * </p>
 * <h1 style="padding-top: 16pt;">Virtual Threads</h1>
 * <p>
 *     In simple terms, <a href="https://bugs.openjdk.java.net/browse/JDK-8277131">Virtual Threads</a>
 *     are a new implementation of the classic Java Thread APIs, <em> <strong>but critically</strong>, a more
 *     efficient implementation.</em> Unlike Kotlin Coroutines, there are no language changes to support Project Loom.
 *     However, unlike Kotlin Coroutines, Virtual Threads are supported by new functionality in the JVM. Once Virtual
 *     Threads become stable and available, we should likely see a refactoring of many concurrency frameworks to use
 *     Virtual Threads, such as Kotlin Coroutines, Scala, Akka, Reactor and other Reactive frameworks.
 * </p>
 * <h1 style="padding-top: 16pt;">Structured Concurrency</h1>
 * <p>
 *     In addition to Virtual Threads, one of the important new features Project-Loom brings to JDK-18 is
 *     <a href="https://bugs.openjdk.java.net/browse/JDK-8277129">Structured Concurrency</a>
 *     In a nutshell, Structured Concurrency is like eliminating <tt>goto</tt> in old style programming languages,
 *     creating better discipline in how we fork and join, keeping all such concurrency in a hierarchy of parents
 *     and children, or sessions and sub-sessions.
 *     <blockquote>
 *         <em>When the flow of execution splits into multiple concurrent flows, they rejoin in the same code block.</em>
 *     </blockquote>
 *     {@link StructuredExecutor} is the heart of Structured Concurrency which is designed to work with
 *     try-with-resources blocks, where the {@link java.util.concurrent.ExecutorService} resource is closed at the
 *     end of the block. {@link StructuredExecutor#open(String)} opens a '<em>Session</em>' which implies a lifecycle
 *     and lifetime. The lifecycle is best described as
 * </p>
 * <ol>
 *     <li>
 *         Get an ExecutorService instance and open a session, which starts the lifecycle of that session.
 *     </li>
 *     <li>
 *         Define a Completion Handler from one of
 *         <ul>
 *             <li>StructuredExecutor.ShutdownOnFailure()</li>
 *             <li>StructuredExecutor.ShutdownOnSuccess()</li>
 *         </ul>
 *         each of which initiates the shutdown of the whole family of forked tasks... children, children of children,
 *         etc. These are also known as Completion Policies, and there could be other policies in the future.
 *     </li>
 *     <li>
 *         Use {@link StructuredExecutor#fork(Callable, BiConsumer)} to fork (spawn) tasks according to the
 *         ThreadFactory in {@link StructuredExecutor#open(String,ThreadFactory)}. If not specified, the default is
 *         Virtual Threads. {@link BiConsumer} the Completion Handler.
 *     </li>
 *     <li>
 *         {@link StructuredExecutor#join()} to wait for the lifecycles of all the spawned tasks to complete. Note,
 *         if these child tasks spawn their own tasks, those lifecycles must also complete.
 *     </li>
 *     <li>
 *         Handle Execution Failures, such as with <tt>StructuredExecutor.ShutdownOnFailure.throwIfFailed();</tt>
 *         Basically, we need to deal with this before the try-with-resources block implicitly calls close() in
 *         the <tt>finally</tt> stage, because try-with-resources is not flexible enough to handle this situation.
 *     </li>
 *     <li>
 *         Close the StructuredExecutor resource implicitly, finally completing its lifetime.
 *     </li>
 * </ol>
 * <h1 style="padding-top: 16pt;">Conclusions</h1>
 * <p>
 *     The StructuredExecutor we create here is a 'child' node of the Thread running, and each child that is forked
 *     becomes a child node of the StructuredExecutor. In this way, we can manage all forked threads as a group in
 *     a well-disciplined way. In a sense, the StructuredExecutor and the Thread it is opened from, are the parents
 *     of the sibling tasks that are spawned. In short, Project Loom is an attempt to make Concurrent Families less
 *     dysfunctional... 😉
 * </p>
 * @see <a href="https://kolotyluk.github.io/loom-lab/">Project Documentation</a>
 * @see <a href="https://kolotyluk.github.io/loom-lab/lexicon.md">Project Loom Lexicon</a>
 * @see <a href="https://bugs.openjdk.java.net/browse/JDK-8277129">Structured Concurrency</a>
 * @see <a href="https://bugs.openjdk.java.net/browse/JDK-8277131">Virtual Threads</a>
 * @see <a href="https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/StructuredExecutor.html">Class StructuredExecutor</a>
 * @see <a href="https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/StructuredExecutor.ShutdownOnFailure.html">Class StructuredExecutor.ShutdownOnFailure</a>
 * @see <a href="https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/StructuredExecutor.ShutdownOnSuccess.html">Class StructuredExecutor.ShutdownOnSuccess</a>
 * @see <a href="https://download.java.net/java/early_access/loom/docs/api/java.base/java/lang/ScopeLocal.html">Class ScopeLocal</a>
 */
public class Experiment00 {

    public static void main(String args[]) {
        Context.printHeader(Experiment00.class.getName());

        var platformThreadFactory = Thread.ofPlatform().factory();
        var virtualThreadFactory = Thread.ofVirtual().factory();

        // Best practice is to use try-with-resources to get an instance of an Executor, something that
        // implements Executor and AutoClosable. We don't need to specify virtualThreadFactory because it's
        // the default, so this is just for demonstration purposes.
        try (var structuredExecutor = StructuredExecutor.open("Experiment00", virtualThreadFactory)) {

            // We don't always need this, but it's used here as an example
            var completionHandler = new StructuredExecutor.ShutdownOnFailure();

            // It is not good practice to 'fire-and-forget' concurrent tasks, so we collect all the Futures returned
            // from fork() in order to manage them later if we need to.
            var futureResults = IntStream.range(0, 15).mapToObj(item -> {
                System.out.printf("item = %d, Thread ID = %s\n", item, Thread.currentThread());
                return structuredExecutor.fork(() -> {
                    System.out.printf("\ttask = %d, Thread ID = %s\n", item, Thread.currentThread());
                    return item;
                }, completionHandler);
            });

            // One way to wait for all our tasks to be done is to get() the results from all the Futures.
            // However, as we can see, this is kinda messy because of possible exceptions thrown. This is
            // old-school, so see below for better practices.
            var results = futureResults.map(future -> {
                try {
                    return future.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());

            // Rather than the above, it's more useful to call join() to wait for the session's lifecycle to complete.
            // Currently, we are REQUIRED to call join() before close() is implicitly called at the end of the block.
            // If we don't call join, close() will throw an exception. In concurrent programming, it's a best practice
            // to time limit operations.
            structuredExecutor.joinUntil(Instant.now().plusSeconds(10));

            // Generally there is some other housekeeping we might do after rejoining all the threads we forked,
            // such as dealing with failure.
            completionHandler.throwIfFailed();

            // Rather than collect the results of all our tasks as above, it's better to call join(), and then
            // call resultNow() on the Futures, because we won't have to catch any exceptions. Note, it's best do
            // this after dealing with any failures first.
            var completedResults = futureResults.map(Future::resultNow).toList();

        }
        catch  (InterruptedException e) {
            // thrown from join() and joinUntil() if we're being interrupted
            System.out.println("interrupted");
        } catch (ExecutionException e) {
            // thrown from throwIfFailed() if any of the children failed with an exception
            e.printStackTrace();
        } catch (TimeoutException e) {
            // thrown from joinUntil() if the deadline is exceeded
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // thrown from resultNow() if the Future is not completed, or the Task failed,
            // but this should never happen if join() and throwIfFailed() have been called first.

        }
        // When exiting this block, structuredExecutor.close() is called to 'finally' clean up.
    }
}
