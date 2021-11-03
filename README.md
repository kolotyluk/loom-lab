# Project Loom Learning Laboratory

A place to learn about [Project Loom](https://openjdk.java.net/projects/loom) through hands-on
experimentation and exploration.

My hope is to develop this into a learning tool for other people as well, so if you have ideas on how
this can work better for you or others, please create a ticket in
[loom-lab Issues](https://github.com/kolotyluk/loom-lab/issues).
People are encouraged to `clone` this repo, run the experiments
and other code, make local changes, and watch what happens.

If someone wants to pay me to develop this, I will be even more motivated 🥳, but for now, it's just
recreational programming.

If you want to contribute, I will discuss this later...

Finally, while this laboratory is based on Project Loom, my expectation is that anything you can do via
Loom Virtual Threads, you can **semantically** do using conventional Platform Threads, but because
Virtual Threads are more efficient, you can use ***many*** more of them than you would ever consider
with Platform Threads, and thereby adopt new programming styles that were not pragmatic before. *The
bottom line is, that with Platform Threads, you cannot **pragmatically** do the same things you can with
Virtual Threads.*

# Getting Started

This is the part I hate the most in learning anything new as too often on-boarding is unnecessarily
traumatic because of poor documentation and inadequate examples. 🙄 Again, if you think this section could
be better, create an issue ticket.

If you're a master hacker, a [MacGyver](https://en.wikipedia.org/wiki/MacGyver) who can figure anything
out, I hope the boilerplate does not get in your way too much.


## IntelliJ IDEA

This projected is maintained with JetBrains [IntelliJ IDEA](https://www.jetbrains.com/idea).
Consequently, the `.idea` folder exists in the git repo to help others get started sooner.

🤔   If this makes things difficult or confusing for others, please let me know as I have some
ideas on how to structure things better.

One nice feature of the IntelliJ IDEA is that you can display formatted `javadoc` in the editor.
As this project is a learning resource, there is an abundance of javadoc to explain things, and IntelliJ
makes this a very effective way to read and explore without having to explicitly generate the `javadoc`.
You can toggle between 

![Code Documentation](IntelliJ Code Documentation.png)

## Getting Loom Binaries

Currently, to use Project Loom you need the
[Project Loom Early-Access Builds](http://jdk.java.net/loom), in particular, as of 2021-10-31 you
need to download the `jdk-18` + `loom` runtime. This is currently incomplete, so don't use it to run
anything in your tool chain, such as IntelliJ, just use it as a local runtime for running these
experiments.

### Macintosh

tbd

### Windows

On Windows I install this in `C:\Program Files (Open)\jdk-18` because `C:\Program Files` is protected
by Windows, so it's best to let a package installer like MSI manage that, and jdk-18 does not yet
have a package installer.


# References

- Loom
  - [Open JDK Wiki](https://wiki.openjdk.java.net/display/loom)
  - [Inside Java](https://inside.java/tag/loom)
  - [GitHub](https://github.com/openjdk/loom)
  - [Java® Platform, Standard Edition & Java Development Kit - Version 18 API Specification](https://download.java.net/java/early_access/loom/docs/api/index.html)

# Insights

I have been using Java since 1995 before it's first commercial release. After that, I said
**Goodbye Forever** to C++. While I continued to use C now and then, I was fed up with the
unreasonable complexity and psychological danger of C++.

Out of curiosity, I did experiment a little with the original Java Green Threads, but did not really
try too much Java Concurrent Programming until Platform Threads were introduced later. Jumping in with
confidence, it did not take too long to realize how difficult it was to do Concurrent Programming
properly, but things did get better.

I discovered sanity in reading
[Java Concurrency In Practice](https://www.amazon.ca/Java-Concurrency-Practice-Brian-Goetz/dp/0321349601)
where [Brian Goetz](https://www.linkedin.com/in/briangoetz) offers amazing clarity in reasoning and
understanding. After reading this, I was ready for
[Concurrent Programming in Java : Design Principles and Pattern](https://www.amazon.com/Concurrent-Programming-Java%C2%99-Principles-Pattern/dp/0201310090)
where [Doug Lea](https://en.wikipedia.org/wiki/Doug_Lea) brought on a lot of academic rigor that was
essential, but often challenging to grok. The point is, I went from believing Concurrent Programming
is not that hard to respecting how hard ***correct*** Concurrent Programming really is.

About 2005 I started playing with [Scala](https://en.wikipedia.org/wiki/Scala_(programming_language)),
and [Akka](https://akka.io/) by 2010, where I started learning Reactive
Programming methods; Functional Programming, Non-Blocking Code, Futures, Actors, Streams
with Backpressure, etc. Several large Akka Projects I worked on, I loved starting the project and
watching all the CPU Cores go to 100% initially, demonstrating how effectively Reactive Systems
could utilize resources. After initializing, the CPU load would drop to about 10% or so until
you started up some performance tests.

After all this, the most important lesson I learned was it is generally safer and saner to program at the
higher levels of abstraction, Futures, Actors, and Streams al la Akka, than mess with Threads,
`syncronized`, Locks, etc.

While it is clear how well these Reactive systems performed, at the raw code level we really had
to tangle ourselves up with hard to read programming styles, especially because of Non-Blocking
requirements.

The miracle of Project Loom is the JVM itself had been modified to support Virtual Threads that
are astonishingly cheap and effective, creating a new Paradigm Shift from Non-Blocking to
'Let It Block,' because now the JVM can dance around Blocking APIs, and efficiently deal with all
the necessary concurrency. *What's Old, is New again.*

This Git Project is a playground where I can learn how to use Project Loom, and maybe share some
of that learning.


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

