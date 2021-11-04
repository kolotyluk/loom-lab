package net.kolotyluk.loom

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.function.IntConsumer
import java.util.stream.IntStream
import net.kolotyluk.loom.LoomUtilities.use

/**
 * https://wiki.openjdk.java.net/display/loom/Getting+started
 */
fun main(args: Array<String>) {
    println("Hello, World")

    println("CPU Cores = ${Runtime.getRuntime().availableProcessors()}")

//
//    Executors.newVirtualThreadExecutor().use { executor ->
//        println("starting...")
//
//        // Submits a value-returning task and waits for the result
//        val future = executor.submit<String> { println("foo"); "foo" }
//        val result = future.join()
//
//        println(result)
//
//        // Submits two value-returning tasks to get a Stream that is lazily populated
//        // with completed Future objects as the tasks complete
//        val stream = executor.submit(
//            List.of(
//                Callable { "foo" },
//                Callable { "bar" })
//        )
//        stream.filter { obj: Future<String> -> obj.isCompletedNormally }
//            .map { obj: Future<String> -> obj.join() }
//            .forEach { x: String? -> println(x) }
//
//        // Executes two value-returning tasks, waiting for both to complete
//        val results1 = executor.invokeAll(
//            List.of(
//                Callable { "foo" },
//                Callable { "bar" })
//        )
//
//        // Executes two value-returning tasks, waiting for both to complete. If one of the
//        // tasks completes with an exception, the other is cancelled.
//        val results2 =
//            executor.invokeAll(
//                List.of(
//                    Callable { "foo" },
//                    Callable { "bar" }),  /*waitAll*/false
//            )
//
//        // Executes two value-returning tasks, returning the result of the first to
//        // complete, cancelling the other.
//        val first = executor.invokeAny(
//            List.of(
//                Callable { "foo" },
//                Callable { "bar" })
//        )
//    }
//
//    Thread.sleep(5000)

//    IntStream.range(0, 16).forEach(IntConsumer { i: Int ->
//        println("i = $i, Thread ID = ${Thread.currentThread()}")
//    })

    val executor = Executors.newVirtualThreadExecutor()

    executor
        .submit(Runnable { println("Thread = ${Thread.currentThread()}") })

    executor.shutdown()

    Executors.newVirtualThreadExecutor().use { executorService ->
        IntStream.range(0, 16).forEach(IntConsumer { i: Int ->
            println("i = $i, Thread ID = ${Thread.currentThread()}")
            executorService.submit(Runnable {
                println("Thread ID = ${Thread.currentThread()}")
            })
        })
    }

}







