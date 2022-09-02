package net.kolotyluk.loom;

import jdk.incubator.concurrent.StructuredTaskScope;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongFunction;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 *  <h1>Experiment Suite 02 - Throughput</h1>
 *  <p>
 *      Consider these results obtained via the
 *      <a href="https://github.com/openjdk/jmh">Java Microbenchmark Harness (JMH)</a>
 *      in
 *      <a href="https://github.com/kolotyluk/loom-lab/blob/master/benchmarks/src/main/java/net/kolotyluk/benchmark/BasicThroughput.java">BasicThroughput</a>
 *      that we will analyze below...
 *  </p>
 *  <pre>
 * Benchmark                                         runs/second        Error
 * baselineStream1To_10                              7687469.693 ± 224814.056
 * baselineStream1To_100                             1104471.967 ±  27214.069
 * baselineStream1To_1000                             114529.797 ±   3105.523
 * baselineStream1To_10000                             11468.640 ±     65.506
 * baselineStream2To_10                               581039.445 ±   2984.074
 * baselineStream2To_100                               48674.207 ±    119.580
 * baselineStream2To_1000                               5359.420 ±     17.450
 * baselineStream2To_10000                               534.121 ±      1.441
 * parallelStream1To_10                                70921.902 ±    364.130
 * parallelStream1To_100                               43634.949 ±    308.947
 * parallelStream1To_1000                              40419.203 ±    149.770
 * parallelStream1To_10000                             14114.597 ±    152.243
 * parallelStream2To_10                                69896.785 ±    656.540
 * parallelStream2To_100                               30738.958 ±    162.498
 * parallelStream2To_1000                              13686.162 ±    105.587
 * parallelStream2To_10000                              2799.335 ±     21.269
 * structuredPlatformThreads1To_10                       522.304 ±    142.136
 * structuredPlatformThreads1To_100                       56.890 ±     15.478
 * structuredPlatformThreads1To_1000                       5.908 ±      1.781
 * structuredPlatformThreads1To_10000                      0.584 ±      0.141
 * structuredPlatformThreads2To_10                       522.890 ±    118.930
 * structuredPlatformThreads2To_100                       57.186 ±     18.233
 * structuredPlatformThreads2To_1000                       5.994 ±      1.816
 * structuredPlatformThreads2To_10000                      0.589 ±      0.175
 * structuredVirtualThread2sTo_1000                     1755.882 ±     86.524
 * structuredVirtualThreads1To_10                       5595.565 ±    221.392
 * structuredVirtualThreads1To_100                      4019.994 ±    104.544
 * structuredVirtualThreads1To_1000                     1725.087 ±     47.855
 * structuredVirtualThreads1To_10000                     231.667 ±      4.741
 * structuredVirtualThreads2To_10                       5738.564 ±    303.156
 * structuredVirtualThreads2To_100                      4099.271 ±    121.266
 * structuredVirtualThreads2To_10000                     230.352 ±      5.745
 * transactionalBaselineStream1To_10                      47.043 ±      1.342
 * transactionalBaselineStream1To_100                      4.718 ±      0.112
 * transactionalBaselineStream1To_1000                     0.461 ±      0.014
 * transactionalBaselineStream1To_10000                    0.048 ±      0.006
 * transactionalBaselineStream2To_10                      46.969 ±      0.969
 * transactionalBaselineStream2To_100                      4.731 ±      0.773
 * transactionalBaselineStream2To_1000                     0.462 ±      0.015
 * transactionalBaselineStream2To_10000                    0.077 ±      0.007
 * transactionalParallelStream1To_10                     452.607 ±     47.752
 * transactionalParallelStream1To_100                     50.794 ±      2.796
 * transactionalParallelStream1To_1000                     5.299 ±      0.192
 * transactionalParallelStream1To_10000                    0.521 ±      0.004
 * transactionalParallelStream2To_10                     362.114 ±      7.574
 * transactionalParallelStream2To_100                     50.556 ±      1.149
 * transactionalParallelStream2To_1000                     5.246 ±      0.079
 * transactionalParallelStream2To_10000                    0.521 ±      0.011
 * transactionalStructuredPlatformThreads1To_10          228.233 ±     59.875
 * transactionalStructuredPlatformThreads1To_100          48.290 ±     11.494
 * transactionalStructuredPlatformThreads1To_1000          5.391 ±      1.313
 * transactionalStructuredPlatformThreads1To_10000         0.564 ±      0.133
 * transactionalStructuredPlatformThreads2To_10          235.796 ±     68.204
 * transactionalStructuredPlatformThreads2To_100          50.826 ±     14.387
 * transactionalStructuredPlatformThreads2To_1000          5.640 ±      1.552
 * transactionalStructuredPlatformThreads2To_10000         0.561 ±      0.113
 * transactionalStructuredVirtualThreads1To_10            62.805 ±      0.372
 * transactionalStructuredVirtualThreads1To_100           62.546 ±      2.281
 * transactionalStructuredVirtualThreads1To_1000          62.873 ±      0.582
 * transactionalStructuredVirtualThreads1To_10000         47.135 ±     16.391
 * transactionalStructuredVirtualThreads2To_10            62.818 ±      0.159
 * transactionalStructuredVirtualThreads2To_100           61.564 ±      7.955
 * transactionalStructuredVirtualThreads2To_1000          59.581 ±      1.140
 * transactionalStructuredVirtualThreads2To_10000         51.455 ±     13.161
 * </pre>
 * <p>
 *     For all of these benchmarks, identical tasks were run, where there were two types of tasks.
 *     <ol>
 *         <li>
 *             A simple computation of doubling a value.
 *         </li>
 *         <li>
 *             A complex computation of testing if a number is prime.
 *         </li>
 *     </ol>
 *     Consider <a href="https://en.wikipedia.org/wiki/Little%27s_law">Little's Law</a>
 *
 *     <div style="padding: 30pt;font-size: 30pt;font-family: 'Times New Roman', serif;">L = λW</div>
 *
 *     Where L is the <em>Level</em> of concurrency, λ is the throughput, and W is the Wait time or
 *     <em>total latency</em>. For the purely computational tasks, the pure Wait time for doubling a value is much
 *     less than the pure Wait time for testing if values are prime numbers. If we want to improve throughput,
 *     then we need to consider:
 *
 *     <div style="padding: 30pt;font-size: 30pt;font-family: 'Times New Roman', serif;">λ = L / W</div>
 *
 *     then, without changing W, we need to increase L to increase λ. However, <em>Pure</em> Wait is not the
 *     same as <em>Transactional</em> Wait, and we can see the effects of this in our benchmarks. In these
 *     benchmarks, except for serial Streams, the count to 10, to 100, to 1000, to 10000, (the number of tasks
 *     created) indicates a higher <em>demand</em> for concurrency. However, there are also different implementations
 *     of concurrency, which is why these benchmarks are interesting. A key concept is that is that Level of
 *     Concurrency also implies Quality of Concurrency, where Virtual Threads are able to achieve higher throughput
 *     than Platform Threads for the same demand.
 * </p>
 * <h2>Parallel vs Sequential</h2>
 * <p>
 *     The first thing we should notice is <tt>baselineStream1...</tt> vs <tt>baselineStream2...</tt> where
 *     the Throughput λ of <tt>baselineStream1...</tt> is always higher, because the Wait W is lower.
 *     Similarly this is true of <tt>parallelStream1...</tt> vs <tt>parallelStream2...</tt> for the same reason.
 * </p>
 * <p>
 *     The other impression we should have is that Parallel Streams are <strong><em>capable</em></strong> of better
 *     throughput than Sequential Streams, but there is an substantial <em>overhead</em> in running Parallel Streams,
 *     so a thoughtful design with performance testing such as benchmarks is useful in understanding where the benefits
 *     of <em>Parallelism</em> kick in.
 *     <ul>
 *         <li>
 *             When running a low W task, <tt>baselineStream1To_1000</tt> has better throughput than
 *             <tt>parallelStream1To_1000</tt>, but <tt>parallelStream1To_10000</tt> has better throughput than
 *             <tt>baselineStream1To_1000</tt>, so somewhere between 1,000 and 10,000 Parallel Streams gets better
 *             throughput than Serial Streams.
 *         </li>
 *         <li>
 *             When running a higher W task, <tt>parallelStream2To_1000</tt> has better throughput than
 *             <tt> baselineStream2To_1000</tt>, so when W is higher, a higher λ helps us sooner because the
 *             overhead of concurrency is less dominant than the cost of the task.
 *         </li>
 *     </ul>
 *     In conclusion, when <strong><em>optimizing throughput through parallelization</em></strong>, it helps to
 *     understand what level of concurrency is needed with respect to pure W, but ultimately devising benchmarks
 *     will guide us best.
 *     <blockquote>
 *         Parallelism is strictly an optimization<br />
 *         — Brian Goetz
 *     </blockquote>
 * </p>
 * <h2>Computational vs Transactional</h2>
 * <p>
 *     The {@link java.util.stream.Stream} API is an excellent framework for computational tasks. In particular,
 *     {@link Stream#parallel()} is a simple way to optimize purely computational tasks that are independent.
 *     By <em>purely computational</em> I mean, tasks that do not call any blocking/transactional APIs. By
 *     <em>transactional</em> I mean any API, such as {@link Thread#sleep(Duration)}, that will <em>pin</em>
 *     the task such that it cannot be scheduled for execution until the transaction has completed.
 * </p>
 * <p>
 *     Half of the benchmarks are purely computational, while the other half are transactional, and the first
 *     impression we should have is that Streams are good for purely computational tasks, but not for transactional
 *     task, while the opposite is largely true for Platform Threads and Virtual Threads.
 *     <ul>
 *         <li>
 *             In the case of pure computation, at no time in these benchmarks did Virtual Threads perform
 *             better than Parallel Streams.
 *         </li>
 *         <li>
 *             In the case of transactions, 1 ms Thread Sleep per task, Streams and Parallel Streams only perform
 *             well with low λ, as do Platform Threads. It is interesting to note that in these experiments,
 *             Platform Threads seems to perform similarly to Streams and Parallel Streams. 🤔
 *         </li>
 *     </ul>
 * </p>
 * <h2>Platform Threads vs Virtual Threads</h2>
 * <p>
 *     The most important thing we should note is generally how much better Virtual Threads perform than Platform Threads.
 *     The other thing that is striking from the transactional results is that Virtual Threads are much more balanced
 *     and predictable in their throughput than Platform Threads under difference levels of concurrency. When we are
 *     designing and implementing concurrent applications, <em>balanced</em> and <em>predictable</em> are qualities
 *     we want to embrace.
 * </p>
 * <h1>Also</h1>
 * <p>
 *     For clarification on the distinction between Concurrency and Parallelism, see
 *     <a href="https://www.youtube.com/watch?v=2J2tJm_iwk0">Project Loom Brings Structured Concurrency - Inside Java Newscast #17</a>.
 *     For better context overall, checkout the
 *     <a href="https://kolotyluk.github.io/loom-lab/lexicon.html">lexicon</a> and
 *     <a href="https://kolotyluk.github.io/loom-lab/advantages.html">loom advantages</a>.
 * </p>
 * <p>
 *     Is is worth noting that this benchmark was run on an Intel Xeon W3680 @ 3.33 GHz, with 6 Cores, 12 Hardware
 *     Threads, and 24 GB RAM. Indeed, on benchmarks where the demand was less than 12, the the number of Hardware
 *     Threads would be underutilized.
 * </p>
 * @see <a href="https://wiki.openjdk.java.net/display/loom/Getting+started">Loom Getting Started</a>
 * @see <a href="https://www.youtube.com/watch?v=Nb85yJ1fPXM">Java ExecutorService - Part 1</a>
 * @see <a href="https://github.com/openjdk/jmh">Java Microbenchmark Harness (JMH)</a>
 * @see <a href="https://www.youtube.com/watch?v=2J2tJm_iwk0">Project Loom Brings Structured Concurrency - Inside Java Newscast #17</a>
 * @author eric@kolotyluk.net
 */
public class Experiment02_Throughput {

    public static void main(String ... args){
        Context.printHeader(Experiment02_Throughput.class);

        var platformThreadFactory = Thread.ofPlatform().factory();
        var virtualThreadFactory = Thread.ofVirtual().factory();

        long limit = 10000;

        var time1 = System.currentTimeMillis();

        var r1 = structuredThreads(isPrime, limit, platformThreadFactory);

        var time2 = System.currentTimeMillis();
        System.out.println((time2 - time1) + " milliseconds");

        var r2 = structuredThreads(isPrime, limit, virtualThreadFactory);

        var time3 = System.currentTimeMillis();
        System.out.println((time3 - time2) + " milliseconds");


    }

    static Lag simpleLag = new Lag(Duration.ofMillis(1));

    static public LongFunction<Long> doubleIt = (value) -> value + value;
    static public LongFunction<Long> doubleItTransactionally = (value) -> {
        simpleLag.sleep();
        return value * value + value;
    };

    static public LongFunction<Long> isPrime = (value) -> Primes.isPrime(value + 104_729, 0, 0) ? value : 0;
    //static public LongFunction<Long> isPrime = (value) -> Primes.isPrime(value +  100000000003L, 0, 0) ? value : 0;

    static public LongFunction<Long> isPrimeTransactionally = (value) -> {
        simpleLag.sleep();
        return Primes.isPrime(value + 104_729, 0, 0) ? value : 0;
        //return Primes.isPrime(value + 100_000_000, 0, 0) ? value : 0;
    };

    public static List<Long> baselineStream(LongFunction<Long> task, long limit) {
        return LongStream.range(0,limit).mapToObj(task::apply).toList();
    }

    public static List<Long> parallelStream(LongFunction<Long> task, long limit) {
        return LongStream.range(0,limit).parallel().mapToObj(task::apply).toList();
    }

    public interface Counter {
        public Long count(LongFunction<Long> function, Long item);
    }

    public static List<Long> structuredThreads(LongFunction<Long> task, long limit, ThreadFactory threadFactory) {

        var threadCount = new AtomicLong();
        var threadMaximum = new AtomicLong();

        Counter counter = (function, item) -> {
            try {
                var newCount = threadCount.incrementAndGet();
                return function.apply(item);
            }
            finally {
                var highCount = threadCount.getAndDecrement();
                var maximum = 0L;
                do {
                    maximum = threadMaximum.get();
                    if (highCount <= maximum) break;
                } while (!threadMaximum.compareAndSet(maximum, highCount));
            }
        };

        try (var structuredExecutor = new StructuredTaskScope.ShutdownOnFailure()) {
            var result = LongStream.range(0,limit)
                    .mapToObj(item -> structuredExecutor.fork(() -> counter.count(task,limit)))
                    .toList();
            structuredExecutor.join();
            // return result.stream().map(Future::resultNow).toList();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
        finally {
            System.out.println("threadMaximum = " + threadMaximum + " for " + threadFactory);
        }
    }
}
