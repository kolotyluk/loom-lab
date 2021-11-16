package net.kolotyluk.loom;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static net.kolotyluk.loom.Experiment03_PrimeStreams.getPrimes;
import static net.kolotyluk.loom.Experiment03_PrimeStreams.isPrime;

/**
 * TODO - Experiment with Java Flow
 * <h1>Experiment 3 - Prime Numbers</h1>
 * <p>
 *     Calculating Prime Numbers is always fun, and it's most fun when we try to compute prime numbers as
 *     fast as possible, or as many at once as we can. As we will see, Project Loom offers us no benefits in this
 *     <em>use case</em>, but it's interesting to experiment and see why.
 * </p>
 * <p>
 *     As I have mentioned before, with Concurrent Programming, higher levels of abstraction are generally better
 *     and safer because they have been designed and implemented by experts, and <em>it's better to stand on the
 *     shoulders of giants</em>. Generally, for CPU Bound use cases, the {@link Stream} Interface
 *     is a good place to start as it has been specifically design for such use cases.
 * </p>
 * <h2>Streams</h2>
 * <p>
 *     The Streams Architecture has three important phases
 *     <dl>
 *         <dt>Source</dt>
 *         <dd>Something that generates a stream of objects, such as a range of numbers, in our case,
 *             numbers that could be prime</dd>
 *         <dt>Pipeline</dt>
 *         <dd>A sequence of Functions on each object in the stream, in our case, testing if a number is prime,
 *             which leverages the {@link Stream#filter(Predicate)} function.</dd>
 *         <dt>Sink</dt>
 *         <dd>Ultimately, we need somewhere to <em>Collect</em> the Stream of objects. This is also known
 *             as *Terminating* the Stream.</dd>
 *     </dl>
 *     Note: Unlike <a href="https://doc.akka.io/docs/akka/current/stream/index.html">Akka Streams</a>,
 *     Java Streams are not Infinite, they must terminate, and indeed, the Pipeline does not start until
 *     it is actually terminated with some collection function. The Java {@link Flow} is more akin to
 *     Akka Streams, and in Akka Streams, they actually use the term <em>Flow</em>. Hopefully later we
 *     can see the effects of running Java Flows with Project Loom...
 *     <blockquote>
 *         There are only two hard things in Computer Science: cache invalidation and
 *         <strong><em>naming things</em></strong>.
 *         <p>— Phil Karlton</p>
 *     </blockquote>
 * </p>
 * <h2>Threads</h2>
 * <p>
 *     For the rest of the experiments, we can conclude that that attempts to use Threads to do better than basic
 *     {@link Stream#parallel()} generally fail. We can also see that the code is substantially more
 *     complicated too...
 * </p>
 * <p>
 *     However, where threads really shine is when we are dealing with concurrency where there are a lot of blocking
 *     operations, such as transaction processing, network access, etc. Every blocking operation is an opportunity for
 *     some other task to run and progress.
 * </p>
 * <p>
 *     For these experiments we warp our primes experiments into a pseudo networking application, where we simulate
 *     farming <tt>isPrime()</tt> out to HTTP Endpoints. This simulation basically wraps the
 *     {@link Primes#isPrime(long, long, long)} calculation with network latency via
 *     {@link Thread#sleep(long)}, one to simulate Request Latency, another to simulate Response
 *     Latency.
 * </p>
 * <p>
 *     The spirit of the experiments here is that we might have some application that is computationally expensive and
 *     takes a long time. While {@link Stream#parallel()} works just fine on limited scale, we might imagine a magic
 *     cloud that has some <tt>/isPrime</tt> endpoints where the work can be done faster, more efficiently, etc. Indeed,
 *     this is a classic
 *     <a href="https://en.wikipedia.org/wiki/MapReduce">MapReduce</a>
 *     model. To be sure, if we really wanted to generate prime numbers in a
 *     <a href="https://en.wikipedia.org/wiki/High-performance_technical_computing">High-Performance Computing</a>
 *     style, we might use something like <a href="https://en.wikipedia.org/wiki/Apache_Spark">Apache Spark</a>,
 *     but here we are trying to keep things simple, and make a point, not a perfect application.
 * </p>
 * <h1>Benchmarking</h1>
 * <p>
 *     When trying to compare the performance of different Concurrent and Parallel approaches, it's important to
 *     conduct <a href="https://en.wikipedia.org/wiki/Benchmark_(computing)">benchmarks</a>, and one tool to use is
 *     the <a href="https://github.com/openjdk/jmh">Java Microbenchmark Harness (JMH)</a>. However, it can take quite
 *     a long time for JMH to run benchmarks. By default, JMH runs 25 warmups, and 25 benchmarks, then averages the
 *     results. For quicker results, the experiments in this code take a rather casual approach to get a feel for things,
 *     leaving the heavy lifting to JMH.
 * </p>
 * <h2>Streams</h2>
 * <p>
 *     See <tt>benchmarks/PrimeNumbers</tt> for the code that runs these the JMH Benchmarks.
 * </p>
 * <pre>
 * Benchmark                                  Mode  Cnt        Score        Error
 * PrimeNumbers.parallelPrimesTo_1000         avgt   25       46.620 ±      1.549
 * PrimeNumbers.parallelPrimesTo_10_000       avgt   25      379.036 ±      7.604
 * PrimeNumbers.parallelPrimesTo_10_000_000   avgt   25  1360362.823 ±  14723.084
 * PrimeNumbers.serialPrimesTo_1000           avgt   25       32.998 ±      0.315
 * PrimeNumbers.serialPrimesTo_10_000         avgt   25      644.467 ±     16.160
 * PrimeNumbers.serialPrimesTo_10_000_000     avgt   25  8199848.272 ±  84514.067
 * </pre>
 * <p>
 *     where the <tt>Score</tt> is the Average Microseconds (μS) to complete the run. Given we only test odd numbers
 *     we can conclude from testing numbers if they are prime, where throughput = tests per μS
 * </p>
 * <pre>
 * Benchmark                                     tested  throughput  ratio
 * PrimeNumbers.parallelPrimesTo_1000               500      10.725  0.708
 * PrimeNumbers.parallelPrimesTo_10_000           5,000      13.191  1.700
 * PrimeNumbers.parallelPrimesTo_10_000_000   5,000,000       3.675  6.025
 * PrimeNumbers.serialPrimesTo_1000                 500      15.152  1.413
 * PrimeNumbers.serialPrimesTo_10_000             5,000       7.758  0.588
 * PrimeNumbers.serialPrimesTo_10_000_000     5,000,000       0.610  0.166
 * </pre>
 * <p>
 *     From this we can conclude
 * </p>
 * <nl>
 *     <li>
 *         For smaller numbers of computations, O(1,000), Serial Stream has more throughput than Parallel Stream.
 *         This is because, Parallel Stream requires Concurrency, and Concurrency has overhead.
 *     </li>
 *     <li>
 *         For larger numbers of computations, O(10,000), Parallel Stream has more throughput than Serial Stream.
 *         This is because the power of parallelism overcomes the overhead of concurrency.
 *     </li>
 *     <li>
 *         For really large numbers of computations, O(10,000,000), Parallel Stream really shines over Serial Stream.
 *         In this use case, it is computationally more expensive to test large numbers if they are prime, so the
 *         dominant factor is raw CPU, and in this case, computation has been spread across 12 CPUs.
 *     </li>
 *     <li>
 *         For O(10,000,000) computations we can see the throughput of Parallel Streams is much lower than O(10,000),
 *         but still much better than O(10,000,000) Serial Stream, so the throughput sweet spot for Parallel Stream
 *         in this use case is somewhere between O(10,000) and O(10,000,000). This is explained because it takes longer to test
 *         larger numbers if they are prime, therefore throughput will begin decreasing. This is why it's important
 *         to benchmark <em>real</em> applications, and not synthetic experiments. As we will see later, however,
 *         synthetic experiments do have their place.
 *     </li>
 * </nl>
 * <p>
 *     As an aside, computing primes up to
 *     <ul>
 *         <li>1,000, there are 167 primes</li>
 *         <li>10,000, there are 1228 primes</li>
 *         <li>10,000,000 there are 664578 primes</li>
 *     </ul>
 * </p>
 * <h2>Project Loom</h2>
 * <h3>Pure Computation</h3>
 * <p>
 *     When I first started playing with these experiments I naïvely thought that using Project Loom could improve
 *     upon the previous benchmarks. Even though I had already watched
 *     <a href="https://www.youtube.com/watch?v=r6P0_FDr53Q">Ron Pressler - Loom: Bringing Lightweight Threads and Delimited Continuations to the JVM</a>,
 *     I had not really appreciated or internalized that knowledge. But, by naïvely concocting my own experiments,
 *     I had empirical evidence that supported what Ron Pressler had already stated.
 * </p>
 * <p>
 *     Without using JMH, playing around here I discovered
 *     <nl>
 *         <li>
 *             Running {@link Stream#parallel()} within a VirtualThread ExecutorService context did not improve
 *             throughput. In most cases it was a little worse.
 *         </li>
 *         <li>
 *             Replacing <tt>Stream.parallel()</tt> with a VirtualThread ExecutorService that calls
 *             {@link ExecutorService#invokeAll(Collection)} has about 1/3 the throughput of <tt>Stream.parallel()</tt>
 *             for testing up to 10,000,000 primes.
 *         </li>
 *         <li>
 *             Replacing <tt>Stream.parallel()</tt> with a VirtualThread ExecutorService that calls
 *             {@link ExecutorService#submit(Callable)} has about 1/2 the throughput of <tt>Stream.parallel()</tt>,
 *             which is better than <tt>ExecutorService#.invokeAll(Collection)</tt>
 *         </li>
 *         <li>
 *             Comparing Parallel Streams to Virtual Threads is sort of like comparing oranges to grapefruit,
 *             but in a sense they are both citrus. However, comparing <tt>ExecutorService#.invokeAll(Collection)</tt>
 *             to <tt>ExecutorService.submit(Callable)</tt> is more like comparing navel oranges to mandarin oranges.
 *             While <tt>ExecutorService#.invokeAll(Collection)</tt> looks more attractive, it might not perform as
 *             well as we would expect. <em>This is still a naïve test, so it should be invested further with more
 *             rigour and analysis.</em>
 *         </li>
 *     </nl>
 * </p>
 * <h3>Simulated Networking</h3>
 * <p>
 *     The bottom line is, that for raw computation, stick with the Java Streams API, and Parallel Streams. However, how
 *     does Project Loom compare when simulating a Network Application, where we might be farming prime computations
 *     out to some HTTP Endpoint?
 * </p>
 * <p>
 *      Using the same {@link Primes#isPrime(long, long, long)} code as the previous benchmarks,
 *      where we use <tt>isPrime(candiate, minimumLag, maximumLag)</tt> with <tt>minimumLag = 10</tt> ms and
 *      <tt>maximumLag = 30</tt> ms, times 2, or a total of 20 ms minimum and 60 ms maximum, where the actual
 *      lag is random; we simulate some network blocking overhead by using {@link Thread#sleep(long)} before the prime
 *      test and after the prime test to simulate the HTTP Request overhead and the HTTP Response overhead. More
 *      importantly, these two <tt>sleep()</tt> requests give the Thread schedulers a chance to let other tasks run.
 *      Implicitly, there is the actual lag of the isPrime(candidate) computation itself, which leads me to believe
 *      that this is actually a pretty good simulation.
 * </p>
 * <pre>
 * Benchmark                                 Mode  Cnt        Score        Error
 * PrimeThreads.platformPrimesTo_1000        avgt   25      122.004 ±      4.888
 * PrimeThreads.platformPrimesTo_10_000      avgt   25      986.577 ±     55.197
 * PrimeThreads.platformPrimesTo_10_000_000  avgt   25  1043651.917 ± 139003.151
 * PrimeThreads.virtualPrimesTo_1000         avgt   25       33.420 ±      1.142
 * PrimeThreads.virtualPrimesTo_10_000       avgt   25       53.476 ±      0.610
 * PrimeThreads.virtualPrimesTo_10_000_000   avgt   25    33058.254 ±   1171.181
 *
 * Benchmark                                     tested  throughput   ratio
 * PrimeThreads.platformPrimesTo_1000               500       4.098   0.274
 * PrimeThreads.platformPrimesTo_10_000           5,000       5.068   0.054
 * PrimeThreads.platformPrimesTo_10_000_000   5,000,000       4.791   0.032
 * PrimeThreads.virtualPrimesTo_1000                500      14.961   3.651
 * PrimeThreads.virtualPrimesTo_10_000            5,000      93.500  18.449
 * PrimeThreads.virtualPrimesTo_10_000_000    5,000,000     151.248  31.569
 * </pre>
 * <p>
 *     Based on the JMH results (after a time of 14:56:54), where we are using units of Milliseconds,
 *     <nl>
 *         <li>
 *             I am really surprised I was able to have 5,000,000 Platform Threads running, but it took an incredibly
 *             long time
 *         </li>
 *         <li>
 *             On a scale of 5,000,000 threads, Virtual Threads have 32 times the throughput of Platform Threads
 *         </li>
 *         <li>
 *             Given how long my system was running with fans at full, I think Project Loom deserves bragging rights
 *             that it also can save energy. It would be interesting to run this benchmark again with my power meter
 *             to see how many Joules was consumed by each benchmark.
 *         </li>
 *     </nl>
 * </p>
 * <p>
 *     Many thanks to Ron Pressler who responded to my email regarding benchmarking Project Loom, and patiently
 *     clarified my thinking on how to benchmark, and more importantly how to interprest and explain benchmarks.
 * </p>
 * <hr>
 * @see <a href="https://www.youtube.com/watch?v=NsDE7E8sIdQ">From Concurrent to Parallel</a>
 * @see <a href="https://www.youtube.com/watch?v=fOEPEXTpbJA">Project Loom: Modern Scalable Concurrency for the Java Platform</a>
 * @see <a href="https://stackoverflow.com/questions/69832291/will-project-loom-virtual-threads-improve-the-perfomance-of-parallel-streams">Will Project Loom Virtual Threads improve the perfomance of parallel Streams?</a>
 * @see <a href="https://github.com/openjdk/jmh">Java Microbenchmark Harness (JMH)</a>
 * @author eric@kolotyluk.net
 */
