package net.kolotyluk.loom

import net.kolotyluk.loom.LoomUtilities.use
import java.util.concurrent.Executors
import java.util.function.IntConsumer
import java.util.stream.IntStream

object HelloKotlin {

    /**
     * https://wiki.openjdk.java.net/display/loom/Getting+started
     */
    @JvmStatic
    fun main(args: Array<String>) {
        Context.printHeader(HelloKotlin.javaClass)

        val executor = Executors.newVirtualThreadPerTaskExecutor()

        executor
            .submit(Runnable { println("Thread = ${Thread.currentThread()}") })

        executor.shutdown()

        Executors.newVirtualThreadPerTaskExecutor().use { executorService ->
            IntStream.range(0, 16).forEach(IntConsumer { i: Int ->
                println("i = $i, Thread ID = ${Thread.currentThread()}")
                executorService.submit(Runnable {
                    println("Thread ID = ${Thread.currentThread()}")
                })
            })
        }

    }
}
