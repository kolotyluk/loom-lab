# Video Resources

## 2020-09-12
- [Project Loom: Modern Scalable Concurrency for the Java Platform](https://www.youtube.com/watch?v=fOEPEXTpbJA)
- [Java](https://www.youtube.com/channel/UCmRtPmgnQ04CMUpSUqPfhxQ)
> Project Loom: Modern Scalable Concurrency for the Java Platform
>
> Concurrent applications, those serving multiple independent application actions simultaneously,
> are the bread and butter of Java server-side programming. The thread has been Java’s primary unit
> of concurrency since Java’s inception, and is a core construct around which the entire Java platform
> is designed, but its cost is such that it can no longer efficiently represent a domain unit of
> concurrency, such as the session, request or transaction. As a result, Java has seen a proliferation
> of libraries and frameworks that offer scalable concurrency at the cost of abandoning the thread as
> the unit of software concurrency and, with it, the straightforward support of Java’s troubleshooting
> observability tooling, such as stack dumps, debuggers and profilers. Project Loom aims to reinstate
> the thread as an efficient unit of concurrency by adding a lightweight implementation of threads to
> the Java platform, which would allow straightforward code—that’s easy to write, understand and maintain,
> and works in harmony with the platform and its tooling—to scale and meet the requirements of even most
> demanding concurrent applications.
>
> Ron Pressler, Consulting Member of Technical Staff, Oracle

### Conclusions

- Project Loom will best help you for I/O Concurrency, but not for CPU Bound Concurrency

## 2021-12-17
- [Project Loom Brings Structured Concurrency - Inside Java Newscast #17](https://www.youtube.com/watch?v=2J2tJm_iwk0)
- [Java](https://www.youtube.com/channel/UCmRtPmgnQ04CMUpSUqPfhxQ)

### Conclusions

- A good introduction to Structured Concurrency
- A good reminder of the distinction between Concurrency and Parallelism

## 2019-07-18
- [Ron Pressler - Loom: Bringing Lightweight Threads and Delimited Continuations to the JVM](https://www.youtube.com/watch?v=r6P0_FDr53Q)
- [Curry On!](https://www.youtube.com/channel/UC-WICcSW1k3HsScuXxDrp0w)

### Conclusions

- This is seminal grounding in *what problems does Project Loom solve?*
- Contains a lot of interesting Computing Science Theory and Experience

## 2019-09-06
- [Java Concurrency Interview - What is an Ideal Threadpool size?](https://www.youtube.com/watch?v=ErNre5varF8)
- [Defog Tech](https://www.youtube.com/channel/UCiz26UeGvcTy4_M3Zhgk7FQ)
> Answer - the size depends on number of CPU cores and whether the task is CPU bound or IO bound.
>
> For CPU bound (CPU intensive) tasks, the ideal size is same as number of CPU cores.
>
> For IO bound tasks, ideal size depends on the wait time of the IO task. More the wait time, more number
> of threads can be used to ensure maximum CPU utilization.
>
> Channel
> 
> ----------------------------------
> Complex concepts explained in short & simple manner. Topics include Java Concurrency,  Spring Boot,
> Microservices, Distributed Systems etc. Feel free to ask any doubts in the comments. Also happy to
> take requests for new videos.
>
> Subscribe or explore the channel - https://youtube.com/defogtech
>
> New video added every weekend.
>
> Popular Videos
>
> ----------------------------------
> What is an API Gateway - https://youtu.be/vHQqQBYJtLI  
> Executor Service - https://youtu.be/6Oo-9Can3H8  
> Introduction to CompletableFuture - https://youtu.be/ImtZgX1nmr8  
> Java Memory Model in 10 minutes - https://youtu.be/Z4hMFBvCDV4  
> Volatile vs Atomic - https://youtu.be/WH5UvQJizH0  
> What is Spring Webflux - https://youtu.be/M3jNn3HMeWg  
> Java Concurrency Interview question  - https://youtu.be/_RSAS-gIjGo  

### Conclusions

- Ideal Thread Pool Size depends on Use Case
  - CPU Bound Tasks generally do not need more threads per pool than the number of ***Available*** CPU
    Cores, where a CPU Core could be virtual, as in the case of
    [Hyper-Threading](https://en.wikipedia.org/wiki/Hyper-threading) or similar technology
  - I/O Bound Tasks may benefit from more threads per pool than the number of ***Available*** CPU
    Cores, but it depends on the Blocking Coefficient: \[1 + Wait Time / CPU Time]
    - Ideal Thread Count = Number of Cores X \[1 + Blocking Coefficient]
  - [How to set an ideal thread pool size](https://engineering.zalando.com/posts/2019/04/how-to-set-an-ideal-thread-pool-size.html)
- Use Different Thread Pools for Different Use Cases
- ***Available*** CPU Cores depends on context.
  - On your laptop or desktop computer, you will generally have access to all physical CPU Cores
  - Unless you are running a virtual machine, such as
    [Docker](https://en.wikipedia.org/wiki/Docker_(software)),
    where the Docker Configuration determines how many CPU Cores are Available
  - On a Cloud Application, it will likely be running under
    [Kubernetes](https://en.wikipedia.org/wiki/Kubernetes),
    and the Kubernetes Configuration determines how many CPU Cores are Available
- Consequently, when performance testing Concurrent/Parallel code, test it in the environment it will
  be running in, not the environment it is developed in.
  - This is a deployment risk where the DevOps people may undermine the design of the application
    when they do not understand the CPU Core expectations of the software designer, and may make
    arbitrary changes the number of Available CPUs
  - Consequently, the software designer should bake in self optimizing calculations of Thread Pool
    Size based on the number of detected CPU Cores
    - *and then test in the environment it will be running it*

2017-11-08 2:34:29
- [Parallel and Asynchronous Programming with Streams and CompletableFuture by Venkat Subramaniam](https://www.youtube.com/watch?v=IwJ-SCfXoAU)
- [Devoxx](https://www.youtube.com/channel/UCCBVCTuk6uJrN3iFV_3vurg)
> Subscribe to Devoxx on YouTube @ https://bit.ly/devoxx-youtube  
> Like Devoxx on Facebook @ https://www.facebook.com/devoxxcom  
> Follow Devoxx on Twitter @ https://twitter.com/devoxx  
> 
> Java 8 makes it relatively easy to program with parallel streams and to implement asynchronous tasks using CompletableFuture. When someone says it’s easy, cautious programmers ask “What’s the catch?” Well, of course, that’s one of the major topic we’ll address in this deep dive session. We will start with a fast introduction to parallel and asynchronous programming with Java 8 and quickly delve into the gotchas, when to use these facilities, when to avoid them, and how and where to put them to good use.

Dr. Venkat Subramaniam is an award-winning author, founder of Agile Developer, Inc., creator of agilelearner.com, and an instructional professor at the University of Houston. He has trained and mentored thousands of software developers in the US, Canada, Europe, and Asia, and is a regularly-invited speaker at several international conferences. Venkat helps his clients effectively apply and succeed with sustainable agile practices on their software projects.

Venkat is a (co)author of multiple technical books, including the 2007 Jolt Productivity award winning book Practices of an Agile Developer. You can find a list of his books at agiledeveloper.com. You can reach him by email at venkats@agiledeveloper.com or on twitter at @venkat_s


2017-10-02
- [Parallel Streams, CompletableFuture, and All That: Concurrency in Java 8](https://www.youtube.com/watch?v=x5akmCWgGY0)
- [Java](https://www.youtube.com/channel/UCmRtPmgnQ04CMUpSUqPfhxQ)
> Kenneth Kousen, President, Kousen IT, Inc.
>
> The Java 8 (and 9) standard library includes multiple techniques for taking advantage of parallelism
> and concurrency, including parallel streams and the CompletableFuture class. Combined with the
> ExecutorService and other classes from java.util.concurrent, they give developers a wide range of
> choices for optimizing program speed. This session reviews the options and trade-offs involved.

2016-02-23
- [From Concurrent to Parallel](https://www.youtube.com/watch?v=NsDE7E8sIdQ)
- [Jfokus](https://www.youtube.com/channel/UCDG4jVD88QEjB8UKLYZKiNA)
> From Concurrent to Parallel
>
> As core counts continue to increase, how we exploit hardware parallelism in practice shifts from
> concurrency - using more cores to handle a bigger workload - to parallelism - using more cores to
> solve data-intensive problems faster. This talk will explore the different goals, tools, and
> techniques involved between these various approaches, and how to analyze a computation for
> potential parallelism, with specific attention to using the parallel stream library in Java SE 8.
>
> Brian Goetz, Oracle
>
> Brian Goetz is the Java Language Architect at Oracle, and was specification lead for JSR 335
> (Lambda Expressions for the Java Language.) He is the author of the best-selling book "Java
> Concurrency in Practice" and is a frequent presenter at major industry conferences.