public class Experiment04_PrimeThreads {
    static final long limit1 = 1000;
    static final long limit2 = 10_000;
    static final long limit3 = 10_000_000;

    static final ThreadFactory platformThreadFactory = Thread.ofPlatform().factory();
    static final ThreadFactory virtualThreadFactory = Thread.ofVirtual().factory();

    static int availableProcessors = Runtime.getRuntime().availableProcessors();
    static long pid = ProcessHandle.current().pid();

    static ExecutorService platformCachedThreadPool() { return Executors.newCachedThreadPool(platformThreadFactory); }
    static ExecutorService platformFixedThreadPool() { return Executors.newFixedThreadPool(availableProcessors, platformThreadFactory); }
    static ExecutorService platformSingleThreadTaskExecutor() { return Executors.newSingleThreadExecutor(platformThreadFactory); }
    static ExecutorService platformThreadPerTaskExecutor() { return Executors.newThreadPerTaskExecutor(platformThreadFactory); }

    static ExecutorService virtualCachedThreadPool() { return Executors.newCachedThreadPool(virtualThreadFactory); }
    static ExecutorService virtualFixedThreadPool() { return Executors.newFixedThreadPool(availableProcessors, virtualThreadFactory); }
    static ExecutorService virtualSingleThreadTaskExecutor() { return Executors.newSingleThreadExecutor(virtualThreadFactory); }
    static ExecutorService virtualThreadPerTaskExecutor() { return Executors.newThreadPerTaskExecutor(virtualThreadFactory); }

