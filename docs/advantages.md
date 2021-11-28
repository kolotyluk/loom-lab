# Project Loom Advantages

While there are no new features in the Java Language, Project Loom involves critical changes to the 
Java Virtual Machine Runtime that improves Concurrent Programming. Many other systems have offered
Concurrent Programming solutions, such as Akka, Kotlin Coroutines, Project Reactor, etc., none of
these have the capability of changing the underlying JVM.



## Reuse of Thread API

While a key advantage of Project Loom is
[Structured Concurrency](https://bugs.openjdk.java.net/browse/JDK-8277129),
in many legacy applications is it not
easy or pragmatic to refactor the code to use Structured Concurrency. However, because
[Virtual Threads](https://bugs.openjdk.java.net/browse/JDK-8277131)
are semantically equivalent to legacy *Platform* Threads, it can very easy to retrofit a legacy
application with Virtual Threads and exploit the performance boost the Virtual Threads offer.

Essentially, the core feature that Project Loom offers is a new implementation of the legacy
Java Concurrent Programming APIs; a better, light-weight, more efficient implementation. Unlike
Platform Threads, that are implemented in the Operating System, where each O/S has its own
implementation, Virtual Threads are implemented and maintained by the 

## Avoid Complexity of Reactive Programming

The [Reactive Manifesto](https://www.reactivemanifesto.org/) was a great achievement in Concurrent
Programming, and while it really did make it easier to reason about concurrent systems, it also added
complexity in the functional non-blocking style, especially chaining Futures together with monadic 
operations, eliminating the use of Exceptions with try-catch-finally, 

## Thread Pools with Blocking Tasks

While it is possible to use Tread Pool Executors with Tasks that block on I/O calls and other and
other APIs, it can be problematic.

Virtual Threads are extremely well suited to solve this problem, in fact, it is one of the main design goals.
