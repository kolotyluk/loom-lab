# Fun With Project Loom Fibers

Note: the `fiber` idiom of Project Loom is now the `VirtuaThread`, so I will only refer to fibers in
the title of this project for historic reasons.

Part of the *fun* of this project is throwing some Kotlin into the mix.

## Getting Started

Currently, to use Project Loom you need the
[Project Loom Early-Access Builds](http://jdk.java.net/loom)

### Windows

On Windows I install this in `C:\Program Files (Open)\jdk-18` because `C:\Program Files` is protected
by Windows, so it's best to let a package installer like MSI manage that.


# Lessons Learned

## Kotlin

### Use

In Java we can have code like 

    try (ExecutorService executor = Executors.newVirtualThreadExecutor()) {
        IntStream.range(0, 15).forEach(i -> {
            System.out.println("i = " + i + ", Thread ID = " + Thread.currentThread());
            executor.submit(() -> {
                System.out.println("Thread ID = " + Thread.currentThread());
            });
        });
    }

using try-with-resources to automatically `close()` the `ExecutorService`, which will `shutdown`
all spawned threads and wait for them to complete. Kotlin does not have try-with-resources, but has
the [use](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/use.html) construct which can be
used as

    Executors.newVirtualThreadExecutor().use { executorService ->
        IntStream.range(0, 16).forEach(IntConsumer { i: Int ->
            println("i = $i, Thread ID = ${Thread.currentThread()}")
            executorService.submit(Runnable {
                println("Thread ID = ${Thread.currentThread()}")
            })
        })
    }

but requires the special *Black Magic* function extension of

    fun ExecutorService.use(block: (executorService: ExecutorService) -> Unit) = block(this)

In this project, I keep this in `net.kolotyluk.loom.LoomUtilities` as

    package net.kolotyluk.loom
    import java.util.concurrent.ExecutorService
    object LoomUtilities {
        fun ExecutorService.use(block: (executorService: ExecutorService) -> Unit) = block(this)
    }

so other files can use

    import net.kolotyluk.loom.LoomUtilities.use

