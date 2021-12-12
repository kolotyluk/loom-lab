# Project Loom Advantages

While there are no new features in the Java Language, Project Loom involves critical improvements to the Java
Concurrency APIs and the ***Java Virtual Machine Runtime*** that improves Concurrent Programming performance.
While many other frameworks have offered improved Concurrent Programming solutions, such as
[Akka](https://en.wikipedia.org/wiki/Akka_(toolkit)),
[Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html),
[Reactive Streams](http://www.reactive-streams.org) and
[Java Flow](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/Flow.html),
etc., none of these have had the capability of changing the underlying JVM (except maybe Flow).

## History

In the early days of the World Wide Web, we would configure our Operating System to *listen*
on port 8000 or 8080 for an incoming *HTTP Request*, start an O/S Process to handle that request,
return an *HTTP Response*, such as an *HTML Page*, then terminate when done. This was operationally
fairly simple to set up, but it did not scale well because of the overhead of O/S Processes.

To improve on this, we would create an HTTP Service Process, that would start, then listen
on port 8000, etc., and for each incoming HTTP Request, would typically start a new O/S Thread
in that O/S Process to handle the request. In this way, one Process could handle multiple
requests concurrently, such that cheap requests did not have to suffer/wait for expensive requests.

However, even while O/S Threads do not have as much overhead as O/S Processes, they still have
substantial overhead, and so there were limits to how well this strategy could scale.

Historically, there were many attempts to get around these scaling constraints, such as
[Java Enterprise Edition](https://en.wikipedia.org/wiki/Jakarta_EE).

<p style="padding-left: 40pt; padding-right: 40pt; font-style: italic;font-family: 'Times New Roman', Times, serif;">
    In my humble opinion, Java EE is a good example of 'The road to hell is paved with good intentions.'
    While it did succeed in scaling better, using hardware resources better, it quickly became
    a bloated confusing mess of yet another idea... While I have implemented solutions in J2EE,
    for the most part I have done my best to avoid it, but I have first hand experience of how
    truly awful it is. However, we learn more from our mistakes than our successes.
</p>

[Ruby on Rails](https://en.wikipedia.org/wiki/Ruby_on_Rails)
was one of the first successful attempts to resolve the problems with J2EE. This quickly inspired
[Groovy on Grails](https://en.wikipedia.org/wiki/Grails_(framework)), 
[Typesafe Play](https://en.wikipedia.org/wiki/Play_Framework), etc., where all these frameworks
offered improved scalability, without the bloated complexity of J2EE.

Personally, I have used
[Play](https://en.wikipedia.org/wiki/Play_Framework) with
[Akka](https://en.wikipedia.org/wiki/Akka_(toolkit)) and
[Scala](https://en.wikipedia.org/wiki/Scala_(programming_language))
to build some high performance web services, and it was very satisfying starting a substantial
Akka node, watching all the CPU Cores and CPU Threads go to 100% briefly while the system initialized,
then return to nominal CPU utilization after initialization. My interpretation is that Akka is
very good with concurrency, that can manifest as parallelism.

However, to achieve good concurrency in Akka takes some more sophisticated paradigms such as
[Reactive Programming](https://en.wikipedia.org/wiki/Reactive_programming)
employing techniques such as non-blocking programming, using Futures with callbacks, Actors, Akka Streams,
etc. In particular, we have to give up on using Java Exceptions, except in well understood situations.
Consequently, Reactive Programming is like leaving the comforting home of Kansas (imperative style),
and travelling to the confusing land of
[Oz](https://en.wikipedia.org/wiki/The_Wizard_of_Oz_(1939_film))
(reactive style).

To be fair, learning Reactive Programming is something we all should learn, as even with Project Loom,
there are still situations where Reactive Programming is valuable. However, Project Loom does give us
more opportunities to enjoy some advantages of imperative programming that reactive programming does not.

These days
[Spring](https://en.wikipedia.org/wiki/Spring_Framework) is one of the most commonly used Web Application
Frameworks.

It is well known that Spring Applications can become quite bloated, and take a very long time
to initialize and get to the operational state. One place I worked, some of our Spring micro-services
could take minutes to start.
[Compared to Micronaut](https://micronaut.io/2020/04/28/practical-performance-comparison-of-spring-boot-micronaut-1-3-micronaut-2-0/),
Spring does not give us as good Throughput, does not make as good use of our expensive hardware
resources as other frameworks such as
[Micronaut](https://micronaut.io).

<p style="padding-left: 40pt; padding-right: 40pt; font-style: italic;font-family: 'Times New Roman', Times, serif;">
    In my humble opinion, Spring is yet another very overly complex mess that reminds me of J2EE.
    From
    <a href="https://www.quora.com/What-are-some-criticisms-of-the-Spring-Framework">
        What are some criticisms of the Spring Framework?
    </a>,
    we can see that others have valid criticism too. My major complaint about Spring is that in
    order to become effective with Spring, you first need a Ph.D from the Theological Collage of Spring.
    I do not doubt that Spring will benefit from Project Loom, but it will still be Spring, and all the
    other frameworks that exploit Project Loom, will still be ahead of Spring.
</p>

The bottom line is that for decades, we tried to invent better *strategies* and *tactics* of getting
around the constraints of O/S Threads, but with Project Loom, where Virtual Threads offer improved
concurrency using the same API, we can eliminate or reduce the use of some of these more complex
*strategies* and *tactics*, and the *logistics* of supporting them (such as documentation and training).

Project Loom is a good example of *what is old, is new.* We can reuse old strategies and APIs because of new
and improved implementations. Indeed, we can go back to the original Strategy of a single Service Process that
listens on port 8000, and starts a new Virtual Thread for each HTTP Request, as this will scale much better
than before. An analogy I would like to offer is that Bubble Sort and Quicksort have the same API, but the
implementations offer strikingly different performance.

Finally, while this narrative has focused on Web Applications, such as HTTP Servers, this by
no means implies that Project Loom does not have other applications too. For example, any
application that can benefit from concurrency, but also has to handle transactions, such
as blocking operations like I/O, can benefit from Project Loom.

Project Loom does not directly improve Parallelism, where Java Parallel Streams are still the
best tool for this; however, applications that use Java Parallel Stream, with transactions,
could benefit from being refactored to use Virtual Threads.

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
