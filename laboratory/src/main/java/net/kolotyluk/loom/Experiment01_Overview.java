package net.kolotyluk.loom;

import java.time.Instant;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * <h1>Project Loom Overview Experiment</h1>
 *  <p>
 *     This experiment expands on the previous one, producing the same results, but doing so with a lot more code.
 *  </p>
 * <p>
 *     While this is still a very simple experiment, we will look at many of the new Project Loom
 *     bells and whistles, using some new best new practices, so that we are well grounded for further experiments.
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
 *             <li>
 *                 <a href="https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/StructuredExecutor.ShutdownOnFailure.html">StructuredExecutor.ShutdownOnFailure()</a>
 *                 when you want to shutdown on any failure.
 *             </li>
 *             <li>
 *                 <a href="https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/StructuredExecutor.ShutdownOnSuccess.html">StructuredExecutor.ShutdownOnSuccess()</a>
 *                 when you only care about the successfull results of one Task.
 *             </li>
 *             <li>
 *                 <a href="https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/StructuredExecutor.CompletionHandler.html">StructuredExecutor.CompletionHandler</a>
 *                 for a custom Completion Handler.
 *             </li>
 *         </ul>
 *         each of which implements some actions on how Task completion is handled. These are also known as Completion
 *         Policies, and there could be other policies in the future. Note, the act of shutting down not only affects
 *         all the Tasks subordinate to this session, but also recursively shuts down any child sessions too, the whole
 *         family of forked tasks... children, children of children, etc.
 *     </li>
 *     <li>
 *         Use {@link StructuredExecutor#fork(Callable, BiConsumer)} to fork (spawn) tasks according to the
 *         ThreadFactory in {@link StructuredExecutor#open(String,ThreadFactory)}. If not specified, the default is
 *         Virtual Threads. {@link BiConsumer} the Completion Handler.
 *     </li>
 *     <li>
 *         Optionally, call {@link StructuredExecutor#shutdown()} to cancel all uncompleted Tasks. When this method
 *         returns, the calling join() will return immediately without blocking/waiting. We may also call this after
 *         {@link StructuredExecutor#joinUntil(Instant)} as in the code below, where our policy is to shutdown after
 *         timeout. Note: this is very different than {@link ExecutorService#shutdown()} so there is a bit of paradigm
 *         shift here.
 *     </li>
 *     <li>
 *         Always call {@link StructuredExecutor#join()} to wait for the lifecycles of all the spawned tasks to
 *         complete. Note, if these child Tasks spawn their own Tasks, those lifecycles must also complete first.
 *     </li>
 *     <li>
 *         Handle Execution Failures, such as with <tt>StructuredExecutor.ShutdownOnFailure.throwIfFailed();</tt>
 *         Basically, we need to deal with this before the try-with-resources block implicitly calls close() in
 *         the <tt>finally</tt> stage, because try-with-resources is not flexible enough to handle this situation.
 *     </li>
 *     <li>
 *         Optionally, collect the results of all Tasks. If there are failures of some, but not all Tasks, handling
 *         this is also shown below.
 *     </li>
 *     <li>
 *         Close the StructuredExecutor resource implicitly, finally completing its lifetime.
 *     </li>
 * </ol>
 * <h1 style="padding-top: 16pt;">Conclusions</h1>
 * <p>
 *     The StructuredExecutor we create here is a 'child' node of the Thread running, inheriting the {@link ScopeLocal}
 *     values of the thread, and each child that is forked becomes a child node of the StructuredExecutor, also
 *     inheriting the {@link ScopeLocal} values. In this way, we can manage all forked threads as a group in a
 *     well-disciplined way. In a sense, the StructuredExecutor and the Thread it is opened from, are the parents of
 *     the sibling tasks that are spawned. In short, Project Loom is an attempt to make Concurrent Families less
 *     dysfunctional... 😉
 * </p>
 * <h2 style="padding-top: 14pt;">Streams and Lazy Evaluation</h2>
 * <p>
 *     One trap I stumbled into with this experiement, was forgetting to terminate the {@link Stream} I was
 *     using to spawn the Tasks. My original code looked like
 *     <pre>
 *
 * var completedResults = futureStream.map(Future::resultNow).toList();
 *     </pre>
 *     Which threw an <tt>IllegalStateException</tt> because the tasks were not spawned until <tt>toList()</tt>
 *     is called. The problem was not obvious to me, and I had to ask help from the Loom Developers to understand it.
 *     So, when spawning tasks via a Stream, always remember to terminate the stream before calling <tt>join()</tt>.
 * </p>
 * @see <a href="https://kolotyluk.github.io/loom-lab/">Project Documentation</a>
 * @see <a href="https://kolotyluk.github.io/loom-lab/advantages.md">Loom Advantages</a>
 * @see <a href="https://kolotyluk.github.io/loom-lab/lexicon.md">Project Loom Lexicon</a>
 * @see <a href="https://bugs.openjdk.java.net/browse/JDK-8277129">Structured Concurrency</a>
 * @see <a href="https://bugs.openjdk.java.net/browse/JDK-8277131">Virtual Threads</a>
 * @see <a href="https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/StructuredExecutor.html">Class StructuredExecutor</a>
 * @see <a href="https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/StructuredExecutor.ShutdownOnFailure.html">Class StructuredExecutor.ShutdownOnFailure</a>
 * @see <a href="https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/StructuredExecutor.ShutdownOnSuccess.html">Class StructuredExecutor.ShutdownOnSuccess</a>
 * @see <a href="https://download.java.net/java/early_access/loom/docs/api/java.base/java/lang/ScopeLocal.html">Class ScopeLocal</a>
 */
public class Experiment01_Overview {

    public static void main(String args[]) {
        Context.printHeader(Experiment01_Overview.class);

        // The two kinds of Threads we can use, that generally share the same interfaces, but different implementations
        var platformThreadFactory = Thread.ofPlatform().factory();
        var virtualThreadFactory  = Thread.ofVirtual().factory();

        // Best practice is to use try-with-resources to get an instance of an Executor, something that
        // implements Executor and AutoClosable. We don't need to specify virtualThreadFactory because it's
        // the default, so this is just for demonstration purposes.
        try (var structuredExecutor = StructuredExecutor.open("Experiment00", virtualThreadFactory)) {

            // We don't always need this, but it's used here as an example
            var completionHandler = new StructuredExecutor.ShutdownOnFailure();

            // It is not good practice to 'fire-and-forget' concurrent tasks, so we collect all the Futures returned
            // from fork() in order to manage them later if we need to, or collect the results of the tasks.
            var futureStream = IntStream.range(0, 16).mapToObj(item -> {
                System.out.printf("item = %d, Thread ID = %s\n", item, Thread.currentThread());
                return structuredExecutor.fork(() -> {
                    System.out.printf("\ttask = %d, Thread ID = %s\n", item, Thread.currentThread());
                    return item;
                }, completionHandler);
            });

            // Note: we need to terminate the Stream that is spawning Tasks before we call join(), otherwise
            // when we call Future::resultNow below, we will get an IllegalStateException.
            var futureList = futureStream.toList();

            // One way to wait for all our tasks to be done is to get() the results from all the Futures.
            // However, as we can see, this is kinda messy because of possible exceptions thrown. This is
            // old-school, so see below for better practices.
//            var results = futureResults.map(future -> {
//                try {
//                    return future.get();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }).collect(Collectors.toList());

            // Rather than the above, it's more useful to call join() to wait for the session's lifecycle to complete.
            // Currently, we are REQUIRED to call join() before close() is implicitly called at the end of the block.
            // If we don't call join, close() will throw an exception. In concurrent programming, it's a best practice
            // to time limit operations.
            try {
                structuredExecutor.joinUntil(Instant.now().plusSeconds(10));
            }
            catch (TimeoutException e) {
                // Sadly, we have to catch this here, because in the try-with-resources block, structuredExecutor
                // is out of scope. One good strategy is to simply shutdown, then wait again with join, but there
                // may be other strategies people want to use in other situations.
                structuredExecutor.shutdown();
                // Note, that while join() is idempotent, and we could call it again here, we don't need to because
                // it's already been called, and shutdown has the effect of bringing the entire session to the join
                // phase of the session.
            }

            // Generally there is some other housekeeping we might do after rejoining all the threads we forked,
            // such as dealing with failure.
            completionHandler.throwIfFailed();

            // Rather than collect the results of all our tasks as above, it's better to call join(), and then
            // call resultNow() on the Futures, because we won't have to catch any exceptions. Note, it's best do
            // this after dealing with any failures first.
            var completedResults = futureList.stream().map(Future::resultNow).toList();

            // This throws an Exception, so uncomment it to see what happens,
            // but also comment out var futureList = futureStream.toList();
            // var completedResults = futureStream.map(Future::resultNow).toList();

            System.out.printf("completeResults = %s\n", completedResults);

            // Instead of calling completionHandler.throwIfFailed() as above, we might want to simply collect
            // partial results of successful Tasks, and generally ignore the reasons for failure.
            var partialResults = futureList.stream()
                .filter(future -> future.state() == Future.State.SUCCESS)
                .toList();

            System.out.printf("partialResults  = %s\n", partialResults);
        }
        catch  (InterruptedException e) {
            // thrown from join() and joinUntil() if we're being interrupted, possibly a side effect of cancel
            e.printStackTrace();
        } catch (ExecutionException e) {
            // thrown from throwIfFailed() if any of the children failed with an exception
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // thrown from resultNow() if the Future is not completed, or the Task failed,
            // but this should never happen if join() and throwIfFailed() have been called first,
            // so in this context, it's incorrect use of the API.
            e.printStackTrace();
        } finally {
            System.out.println("Finished Finally");
        }
        // When exiting this block, structuredExecutor.close() is called to 'finally' clean up.

        System.out.println("Finished Experiment");
    }
}
