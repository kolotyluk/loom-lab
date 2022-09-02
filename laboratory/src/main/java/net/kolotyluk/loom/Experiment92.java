
package net.kolotyluk.loom;

import jdk.incubator.concurrent.StructuredTaskScope;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * <h1>Structured Concurrency Experiment</h1>
 * <p>
 *     This experiment simulates a more complex, but not unreasonable, distributed computing scenario.
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
public class Experiment92 {

//    public static void main(String args[]) {
//        Context.printHeader(Experiment92.class);
//
//        try {
//            var foo = new URI("https://server1/foobar.com/item");
//
//            getRemoteStrings().forEach(System.out::println);
//        } catch (ExperimentException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//    }
//
//    static List<String> getRemoteStrings() throws ExperimentException {
//
//        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
//
//            // We want complete results, so we won't tolerate failure.
//            var completionHandler = new StructuredExecutor.ShutdownOnFailure();
//
//            var futureResults = IntStream.range(0, 15).mapToObj(item -> {
//                try {
//                    System.out.printf("item = %d, Thread ID = %s\n", item, Thread.currentThread());
//                    return scope.fork(() -> {
//                        try {
//                            System.out.printf("\ttask = %d, Thread ID = %s\n", item, Thread.currentThread());
//                            return getRemoteString(item, new URI("https://server1/foobar.com/item"));
//                        }
//                        catch (Throwable t) {
//                            System.out.printf("TASK EXCEPTION %s\n\t%s\n\n", t.getMessage(), t.getCause());
//                            t.printStackTrace();
//                            throw t;
//                        }
//                    }, completionHandler);
//                } catch (Throwable t) {
//                    System.out.printf("SPAWN EXCEPTION %s\n\t%s\n\n", t.getMessage(), t.getCause());
//                    t.printStackTrace();
//                    throw t;
//                }
//            });
//            structuredExecutor.joinUntil(Instant.now().plusSeconds(10));
//            completionHandler.throwIfFailed();
//            return futureResults.map(Future::resultNow).toList();
//        }
//        catch  (InterruptedException e) {
//            e.printStackTrace();
//            throw new ExperimentException(e);
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//            throw new ExperimentException(e);
//        } catch (TimeoutException e) {
//            e.printStackTrace();
//            throw new ExperimentException(e);
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//            throw new ExperimentException(e);
//        }
//        catch (Throwable t) {
//            t.printStackTrace();
//            throw new ExperimentException(t);
//        }
//    }
//
//    static String getRemoteString(int item, URI from) {
//        return "Item %d from %s".formatted(item, from);
//    }
//
//    static class ExperimentException extends Exception {
//        ExperimentException(Throwable cause) {
//            super(cause);
//        }
//    }
}
