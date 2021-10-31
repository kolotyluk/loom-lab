# Fun With Project Loom Fibers

Note: the `fiber` idiom of Project Loom is now the `VirtuaThread`, so I will only refer to fibers in
the title of this project for historic reasons.

Part of the *fun* of this project is throwing some Kotlin into the mix.

## Getting Started

Currently, to use Project Loom you need the
[Project Loom Early-Access Builds](http://jdk.java.net/loom)

### Windows

On Windows I install this in `C:\Program Files (Open)\jdk-18` because `C:\Program Files` is protected
by Windows, so it's best to let a package installer like MSI manage that, and jdk-18 does not yet
have a package installer.

# Insights

I have been using Java since 1995 before it's first commercial release. After that, I said
**Goodbye Forever** to C++. While I continued to use C now and then, I was fed up with the
unreasonable complexity and psychological danger of C++.

Out of curiosity, I did experiment a little with the original Java Green Threads, but did not really
try too much Concurrent Programming until Platform Threads were introduced later. Jumping in with
confidence, it did not take too long to realize how difficult it was to do Concurrent Programming
properly, but things did get better.

About 2005 I started playing with Scala, and Akka by 2010, where I started learning Reactive
Programming methods. Functional Programming, Non-Blocking Code, Futures, Actors, and Streams
with Backpressure. Several large Akka Projects I worked on, I loved starting the project and
watching all the CPU Cores go to 100% initially, demonstrating how effectively Reactive Systems
could utilize resources. After initializing, the CPU load would drop to about 10% or so until
you started up some performance tests.

While it is clear how well these Reactive systems performed, at the raw code level we really had
to tangle ourselves up with hard to read programming styles, especially because of Non-Blocking
requirements.

The miracle of Project Loom is the JVM itself had been modified to support Virtual Threads that
are astonishingly cheap and effective, creating a new Paradigm Shift from Non-Blocking to
'Let It Block,' because now the JVM can dance around Blocking APIs, and efficiently deal with all
the necessary concurrency. *What's Old, is New again.*

This Git Project is a playground where I can learn how to use Project Loom, and maybe share some
of that learning.

# References

- [Open JDK Wiki - Loom](https://wiki.openjdk.java.net/display/loom)
- [Inside Java - Loom](https://inside.java/tag/loom)
- [GitHub](https://github.com/openjdk/loom)

# Lessons Learned

## Structured Concurrency

> Structured concurrency is a programming paradigm aimed at improving the clarity, quality,
> and development time of a computer program by using a structured approach to concurrent programming.

— [Wikipedia](https://en.wikipedia.org/wiki/Structured_concurrency) 2021-10-28

In Akka it was possible to set up a hierarchy of Actors to support a philosophy of *'let it fail.'*
While this was a Structured Concurrency mechanism, it was truly difficult to set up, and wrap your
mind around what was going on. In Project Loom you can write something like

    // Parent Thread
    try (var executorService = Executors.newVirtualThreadExecutor()) {   // Open a Concurrency Context
        IntStream.range(0, 15).forEach(i -> {
            System.out.println("i = " + i + ", Thread ID = " + Thread.currentThread());
            executorService.submit(() -> {   // Child Thread
                System.out.println("Thread ID = " + Thread.currentThread());
            });
        });
    }   // Close Concurrency Context

where the parent thread, spawns a number of child threads, such that parent thread waits at the end
of the `try` block for the child thread to complete, or 'join' back with the parent in terms of
concurrency.

    i = 0, Thread ID = Thread[#1,main,5,main]
    i = 1, Thread ID = Thread[#1,main,5,main]
    i = 2, Thread ID = Thread[#1,main,5,main]
    i = 3, Thread ID = Thread[#1,main,5,main]
    i = 4, Thread ID = Thread[#1,main,5,main]
    i = 5, Thread ID = Thread[#1,main,5,main]
    i = 6, Thread ID = Thread[#1,main,5,main]
    Thread ID = VirtualThread[#28]/runnable@ForkJoinPool-1-worker-2
    Thread ID = VirtualThread[#29]/runnable@ForkJoinPool-1-worker-3
    Thread ID = VirtualThread[#27]/runnable@ForkJoinPool-1-worker-1
    i = 7, Thread ID = Thread[#1,main,5,main]

where

- `Thread[#1,main,5,main]` is our parent thread, the main startup thread
- `VirtualThread[#28]` is one of the child threads
- `runnable@ForkJoinPool-1-worker-2` is the Carrier Thread that Virtual Thread `#28` is running on.

Each time you run this code, the output will be ordered differently because the order is
nondeterministic, because concurrency itself is generally nondeterministic.

If at some point in the code you did something like

    executorService.shutdown();

which is documented as

> Initiates an orderly shutdown in which previously submitted tasks are executed,
> but no new tasks will be accepted. Invocation has no additional effect if already shut down.
> This method does not wait for previously submitted tasks to complete execution.
> Use awaitTermination to do that.

or you could do

    executorService.shutdown();
    
> Attempts to stop all actively executing tasks, halts the processing of waiting tasks,
> and returns a list of the tasks that were awaiting execution. This method does not wait
> for actively executing tasks to terminate. Use awaitTermination to do that.
> 
> There are no guarantees beyond best-effort attempts to stop processing actively executing tasks.
> For example, typical implementations will cancel via Thread.interrupt, so any task that fails
> to respond to interrupts may never terminate.

The point is, that the `executorService` defines a Structured Concurrency Context, arranged as a
hierarchy or tree of Contexts, where you have control over each context. You can let them finish
and wait, or tell them to stop, and wait. In the Bounded Context of the Java try-with-resources,
when you get to the end of the `try` block, `close()` is called, which basically does
`executorService.shutdown()` and then `awaitTermination()` thereby cleaning up the context.

## Exceptions

From the previous example, if one of the child threads throws and exception that is not caught
in the try-with-resources block, then this will implicitly do a `shutdownAll` because if things
are failing it does not make sense to continue. This is another implicit benefit of Structured
Concurrency.

The best part though, is your stack traces will make sense, you will be able to see what
thread the exception originated from. This was generally not true in nonblocking systems like Akka
or Project Reactor, but is true in Kotlin Coroutines.

## Languages

### Java

### Scala

I don't know if I will try Scala with Project Loom yet, but if I do, it will definitely be Scala 3.
If someone encourages me, that might help. I am pretty sure Scala and Akka will incorporate Project
Loom when it's stable enough... it's just too compelling to ignore Project Loom.

### Kotlin

People keep asking me "why would you try to use Project Loom from Kotlin when Kotlin has Coroutines?"

Because I can... 😋 😛 😝 😜 🤪

Actually, I am just curious to see how different Loom code looks from one language to next.
I have it on good authority that Kotlin Coroutines will utilize Project Loom once things are
stable enough, but for now it's fun to play.

One stumbling block is that Kotlin does not have try-with-resources... well it should... it took
me way too long to figure out how to use `use` properly because it was not clear to me how to create
the necessary function extension. 🙄 
[Can Project Loom be used from Kotlin?](https://stackoverflow.com/questions/69756146/can-project-loom-be-used-from-kotlin)
IMHO, Kotlin should implement try-with-resources because as I have discovered `use` has pitfalls in
using it correctly.

#### Use

In Java we can have code like 

    try (var executor = Executors.newVirtualThreadExecutor()) {
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

