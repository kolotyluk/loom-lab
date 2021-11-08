package net.kolotyluk.loom;

import javax.swing.text.html.Option;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

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
 *     shoulders of giants</em>. Generally, for CPU Bound use cases, the {@link java.util.stream.Stream} Interface
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
 *     As we can see in {@link Experiment03_Primes#serialPrimes(long)} it is very easy to express taking a range
 *     of numbers, and filtering out the primes. From {@link Experiment03_Primes#parallelPrimes(long)} we can see
 *     how easy it is to change this to parallel computation. However, with some experimentation, we can easily
 *     see that unless we have a large set of computations, 10,000 or more, making the computations parallel
 *     does not really buy us much. This is because the overhead of Concurrent Operation can easily overwhelm
 *     any benefits of parallelism.
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
 *     {@link Experiment03_Primes#isPrime(long, long, long)} calculation with network latency via
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
 *      Using the same {@link Experiment03_Primes#isPrime(long, long, long)} code as the previous benchmarks,
 *      where we use <tt>isPrime(candiate, minimumLag, maximumLag)</tt> with <tt>minimumLag = 10</tt> ms and
 *      <tt>maximumLag = 30</tt> ms, times 2, or a total of 20 ms minimum and 60 ms maximum, where the actual
 *      lag is random; we simulate some network blocking overhead by using {@link Thread#sleep(long)} before the prime
 *      test and after the prime test to simulate the HTTP Request overhead and the HTTP Response overhead. More
 *      importantly, these two <tt>sleep()</tt> requests give the Thread schedulers a chance to let other tasks run.
 *      Implicitly, there is the actual lag of the isPrime(candidate) computation itself, which leads me to believe
 *      that this is actually a pretty good simulation.
 * </p>
 * <pre>
 *
 * Benchmark                                 Mode  Cnt           Score
 * PrimeThreads.platformPrimesTo_1000        avgt   25      131.536 ±     2.860
 * PrimeThreads.platformPrimesTo_10_000      avgt   25     1132.570 ±    88.749
 * PrimeThreads.platformPrimesTo_10_000_000  avgt   25  113223.0013 ± 95938.331
 * PrimeThreads.virtualPrimesTo_1000         avgt   25       32.998 ±     9.349
 * PrimeThreads.virtualPrimesTo_10_000       avgt   25       58.174 ±     0.742
 * PrimeThreads.virtualPrimesTo_10_000_000   avgt   25    34989.439 ±  1354.511
 *
 * Benchmark                                     tested  throughput   ratio
 * PrimeThreads.platformPrimesTo_1000               500       3.801   0.251
 * PrimeThreads.platformPrimesTo_10_000           5,000       4.415   0.051
 * PrimeThreads.platformPrimesTo_10_000_000   5,000,000       4.416   0.031
 * PrimeThreads.virtualPrimesTo_1000                500      15.152   3.986
 * PrimeThreads.virtualPrimesTo_10_000            5,000      85.947  19.467
 * PrimeThreads.virtualPrimesTo_10_000_000    5,000,000     142.900  32.360
 * </pre>
 * <p>
 *     Based on the JMH results (after a time of 16:20:35), where we are using units of Milliseconds,
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
public class Experiment03_Primes {
    static final long count1 = 10_000_000;
    static final long count2 = 10_000;
    static final long count3 = 1000;

    static final ThreadFactory platformThreadFactory = Thread.ofPlatform().factory();
    static final ThreadFactory virtualThreadFactory = Thread.ofVirtual().factory();

    public static void main(String args[]) {
        System.out.println("Hello Primes");
        System.out.println("PID = " + ProcessHandle.current().pid());
        System.out.println("CPU Cores = " + Runtime.getRuntime().availableProcessors() + '\n');

        // Arrays.stream(serialPrimes(100)).forEach(prime -> System.out.println(prime));

        var time1 = System.currentTimeMillis();

        serialPrimes(1000);

        var time2 = System.currentTimeMillis();

        parallelPrimes(count1);

        var time3 = System.currentTimeMillis();

        virtualPrimes(count1, virtualThreadFactory);

        var time4 = System.currentTimeMillis();

        futurePrimes1(count1, virtualThreadFactory);

        var time5 = System.currentTimeMillis();

        futurePrimes2(count1, virtualThreadFactory);

        var time6 = System.currentTimeMillis();

        System.out.println("serial   time = " + (time2 - time1));
        System.out.println("parallel time = " + (time3 - time2));
        System.out.println("virtual  time = " + (time4 - time3));
        System.out.println("futures1 time = " + (time5 - time4));
        System.out.println("futures2 time = " + (time6 - time5));

        System.out.println("\nTransactional\n");

        time1 = System.currentTimeMillis();

        serialPrimes2(count2);

        time2 = System.currentTimeMillis();

        parallelPrimes2(count2);

        time3 = System.currentTimeMillis();

        virtualPrimes2(count2, virtualThreadFactory);

        time4 = System.currentTimeMillis();

        futurePrimes12(count2, virtualThreadFactory);

        time5 = System.currentTimeMillis();

        futurePrimes22(count2, virtualThreadFactory);

        time6 = System.currentTimeMillis();

        System.out.println("serial   time = " + (time2 - time1));
        System.out.println("parallel time = " + (time3 - time2));
        System.out.println("virtual  time = " + (time4 - time3));
        System.out.println("futures1 time = " + (time5 - time4));
        System.out.println("futures2 time = " + (time6 - time5));

    }

    public static long[] serialPrimes(long limit) {
        var primes = LongStream.iterate(3, x -> x < limit, x -> x + 2)
                .filter(candidate -> isPrime(candidate, 0, 0)).toArray();

        System.out.println("serialPrimes: primes found = " + primes.length);
        return primes;
    }

    public static long[] serialPrimes2(long limit) {
        var primes = LongStream.iterate(3, x -> x < limit, x -> x + 2)
                .filter(candidate -> isPrime(candidate, 10, 30)).toArray();

        // System.out.println("serialPrimes2: primes found = " + primes.length);
        return primes;
    }

    public static void parallelPrimes(long limit) {
        var primes = LongStream.iterate(3, x -> x < limit, x -> x + 2)
                .parallel()
                .filter(candidate -> isPrime(candidate, 0, 0)).toArray();

        //System.out.println("parallelPrimes: primes found = " + primes.length);
    }

    public static void parallelPrimes2(long limit) {
        var primes = LongStream.iterate(3, x -> x < limit, x -> x + 2)
                .parallel()
                .filter(candidate -> isPrime(candidate, 10, 30)).toArray();

        // System.out.println("parallelPrimes2: primes found = " + primes.length);
    }

    public static void virtualPrimes(long limit, ThreadFactory threadFactory) {
        try (var executorService = Executors.newThreadPerTaskExecutor(threadFactory)) {

            var primes = executorService.submit(() ->
                    LongStream.iterate(3, x -> x < limit, x -> x + 2)
                            .parallel()
                            .filter(candidate -> isPrime(candidate, 0, 0)).toArray()
            ).get();

            //System.out.println("virtualPrimes: primes found = " + primes.length);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void virtualPrimes2(long limit, ThreadFactory threadFactory) {
        try (var executorService = Executors.newThreadPerTaskExecutor(threadFactory)) {

            var primes = executorService.submit(() ->
                    LongStream.iterate(3, x -> x < limit, x -> x + 2)
                            .parallel()
                            .filter(candidate -> isPrime(candidate, 10, 30)).toArray()
            ).get();

            //System.out.println("virtualPrimes2: primes found = " + primes.length);


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void futurePrimes1(long limit, ThreadFactory threadFactory) {
        try (var executorService = Executors.newThreadPerTaskExecutor(virtualThreadFactory)) {
            var tasks = LongStream.iterate(3, x -> x < limit, x -> x + 2)
                    .mapToObj(candidate -> {
                        Callable<Optional<Long>> l = () -> {
                            if (isPrime(candidate, 0, 0)) return Optional.of(candidate);
                            else return Optional.empty();
                        };
                        return l;
                    }).collect(Collectors.toList());

            var results = executorService.invokeAll(tasks);

            var flat = results.stream().flatMap(x -> {
                try {
                    return x.get().stream();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            });

            //System.out.println("futurePrimes1: primes found = " + flat.count());


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void futurePrimes12(long limit, ThreadFactory threadFactory) {
        try (var executorService = Executors.newThreadPerTaskExecutor(virtualThreadFactory)) {
            var tasks = LongStream.iterate(3, x -> x < limit, x -> x + 2)
                    .mapToObj(candidate -> {
                        Callable<Optional<Long>> l = () -> {
                            if (isPrime(candidate, 10, 30)) return Optional.of(candidate);
                            else return Optional.empty();
                        };
                        return l;
                    }).collect(Collectors.toList());

            var results = executorService.invokeAll(tasks);


            var flat = results.stream().flatMap(x -> {
                try {
                    return x.get().stream();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            });

            //System.out.println("futurePrimes12: primes found = " + flat.count());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void futurePrimes2(long limit, ThreadFactory threadFactory) {
        try (var executorService = Executors.newThreadPerTaskExecutor(virtualThreadFactory)) {
            var tasks = LongStream.iterate(3, x -> x < limit, x -> x + 2)
                    .mapToObj(candidate -> {
                        return executorService.submit(() -> isPrime(candidate, 0, 0) ? candidate : null);
                    }).collect(Collectors.toList());

            var result = tasks.stream().filter(x -> {
                try {
                    return x.get() != null;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                return false;
            });

            //System.out.println("futurePrimes2: primes found = " + result.count());

            // executorService.shutdown();
            // executorService.awaitTermination(100, TimeUnit.SECONDS);
        }
    }

    public static void futurePrimes22(long limit, ThreadFactory threadFactory) {
        try (var executorService = Executors.newThreadPerTaskExecutor(threadFactory)) {
            var tasks = LongStream.iterate(3, x -> x < limit, x -> x + 2)
                    .mapToObj(candidate -> {
                        return executorService.submit(() -> isPrime(candidate, 10, 30) ? candidate : null);
                    }).collect(Collectors.toList());


            var result = tasks.stream().filter(x -> {
                try {
                    return x.get() != null;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                return false;
            });

            //System.out.println("futurePrimes2: primes found = " + result.count());

            // executorService.shutdown();
            // executorService.awaitTermination(100, TimeUnit.SECONDS);
        }
    }



    /**
     * Basic predicate for prime numbers, with capability of simulating network overhead.
     * @param candidate number to test for factors
     * @param minimumLag minimum time in ms to wait simulating network overhead
     * @param maximumLag maximum time in ms to wait simulating network overhead
     * @return true if Prime, false if not
     * @see <a href="https://stackoverflow.com/questions/69842535/is-there-any-benefit-to-thead-onspinwait-while-doing-cpu-bound-work">Is there any benefit to Thead.onSpinWait() while doing CPU Bound work?</a>
     */
    static boolean isPrime(long candidate, long minimumLag, long maximumLag) {

        BinaryOperator<Long> lag = (minimum, maximum) -> {
            if (minimum <= 0 || maximum <= 0) return 0L;
            var approx = (long) Math.nextUp(Math.sqrt(maximum - minimum));
            try {
                var approximateLag = minimum + approx;
                Thread.sleep(approximateLag);
                return approximateLag;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                return 0L;
            }
        };

        lag.apply(minimumLag, maximumLag);  // Simulate network request overhead

        if (candidate == 2) return true;
        if ((candidate & 1) == 0) return false; // filter out even numbers

        var limit = (long) Math.nextUp(Math.sqrt(candidate));

        for (long divisor = 3; divisor <= limit; divisor += 2) {
            // Thread.onSpinWait(); // If you think this will help, it likely won't
            if (candidate % divisor == 0) return false;
        }

        lag.apply(minimumLag, maximumLag);  // Simulate network response overhead

        return true;

    }
}
