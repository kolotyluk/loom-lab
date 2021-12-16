# Purpose of Loom

The [purpose of Project Loom](https://www.youtube.com/watch?v=fq0OEX0XYR8) is to increase the
*Throughput* of *Applications*, such as Web Servers, by improving the level *Concurrency* of *Tasks*
use to complete *Operations*, such as *HTTP Requests*, where there can be Concurrency between both
Operations and the Tasks used by each Operation.

Consider [Little's Law](https://en.wikipedia.org/wiki/Little%27s_law)

<span style="padding-left: 40pt;font-size: 30pt;font-family: 'Times New Roman', serif;">*L = λW*</span>

Where L is the *Level* of concurrency, λ is the throughput, and W is the Wait time or *total latency*.

If we want to build a high performance web server that maximizes request throughput then we need
to consider

<span style="padding-left: 40pt;font-size: 30pt;font-family: 'Times New Roman', serif;">*λ = L / W*</span>

which implies for the same latency on the same hardware, we need to improve our Concurrency.

## Threads

Java has had a concurrency API from the beginning, starting with
[Green Threads](https://en.wikipedia.org/wiki/Green_threads)
in 1.0, but quickly moving to
[Kernel Threads](https://en.wikipedia.org/wiki/Thread_(computing)#Kernel_threads), called
[Platform Threads](lexicon.md#platform-thread) the Loom Lexicon.

Project Loom improves concurrency by offering the same *Interface* with a different *Implementation*
that uses
[User Threads](https://en.wikipedia.org/wiki/Thread_(computing)#User_threads) or
[Fibers](https://en.wikipedia.org/wiki/Thread_(computing)#Fibers), called
[Vitual Threads](lexicon.md#virtual-thread) in the Loom Lexicon.

While there are no changes to the Java Language, there are changes to the Java Libraries and the
Java Virtual Machine to support Project Loom Capabilities, where ***Virtual Threads offer improved
concurrency over Platform Threads.***

## Goals and Objectives

While the purpose, or main goal of Project Loom is improved Application Throughput, there are 
various other Goals and Objectives. Learn more in [Loom Advantages](advantages.md).


# Core Documentation

A place to learn about [Project Loom](https://openjdk.java.net/projects/loom) through hands-on
experimentation and exploration.

- [GitHub](https://github.com/kolotyluk/loom-lab)
- [API Documentation](site/apidocs/index.html)
  - [laboratory](../laboratory/docs/site/apidocs/index.html)
- [Maven Site Documentation](site/index.html)
- [Loom Advantages](advantages.md)
- [Loom Lab Lexicon](lexicon.md)

This is the main body of documentation for loom-lab.

My hope is to develop this into a learning tool for other people as well, so if you have ideas on how
this can work better for you or others, please create a ticket in
[loom-lab Issues](https://github.com/kolotyluk/loom-lab/issues).
People are encouraged to `clone` this repo, run the experiments
and other code, make local changes, and watch what happens.

I am happy to accept donations via
[PayPal](https://www.paypal.com/donate/?hosted_button_id=MHJPUV97X4XVC)

![QR Code](QR-Code.png)

If you want to sponsor this project for advertising, please contact me.

If you want to contribute, I will discuss this later...

Finally, while this laboratory is based on Project Loom, my expectation is that anything you can do via
Loom Virtual Threads, you can **semantically** do using conventional Platform Threads, but because
Virtual Threads are more efficient, you can use ***many*** more of them than you would ever consider
with Platform Threads, and thereby adopt new programming styles that were not pragmatic before. *The
bottom line is, that with Platform Threads, you cannot **pragmatically** do the same things you can with
Virtual Threads.* See also
[Ron Pressler - Loom: Bringing Lightweight Threads and Delimited Continuations to the JVM](https://www.youtube.com/watch?v=r6P0_FDr53Q)
for more theoretical grounding.

## See Also

- [Videos](VIDEOS.md)

### References

- [Little's law](https://en.wikipedia.org/wiki/Little%27s_law) - Wikipedia
- [State of Loom: Part 1](https://cr.openjdk.java.net/~rpressler/loom/loom/sol1_part1.html) - May, 2020
- [State of Loom: Part 2](https://cr.openjdk.java.net/~rpressler/loom/loom/sol1_part2.html) - May, 2020
- [On the Performance of User-Mode Threads and Coroutines](https://inside.java/2020/08/07/loom-performance) - 2020-08-07
- [On Parallelism and Concurrency](https://inside.java/2021/11/30/on-parallelism-and-concurrency) - 2021-11-30
- Loom
  - [Open JDK Wiki](https://wiki.openjdk.java.net/display/loom)
  - [Inside Java](https://inside.java/tag/loom)
  - [GitHub](https://github.com/openjdk/loom)
  - [Java® Platform, Standard Edition & Java Development Kit - Version 18 API Specification](https://download.java.net/java/early_access/loom/docs/api/index.html)


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
You can toggle between edit and preview mode using the little control, circled in yellow.

![Code Documentation](IntelliJ-Code-Documentation.png)

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
and [Akka](https://akka.io/) by 2010, where I started learning
[Reactive](https://www.reactivemanifesto.org) Programming methods;
[Functional Programming](https://en.wikipedia.org/wiki/Functional_programming),
Non-Blocking Code, Futures, Actors, Streams with Backpressure, etc. Several large Akka Projects
I worked on, I loved starting the project and watching all the CPU Cores go to 100% initially,
demonstrating how effectively Reactive Systems could utilize resources. After initializing,
the CPU load would drop to about 10% or so until you started up some performance tests.

Note, when we talk about Non-Blocking, there are several aspects to this.

- [Non-blocking Algorithms](https://en.wikipedia.org/wiki/Non-blocking_algorithm) are generally used to
  implement Non-Blocking Data Structures.
- Non-Blocking Code generally refers to a style of programming that never explicitly waits or blocks
  the progress of the underlying thread of code. For example, in the popular
  [Node.js](https://en.wikipedia.org/wiki/Node.js) system, conceptually there is only one thread of
  execution, where the mantra "Never Block the Event Thread" reigns supreme. Consequently, the underlying
  mechanism of callbacks is used wherever we might have to wait or block for some result. Pathologically,
  it is easy to make mistakes, block the Event Thread, and spend time in *'Callback Hell'* at runtime
  trying to troubleshoot problems.

After all this, the most important lesson I learned was it is generally safer and saner to program at the
higher levels of abstraction, Futures, Actors, and Streams al la Akka, than to directly mess with Threads,
`syncronized`, Locks, and other lower-level mechanisms.

While it is clear how well these Reactive systems performed, at the raw code level, sometimes,
we really had to tangle ourselves up with hard to read programming styles, especially because
of Non-Blocking and other requirements.

- In concurrent frameworks, it was not safe to throw Exceptions because the
  legacy Java Concurrency Framework did not deal with Exceptions as completely.
  - In Scala, a functional approach was used to return composite results such as Option, Either, Try, etc.
    that indicated *either* success or failure. However, this can tend to clutter up the code by forcing
    us to deal with failure closer to the source, rather than outside in catch blocks.
- As above, callback-hell could be a real problem too, making code harder to read and reason about,
  especially for people not familiar to it.
  - Even for people familiar with callbacks, functional programming, etc. we could also experience some
    cognitive overload in trying to reason clearly about concurrency.
  - Scala had some amazing constructs, such as for-comprehensions to help us better deal with
    Future values and callbacks, but these constructs do not appear in Java, and are problematic in
    Kotlin such as Arrow.
- 

The miracle of Project Loom is the JVM itself had been modified to support Virtual Threads that
are astonishingly cheap and effective, creating a new Paradigm Shift from Non-Blocking to
'Let It Block,' because now the JVM can dance around Blocking APIs, and efficiently deal with all
the necessary concurrency. *What's Old, is New again.*

This Git Project is a playground where I can learn how to use Project Loom, and maybe share some
of that learning.


# Lessons Learned

## Abstraction

Generally I have found that higher levels of Abstraction provide better and safer ways to reason
about, design, and implement Concurrency.

- Parallel Streams are a high form of Abstraction
    - But favours Tasks that are Independent and Associative
      - Independent in that they do not need coordination
      - Associative in that the order of work is not important
    - Java Streams may or may not be Parallel, but are still a high level of Abstraction
      whether Parallel or not.
- Executors are a high form of Abstraction, where Streams are higher, and rely on Executors
  to implement their capabilities.
  - Executors work at the abstract level of Tasks, where a Task may use an entire Thread, or where
    several Tasks may execute on the same thread.
- Futures are another high form of Abstraction in that they represent an expected value at some
  point in time. In systems like Javascript and Node.js, the term Promises is used equivalently,
  as a promise for an expected value.
  - In Java, typically when an Executor spawns a Task, it returns a Future object that represents
    the state of the Task, including its ultimate success with a value, or failure.
  - In Scala, a Futures is a higher form of Abstraction than Java.
- Threads are a low form of abstraction, a mechanism of Concurrency, that may or may not run in
  parallel

## Concurrency vs Parallelism

> We could, therefore think of parallelism as the problem of scheduling resources over space and of
> concurrency as the problem of scheduling resources over time. I don’t know if that’s helpful,
> but it sure sounds cool.
> — [Ron Pressler](https://inside.java/2021/11/30/on-parallelism-and-concurrency/)

Dealing with Concurrency and Parallelism is like dealing with time and space, or spacetime.

With any collection of Tasks, we can either schedule these over time, over space, or both.

See also
[Parallel Streams, CompletableFuture, and All That: Concurrency in Java 8](https://www.youtube.com/watch?v=x5akmCWgGY0)

### Concurrency
- Consider a single CPU with only one core and no
  [Simultaneous Multithreading](https://en.wikipedia.org/wiki/Simultaneous_multithreading)
  as analogous to a single dimension
  of spacetime.
- In order to schedule multiple tasks, we need
  [time-division-multiplexing](https://en.wikipedia.org/wiki/Time-division_multiplexing).
  - Yes, I am using a telecommunications idiom because it solves basically the same problem.
  - In [CDMA](https://en.wikipedia.org/wiki/Code-division_multiple_access), a message can be
    broken up into pieces and each piece can be sent in parallel on different carrier frequencies.
    An analogy here is that a CPU is like a radio channel, where each CPU Core is like a
    sub-channel.
- We design for concurrency
  - Concurrency can be cooperative or preemptive.
  - Preemptive Concurrency is more powerful, but requires more resourced, has more overheads
    - This is basically the domain of Operating System processes and threads.
    - This more likely guarantees fair access to resources with less application design
  - Cooperative Concurrency is light-wight, requiring fewer resource, but requires cooperation
    - This is the domain of Loom Virtual Threads.
    - This less likely guarantees fair access to resources, where more application design is
      necessary
    - However, Virtual Threads provides a framework to make this aspect of application design
      easier.

### Parallelism

- Consider a multi-CPU system, where each CPU has several Cores, and each Core can support
  [Simultaneous Multithreading](https://en.wikipedia.org/wiki/Simultaneous_multithreading)
  as multiple dimensions of spacetime.
- Tasks actually run simultaneously in each different dimension
  - where we can also do time-division-multiplexing in each dimension.
- Once we have already solved Concurrency, Parallelism is generally easy to solve

> "Parallelism is strictly an optimization" — Brian Goetz

- Program Correctness is more important than Program Optimization.

### Conclusions

- Concurrency is a capability, while parallelism is a performance optimization that requires concurrency
- Best to test without parallelism first to make sure the logic is correct
    - invoking parallelism can introduce problems beyond concurrency

## Structured Concurrency

> Structured concurrency is a programming paradigm aimed at improving the clarity, quality,
> and development time of a computer program by using a structured approach to concurrent programming.

— [Wikipedia](https://en.wikipedia.org/wiki/Structured_concurrency) 2021-10-28

In Akka it was possible to set up a hierarchy of Actors to support a philosophy of *'let it fail.'*
While this was a Structured Concurrency mechanism, it could be truly difficult to set up, and wrap your
mind around what was going on. This could also be true in Project Loom, and I hope to explore this later,
but for now, in Project Loom you can write something like

    // Parent Thread, Create a Concurrency Context
    try (var executorService = Executors.newThreadPerTaskExecutor(virtualThreadFactory)) {
        IntStream.range(0, 15).forEach(item -> {
            System.out.printf("item %s, Thread Signature %s\n", item, Thread.currentThread());
            executorService.submit(() -> { // Child Thread
                System.out.printf("\ttask %s, Thread Signature %s\n", item, Thread.currentThread());
            })
        });
    } // Close Concurrency Context

where the parent thread, spawns a number of child threads, such that parent thread waits at the end
of the `try` block/wait for the child thread to complete, to 'join' back with the parent in terms of
concurrency.

    item 2, Thread Signature Thread[#1,main,5,main]
    item 3, Thread Signature Thread[#1,main,5,main]
    item 4, Thread Signature Thread[#1,main,5,main]
    item 5, Thread Signature Thread[#1,main,5,main]
        task 1, Thread Signature VirtualThread[#33]/runnable@ForkJoinPool-1-worker-2
        task 0, Thread Signature VirtualThread[#31]/runnable@ForkJoinPool-1-worker-9
    item 6, Thread Signature Thread[#1,main,5,main]
        task 2, Thread Signature VirtualThread[#34]/runnable@ForkJoinPool-1-worker-12
        task 3, Thread Signature VirtualThread[#36]/runnable@ForkJoinPool-1-worker-9

where

- `Thread[#1,main,5,main]` is our parent thread, the main startup thread
- `VirtualThread[#33]` is one of the child threads
- `runnable@ForkJoinPool-1-worker-2` is the Carrier Thread that Virtual Thread `#33` is running on.

Each time you run this code, the output will be ordered differently because the order is
nondeterministic, because concurrency itself is generally nondeterministic.

At some point in the code you could do something like

    executorService.shutdown();

which is documented as

> Initiates an orderly shutdown in which previously submitted tasks are executed,
> but no new tasks will be accepted. Invocation has no additional effect if already shut down.
> This method does not wait for previously submitted tasks to complete execution.
> Use awaitTermination to do that.

or you could do

    executorService.shutdownNow();

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

TODO - this is not correct...

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

In this project, I keep this in `LoomUtilities` as

    package net.kolotyluk.loom
    import java.util.concurrent.ExecutorService
    object LoomUtilities {
        fun ExecutorService.use(block: (executorService: ExecutorService) -> Unit) = block(this)
    }

so other files can use

    import LoomUtilities.use

