# Project Loom Lexicon

For [Ontology](https://en.wikipedia.org/wiki/Ontology)
and [Taxonomy](https://en.wikipedia.org/wiki/Taxonomy)
nerds like me, I try to offer my perspective on things. Much of what I offer
here are things I have learned and experience in experimenting with Project Loom, where I try to not only
provide a Loom slant on things, but also a consistent narrative aligned with the experiments in this
project. There are many other sources of information to consult on this rich subject-matter.

<!--- See https://ecotrust-canada.github.io/markdown-toc/ to generate TOC -->
- [Project Loom Lexicon](#project-loom-lexicon)
   * [Package java.util.concurrent](#package-javautilconcurrent)
   * [Thread](#thread)
      + [Platform Thread](#platform-thread)
      + [Virtual Thread](#virtual-thread)
      + [Carrier Thread](#carrier-thread)
      + [Interrupt](#interrupt)
      + [Blocking Transactions](#blocking-transactions)
      + [Non-Blocking Code](#non-blocking-code)
      + [Park](#park)
      + [Preempted](#preempted)
   * [Executor](#executor)
      + [Task](#task)
      + [Tasks vs Virtual Threads](#tasks-vs-virtual-threads)
      + [Structured Executor](#structured-executor)
         - [Session](#session)
         - [Completion](#completion)
         - [Completion Handlers](#completion-handlers)
         - [Shutdown On Failure](#shutdown-on-failure)
         - [Shutdown On Success](#shutdown-on-success)
         - [Custom Completion Handler](#custom-completion-handler)
      + [Flow](#flow)
      + [HTTP](#http)
   * [Future](#future)
      + [Completable Future](#completable-future)
      + [Cancel](#cancel)
   * [Shutdown](#shutdown)
   * [Scope Local](#scope-local)
- [Domain Driven Design](#domain-driven-design)
   * [Bounded Context](#bounded-context)
   * [Separation of Concerns](#separation-of-concerns)

<small><i><a href='http://ecotrust-canada.github.io/markdown-toc/'>Table of contents generated with markdown-toc</a></i></small>
   
See also

* <a href="https://bugs.openjdk.java.net/browse/JDK-8277129">JEP xxx Structured Concurrency</a>
* <a href="https://bugs.openjdk.java.net/browse/JDK-8277131">JEP xxx Virtual Threads</a>
* <a href="https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/StructuredExecutor.html">Class StructuredExecutor</a>
* <a href="https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/StructuredExecutor.ShutdownOnFailure.html">Class StructuredExecutor.ShutdownOnFailure</a>
* <a href="https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/StructuredExecutor.ShutdownOnSuccess.html">Class StructuredExecutor.ShutdownOnSuccess</a>
* <a href="https://download.java.net/java/early_access/loom/docs/api/java.base/java/lang/ScopeLocal.html">Class ScopeLocal</a>

## Package java.util.concurrent

[java.util.concurrent](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/package-summary.html)
summarizes much of Java Lexicon, Idioms, Conventions, and Practices around concurrency in terms of 
utility resources.

## Thread

[java.lang.Thread](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Thread.html)
Is a managed unit of concurrent execution that is the basis most Java Concurrency.

### Platform Thread

[Thread.ofPlatform()](https://download.java.net/java/early_access/loom/docs/api/java.base/java/lang/Thread.html#ofPlatform())
Is one way to specify a Thread managed by the underlying Operating System the Java Virtual Machine is running on. Tends to be
heavyweight and expensive to use.

### Virtual Thread

[Thread.ofVirtual()](https://download.java.net/java/early_access/loom/docs/api/java.base/java/lang/Thread.html#ofVirtual())
Is one way to specify a Thread managed by the JVM. Also known as
[User Thread](https://en.wikipedia.org/wiki/Thread_(computing)#User_threads) or
[Fiber](https://en.wikipedia.org/wiki/Thread_(computing)#Fibers),
Virtual Threads generally share the same APIs as legacy Platform Threads, but these are lightweight, and cheap to use.
In many cases, with minor refactoring, legacy code can realize impressive performance improvements by switching to
Virtual Threads.

However, when using Virtual Threads, it helps to reimagine your architecture and design to leverage not
only better performance, but better design, and better discipline; leading to better correctness,
better code readability and maintenance, etc.

For example, in the past where we might have used Thread Pools such as
[java.util.concurrent.ForJoinPool](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/ForkJoinPool.html)
to orchestrate a number of execution tasks over a limited number of Threads, it is now appropriate and
often more desirable to use one of the
[java.util.concurrent.Executors](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/Executors.html)
such as
[newThreadPerTaskExecutor(ThreadFactory threadFactory)](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/Executors.html#newThreadPerTaskExecutor(java.util.concurrent.ThreadFactory)),
that does not use a Thread Pool. For example, Virtual Thread Tasks with a lot of blocking operations, such as I/O, can
provide greater parallel concurrency than via a Thread Pool.

### Carrier Thread

A Platform Thread that 'carries' Virtual Threads, where the Virtual Threads are scheduled by the JVM.

### Interrupt

Threads can be interrupted, *invited to end prematurely,* but they cannot be forced to end prematurely.</dd>

### Blocking Transactions

Words like '***blocking***' and '***transactional***' are often used together to indicate code in a Thread that will
'***park***' the Thread, such as when I/O operations are performed.

### Non-Blocking Code

Non-Blocking code, *in this context,* refers to code that will never cause the Thread to block.

Because Platform Threads are an expensive resource that are managed by the host O/S, having too many blocked/parked
threads becomes correspondingly more expensive, often constraining application design and implementation.

For example, when using Java Parallel Streams, it is best that all tasks are non-blocking to best exploit
the design of Fork-Join. Another way of stating this is to say, avoid using *Transactional Tasks* with Parallel
Streams, and other Thread Pool Executors. The best way to say this is, when using *Transactional Tasks*, consider
using Virtual Threads as the cost of using them is substantially lower than with Platform Threads.

Much of the intent of Reactive Design is to facilitate non-blocking code that reduces the number of Platform
Threads used. However, often Reactive Frameworks give the illusion of blocking, while under the hood, they do
not actually cause the Thread to block.

### Park

Parking a Thread generally means, marking it as unschedulable because it is waiting for some background
condition to complete before it can progress again. Specifically, operations such as
[LockSupport.park()](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/locks/LockSupport.html#park())
are used to explicitly park the thread until it is able to progress again.

### Preempted

Platform Threads can be preempted by the Operating System, but this is generally not visible to the JVM as
this is a concern of the O/S not the JVM.

## Executor

[java.util.concurrent.Executor](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/Executor.html)
— Executors are a higher level of Concurrency abstraction added in Java 5, and since then, using Executors
instead of dealing with Threads directly is a '*better*' practice.

### Task

Executors execute
[FutureTasks](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/FutureTask.html)
and return
[Futures](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/Future.html)
as handles to the tasks. Initially tasks were implemented as Runnable, and then were extended to handle Callable,
where both are wrapped with a `FutureTask` object.

    FutureTask runnableFuture = new FutureTask(runnable);
    executor.execute(future);

or

    FutureTask<Integer> callableFuture = new FutureTask<Integer>(callable);
    executorService.submit(callableFuture);

[Callable](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/Callable.html)
should be preferred over
[Runnable](https://download.java.net/java/early_access/loom/docs/api/java.base/java/lang/Runnable.html)
because

1. Callable returns a value, and even if the value is void or null, it's better to be clear on whether
   there is a result or not
2. Callable can throw a checked exception, whereas Runnable cannot, so it supports failure handling better

Since Java 8, Tasks can also be expressed with Lambdas

    executorService.execute(() -> System.out.println());
    Future<?> futureRunnable = executor.submit(() -> System.out.println("Hello World!"));
    Future<Integer> futureCallable = executorService.submit(() -> 2 * 3);

which is encouraged for more elegant code with less boilerplate.

Tasks may be mapped 1:1 to Threads, or they may just be executed sequentially by threads in a
Thread Pool, and that is determined by the implementation of the Executor. For example, in
[ForkJoinPool](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/ForkJoinPool.html)
each Thread may execute many Tasks; when they complete one Task, they can execute the next queued Task.
A task completes with a result, an exception, or it is cancelled. If a worker Thread runs out of queued
Tasks, it will steal Tasks from other workers.

### Tasks vs Virtual Threads

In a sense, Virtual Threads blur the distinction between Tasks and Threads because Virtual Threads are just as
cheap to use a Thread Pool Tasks, but they have more capabilities. Indeed, some new Executors include
[newThreadPerTaskExecutor()](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/Executors.html#newThreadPerTaskExecutor(java.util.concurrent.ThreadFactory))
and
[StructuredExecutor](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/StructuredExecutor.html)
where there is a 1:1 mapping between Tasks and Threads.

Note: it is possible to use Platform Threads with `ThreadPerTaskExecutor` and `StructuredExecutor`, but we should be
clear on some compelling advantage for doing so, such as Platform Threads are pre-emptively scheduled, whereas
Virtual Threads are cooperatively scheduled.

### Structured Executor

[StructuredExecutor](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/StructuredExecutor.html)
is a new class of Executor that which provides better concurrent programming discipline.

Note: StructuredExecutor extends `java.lan.Object` and implements `Executor` and `AutoClosable`
because it is quite different from the legacy Executors. It is similar to
[newThreadPerTaskExecutor()](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/Executors.html#newThreadPerTaskExecutor(java.util.concurrent.ThreadFactory))
where there is a 1:1 mapping between Tasks and Threads.

#### Session

A lifecycle context initiated by `StructuredExecutor.open()` that defines several critical
non-overlapping phases

1. [open()](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/StructuredExecutor.html#open(java.lang.String,java.util.concurrent.ThreadFactory))
   to open a new Session, and start the lifecycle of the session.
2. [fork()](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/StructuredExecutor.html#fork(java.util.concurrent.Callable,java.util.function.BiConsumer))
   to spawn new tasks.
3. [join](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/StructuredExecutor.html#join())
   [joinUntil(Instant)](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/StructuredExecutor.html#joinUntil(java.time.Instant))
   to block/wait for all forked Tasks to complete with either success or failure.
4. [throwIfFailed()](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/StructuredExecutor.ShutdownOnFailure.html#throwIfFailed())
   to proceed to exception handling if there are any failures.
5. [resultNow()](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/Future.html#resultNow())
   to collect successful results.
6. [close()](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/StructuredExecutor.html#close())
   release all the resources acquired within the try-with-resources block.

At this time, a session is an implicit concept, with no separately exposed Session object to interact with,
where the Executor is basically also the Session.

See [Experiment00](https://github.com/kolotyluk/loom-lab/blob/master/laboratory/src/main/java/net/kolotyluk/loom/Experiment00.java)
for a documented example of these concepts that you can play with.

    var virtualThreadFactory = Thread.ofVirtual().factory();

    try (var structuredExecutor = StructuredExecutor.open("Experiment00", virtualThreadFactory)) {
        var completionHandler = new StructuredExecutor.ShutdownOnFailure();

         var futureResults = IntStream.range(0, 15).mapToObj(item -> {
             System.out.printf("item = %d, Thread ID = %s\n", item, Thread.currentThread());
             return structuredExecutor.fork(() -> {
                 System.out.printf("\ttask = %d, Thread ID = %s\n", item, Thread.currentThread());
                 return item;
             }, completionHandler);
         });

         structuredExecutor.joinUntil(Instant.now().plusSeconds(10));
         completionHandler.throwIfFailed();
         var completedResults = futureResults.map(Future::resultNow).toList();
     }
     catch  (InterruptedException e) {
         // thrown from join() and joinUntil() if we're being interrupted
     } catch (ExecutionException e) {
         // thrown from throwIfFailed() if any of the children failed with an exception
     } catch (TimeoutException e) {
         // thrown from joinUntil() if the deadline is exceeded
     } catch (IllegalStateException e) {
         // thrown from resultNow() if the Future is not completed, or the Task failed,
         // but this should never happen if join() and throwIfFailed() have been called first.
     }


#### Completion

When using the 2-arg fork method, the onComplete operation is invoked when the task completes,
irrespective of whether it completed with a result, exception, or was cancelled.

1. Success, with the value of the Callable.
2. Failure, with an exception.
3. Someone aborted, a subclass of Failure,
4. Shutdown.

#### Completion Handlers

Completion handlers allows us to factor out policies for the common and simple cases where we need to
collect results or shutdown the executor session based on the task’s success or failure. A call to `shutdown`
indicates that the computation is done — either successfully or unsuccessfully — and so there’s no point
in processing further results. In more complicated — and, we believe, much rarer — cases, like the connection
example in the javadoc, the completion handler is, indeed, insufficient, and we’d want to do cleanup
processing inside the task and possibly call shutdown directly.


#### Shutdown On Failure

[StructuredExecutor.ShutdownOnFailure](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/StructuredExecutor.ShutdownOnFailure.html)
is for the situation where we want to shut down the session for any failure.

#### Shutdown On Success

[StructuredExecutor.ShutdownOnSuccess](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/StructuredExecutor.ShutdownOnSuccess.html)
is for the situation where we have a sufficient result and are no longer interested in further results
from other tasks in the session.

#### Custom Completion Handler

### Flow

[Flow](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/Flow.html)
is Java's framework for Reactive Streams, that uses Executors for handling asynchronous/concurrent programming
requirements.

### HTTP

[java.net.http](https://download.java.net/java/early_access/loom/docs/api/java.net.http/module-summary.html), and
[com.sun.net.httpserver](https://download.java.net/java/early_access/loom/docs/api/jdk.httpserver/com/sun/net/httpserver/package-summary.html)
are modules that handles various HTTP client/server capabilities. Notably, they both use
[Flow.Publisher](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/Flow.Publisher.html),
[Flow.Subscriber](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/Flow.Subscriber.html),
etc. to handle unique payloads (HTTP Body).

## Future

The `Future` objects returned as a result of
[ExecutorService](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/ExecutorService.html)
`submit` and `invokeAll`, or
[StructuredExecutor](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/StructuredExecutor.html)
`fork` can be used to interrogate the state of the running task, get the result, etc.

### Completable Future

If you would rather have a 
[CompletableFuture](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/CompletableFuture.html)
you can use

    var completableFuture = CompletableFuture.runAsync(myRunnable, myExecutor); // or
    var completableFuture = CompletableFuture.supplyAsync(mySupplier, myExecutor);

where `myRunnable` and `mySupplier` are the tasks that are executed to satisfy the `CompletableFuture`;
[Supplier](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/function/Supplier.html)
is like
[Callable](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/Callable.html)
in that it returns a value; and in either case you can choose your own `Executor`, including Project Loom
Executors.



### Cancel

[Future#cancel(boolean)](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/Future.html#cancel(boolean))
attempts to cancel the execution of the underlying task.

***This can be very subtle as demonstrated in Experiment02***

Within the task, to detect cancellation

    try {
        // some computation... 
        // some blocking operations such as Thread.sleep()
        // some computation...
    }
    catch (CancellationException e) {
        return "some cancellation result";
    }
    catch (InterruptedException e) {    // future.cancel(true);
        return "some cancellation result";
    }

In this case the `CancellationException` will the caught if

1. The future is cancelled before the task is run, or
2. The future is cancelled while the task is running, and there are blocking operations

The `InterruptedException` will likely not be caught as this is more subtle that we may think...



## Shutdown

*Shutdown is the concurrent execution analogue to a `break` or `throw`
statement in sequential loop.*

[ExecutorService#shutdown()](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/ExecutorService.html#shutdown())
method closes the front door to prevent new tasks from starting via `execute()`, `submit`, or `fork`, but
all tasks that have already been queued for execution will be allowed to complete.

[ExecutorService#shutdownNow()](https://download.java.net/java/early_access/loom/docs/api/java.base/java/util/concurrent/ExecutorService.html#shutdownNow())
is like `shutdown()`, but also cancels any tasks that have not completed yet.

It also interrupts the threads
that are running the tasks that
haven't completed yet. It also tries to make it clear that when shutdown
completes that are tasks are "done" (it links to Future::isDone). You
shouldn't need to use Future::get with this API but if you were then you
should see that Future::get wakes up when SE::shutdown is called.


## Scope Local

[ScopeLocal](https://download.java.net/java/early_access/loom/docs/api/java.base/java/lang/ScopeLocal.html)
is a new concept introduced in JDK18, and used by Structured Concurrency to maintain a hierarchy of values
and the scope they are defined in.

# Domain Driven Design

In
[Domain Driven Design](https://en.wikipedia.org/wiki/Domain-driven_design),
concepts such as
[Lexicon](https://en.wikipedia.org/wiki/Lexicon),
[Ontology](https://en.wikipedia.org/wiki/Ontology), and
[Taxonomy](https://en.wikipedia.org/wiki/Taxonomy)
are helpful in defining our
[Domain](https://en.wikipedia.org/wiki/Domain_(software_engineering)),
and our ***Domain*** is ***Java Concurrent Programming***.

As an advocate of good design, including Domain Driven Design, I believe that all good design should be clear
on the domain, contexts, models, etc., and that if this is not documented adequately, then the design is left
open to ambiguity, misunderstanding, misuse, and even abuse.

## Bounded Context

> Bounded Context is a central pattern in Domain-Driven Design. It is the focus of DDD's strategic design section
> which is all about dealing with large models and teams. DDD deals with large models by dividing them into different
> Bounded Contexts and being explicit about their interrelationships.
> — [Martin Fowler](https://martinfowler.com/bliki/BoundedContext.html)

## Separation of Concerns

# More Research Needed

- [TransactionalCallable](https://docs.oracle.com/middleware/12212/osb/java-api/com/bea/wli/config/transaction/TransactionalCallable.html)
- [Java – Using ThreadPoolExecutor with BlockingQueue](https://howtodoinjava.com/java/multi-threading/how-to-use-blockingqueue-and-threadpoolexecutor-in-java/)
- 