    BiFunction<ThreadFactory, Method, ExecutorService> foo = (t, m) -> {
        try {
            return (ExecutorService) m.invoke(t);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    };


    public static void main(String args[]) {

        System.out.printf("""
                Hello Prime Threads
                PID       = %d
                CPU Cores = %d
                Heap Size = %d bytes
                ______________________________________________________________________________
                
                """, pid, availableProcessors, Runtime.getRuntime().maxMemory());

         suite3(limit1); System.out.println("\n\n");
         suite3(limit2); System.out.println("\n\n");
         suite3(limit3);  System.out.println("\n\n");
        // suite3(50_000_000, virtualThreadFactory);
    }

    public static void suite3(long limit) {

        var virtualCachedThreadPool         = Executors.newCachedThreadPool(virtualThreadFactory);
        var virtualFixedThreadPool          = Executors.newFixedThreadPool(12, virtualThreadFactory);
        var virtualSingleThreadTaskExecutor = Executors.newSingleThreadExecutor(virtualThreadFactory);
        var virtualThreadPerTaskExecutor    = Executors.newThreadPerTaskExecutor(virtualThreadFactory);

        var platformCachedThreadPool         = Executors.newCachedThreadPool(platformThreadFactory);
        var platformFixedThreadPool          = Executors.newFixedThreadPool(12, platformThreadFactory);
        var platformSingleThreadTaskExecutor = Executors.newSingleThreadExecutor(platformThreadFactory);
        var platformThreadPerTaskExecutor    = Executors.newThreadPerTaskExecutor(platformThreadFactory);

        var time1 = System.currentTimeMillis();

        var r1 = primeThreads(limit, virtualCachedThreadPool);

        var time1a = System.currentTimeMillis();

        var a1 = getPrimes(r1);

        var time2 = System.currentTimeMillis();

        var r2 = primeThreads(limit, virtualThreadPerTaskExecutor);

        var time2a = System.currentTimeMillis();

        var a2 = getPrimes(r2);

        var time3 = System.currentTimeMillis();

        var r3 = primeThreads(limit, platformCachedThreadPool);

        var time3a = System.currentTimeMillis();

        var a3 = getPrimes(r3);

        var time4 = System.currentTimeMillis();

        var r4= primeThreads(limit, platformThreadPerTaskExecutor);


        var time4a = System.currentTimeMillis();

        var a4 = getPrimes(r4);


        var time5 = System.currentTimeMillis();


        System.out.println("virtualCachedThreadPool          " + (time2 - time1) + ", " + (time1a - time1) + ", " + (time2 - time1a));
        System.out.println("virtualThreadPerTaskExecutor     " + (time3 - time2) + ", " + (time2a - time2) + ", " + (time3 - time2a));
        System.out.println("platformCachedThreadPool         " + (time4 - time3) + ", " + (time3a - time3) + ", " + (time4 - time3a));
        System.out.println("platformThreadPerTaskExecutor    " + (time5 - time4) + ", " + (time4a - time4) + ", " + (time5 - time4a));

    }


    /**
     * <pre>
     * Hello Prime Threads
     * PID = 31560
     * CPU Cores = 12
     * Heap Size = 17179869184
     * ______________________________________________________________________________
     *
     * primes to 1,000
     * primeThreads: threadMaximum = 12
     * primeThreads: threadMaximum = 3
     * primeThreads: threadMaximum = 1
     * primeThreads: threadMaximum = 3
     * virtualCachedThreadPool          = 72, 67, 5
     * virtualFixedThreadPool           = 6, 5, 1
     * virtualSingleThreadTaskExecutor  = 4, 4, 0
     * virtualThreadPerTaskExecutor     = 18, 18, 0
     *
     * primes to 10,000
     * primeThreads: threadMaximum = 3
     * primeThreads: threadMaximum = 3
     * primeThreads: threadMaximum = 1
     * primeThreads: threadMaximum = 3
     * virtualCachedThreadPool         = 78, 76, 2
     * virtualFixedThreadPool          = 15, 14, 1
     * virtualSingleThreadTaskExecutor = 9, 8, 1
     * virtualThreadPerTaskExecutor    = 36, 35, 1
     *
     * primes to 10,000,000
     * primeThreads: threadMaximum = 11
     * primeThreads: threadMaximum = 11
     * primeThreads: threadMaximum = 1
     * primeThreads: threadMaximum = 12
     * virtualCachedThreadPool         = 14338, 14167, 171
     * virtualFixedThreadPool          = 3162, 3071, 91
     * virtualSingleThreadTaskExecutor = 9349, 9297, 52
     * virtualThreadPerTaskExecutor    = 4124, 3939, 185
     *
     * primes to 50,000,000
     * primeThreads: threadMaximum = 12
     * primeThreads: threadMaximum = 12
     * primeThreads: threadMaximum = 1
     * primeThreads: threadMaximum = 12
     * virtualCachedThreadPool         = 96528, 95790, 738
     * virtualFixedThreadPool          = 18029, 17619, 410
     * virtualSingleThreadTaskExecutor = 81046, 80751, 295
     * virtualThreadPerTaskExecutor    = 40608, 39787, 821
     * </pre>
     * @param limit
     * @param threadFactory
     */
    public static void suite3Old(long limit, ThreadFactory threadFactory) {

//        var platformCachedThreadPool         = Executors.newCachedThreadPool(platformThreadFactory);
//        var platformFixedThreadPool          = Executors.newFixedThreadPool(availableProcessors, platformThreadFactory);
//        var platformSingleThreadTaskExecutor = Executors.newSingleThreadExecutor(platformThreadFactory);
//        var platformThreadPerTaskExecutor    = Executors.newThreadPerTaskExecutor(platformThreadFactory);
//
//        var virtualCachedThreadPool         = Executors.newCachedThreadPool(virtualThreadFactory);
//        var virtualFixedThreadPool          = Executors.newFixedThreadPool(availableProcessors, virtualThreadFactory);
//        var virtualSingleThreadTaskExecutor = Executors.newSingleThreadExecutor(virtualThreadFactory);
//        var virtualThreadPerTaskExecutor    = Executors.newThreadPerTaskExecutor(virtualThreadFactory);

        var time1 = System.currentTimeMillis();

        //var r1 = primeThreads(limit, Executors.newCachedThreadPool(threadFactory));
        var r1 = primeThreadsOld(limit, virtualCachedThreadPool());

        var time1a = System.currentTimeMillis();

        var a1 = Primes.getPrimes(r1);

        var time2 = System.currentTimeMillis();

        //var r2 = primeThreads(limit, Executors.newFixedThreadPool(availableProcessors, threadFactory));
        var r2 = primeThreadsOld(limit, virtualFixedThreadPool());

        var time2a = System.currentTimeMillis();

        var a2 = Primes.getPrimes(r2);

        var time3 = System.currentTimeMillis();

        // var r3 = primeThreads(limit, Executors.newSingleThreadExecutor(threadFactory));
        var r3 = primeThreadsOld(limit, virtualSingleThreadTaskExecutor());

        var time3a = System.currentTimeMillis();

        var a3 = Primes.getPrimes(r3);

        var time4 = System.currentTimeMillis();

        // var r4= primeThreads(limit, Executors.newThreadPerTaskExecutor(threadFactory));
        var r4= primeThreadsOld(limit, virtualThreadPerTaskExecutor());

        var time4a = System.currentTimeMillis();

        var a4 = Primes.getPrimes(r4);

        var time5 = System.currentTimeMillis();

//        System.out.println("CachedThreadPool         " + (time2 - time1));
//        System.out.println("FixedThreadPool          " + (time3 - time2));
//        System.out.println("SingleThreadTaskExecutor " + (time4 - time3));
//        System.out.println("ThreadPerTaskExecutor    " + (time5 - time4));

        System.out.println("CachedThreadPool         " + (time2 - time1) + ", " + (time1a - time1) + ", " + (time2 - time1a));
        System.out.println("FixedThreadPool          " + (time3 - time2) + ", " + (time2a - time2) + ", " + (time3 - time2a));
        System.out.println("SingleThreadTaskExecutor " + (time4 - time3) + ", " + (time3a - time3) + ", " + (time4 - time3a));
        System.out.println("ThreadPerTaskExecutor    " + (time5 - time4) + ", " + (time4a - time4) + ", " + (time5 - time4a));

    }

    static Long isPrimeOld(long candidate, AtomicLong taskCount, AtomicLong taskMaximum) {
        var preCount = taskCount.getAndIncrement();
        //System.out.printf("candidate = %d, preCount = %d, taskCount = %d\n", candidate, preCount, taskCount.get());

        var result = Primes.isPrime(candidate, 10, 30) ? candidate : null;

        var postCount = taskCount.getAndDecrement();

        long maximum = 0L;
        do {
            maximum = taskMaximum.get();
            //System.out.printf("postCount = %d, maximum = %d\n", postCount, maximum);
            if (postCount <= maximum) break;
        } while (!taskMaximum.compareAndSet(maximum, postCount));

        return result;
    }


    public static List<Future<Long>> primeThreads(long limit, ExecutorService executorService) {

        final AtomicLong threadCount  = new AtomicLong(0);
        final AtomicLong threadMaximum = new AtomicLong(0);

        try (var closableExecutorService = executorService) {
            var futureResults = LongStream.iterate(3, x -> x < limit, x -> x + 2)
                    .mapToObj(candidate -> {
                        return closableExecutorService.submit(() -> {
                            try {
                                var preCount = threadCount.getAndIncrement();
                                return isPrime(candidate, 10, 30, null, null) ? candidate : null;
                            }
                            finally {
                                var postCount = threadCount.getAndDecrement();
                                var maximum = 0L;
                                do {
                                    maximum = threadMaximum.get();
                                    if (postCount <= maximum) break;
                                } while (!threadMaximum.compareAndSet(maximum, postCount));
                            }
                        });
                    }).collect(Collectors.toList());

//            var result = futureResults.stream().filter(x -> {
//                try {
//                    return x.get() != null;
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
//                return false;
//            });

            return futureResults;
        }
        finally {
            System.out.println("primeThreads: threadMaximum = " + threadMaximum.get());
        }
    }


    public static List<Future<Long>> primeThreadsOld(long limit, ExecutorService executorService) {

        // System.out.println("executorService = " + executorService.toString());

        //final AtomicLong taskCount  = new AtomicLong(0);
        //final AtomicLong taskMaximum = new AtomicLong(0);

        try (var closableExecutorService = executorService) {

            var futureResults = LongStream.iterate(3, x -> x < limit, x -> x + 2)
                    .mapToObj(candidate ->
                        closableExecutorService.submit(() -> Primes.isPrime(candidate, 10, 30) ? candidate : null)
                    );

            return futureResults.collect(Collectors.toList());

//            var result = futureResults.filter(x -> {
//                try {
//                    var number = x.get();
//
//                    return number != null;
//                }
//                catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
//                return false;
//            }).collect(Collectors.toList());
//
//            return Primes.getPrimes(result);
        }
        finally {
            // System.out.println("primeThreads: taskMaximum = " + taskMaximum.get());
        }
    }

}
