package net.kolotyluk.benchmark;

import net.kolotyluk.loom.Experiment03_PrimeStreams;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * <h1>Prime Numbers with Streams</h1>
 * <p>
 *     These benchmarks can take a very long time to complete, and for some tests utilize almost 100% of all CPUs.
 * </p>
 * Make sure we are running with the right JDK for Project Loom
 * <pre>
 * $ "${JAVA_HOME}/bin/java" -version
 * openjdk version "18-loom" 2022-03-15
 * OpenJDK Runtime Environment (build 18-loom+2-74)
 * OpenJDK 64-Bit Server VM (build 18-loom+2-74, mixed mode, sharing)
 * </pre>
 * Make sure the laboratory has been compiled and installed for benchmarking.
 * <pre>
 * $ cd laboratory
 * $ mvn clean install
 * </pre>
 * Run the benchmarks.
 * <pre>
 * $ cd benchmarks
 * $ mvn clean verify
 * $ "${JAVA_HOME}/bin/java" -jar target/benchmarks.jar PrimeNumbers
 * </pre>
 * <pre>
 * # Run complete. Total time: 01:44:07
 *
 * REMEMBER: The numbers below are just data. To gain reusable insights, you need to follow up on
 * why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
 * experiments, perform baseline and negative tests that provide experimental control, make sure
 * the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
 * Do not assume the numbers tell you what you want them to tell.
 *
 * Benchmark                                                Mode  Cnt      Score      Error  Units
 * PrimeThreadsCachedTreadPool.platformPrimesTo_1000        avgt   25     71.376 ±    6.313  ms/op
 * PrimeThreadsCachedTreadPool.platformPrimesTo_10_000      avgt   25    246.932 ±   35.002  ms/op
 * PrimeThreadsCachedTreadPool.platformPrimesTo_10_000_000  avgt   25  38564.159 ±  618.292  ms/op
 * PrimeThreadsCachedTreadPool.virtualPrimesTo_1000         avgt   25     33.159 ±    0.229  ms/op
 * PrimeThreadsCachedTreadPool.virtualPrimesTo_10_000       avgt   25     62.399 ±    0.698  ms/op
 * PrimeThreadsCachedTreadPool.virtualPrimesTo_10_000_000   avgt   25  45667.084 ± 2736.951  ms/op
 * </pre>
 */
public class PrimeThreadsCachedThreadPool {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(PrimeThreadsCachedThreadPool.class.getSimpleName())
//                .mode(Mode.AverageTime)
//                .timeUnit(TimeUnit.SECONDS)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    static final ThreadFactory platformThreadFactory = Thread.ofPlatform().factory();
    static final ThreadFactory virtualThreadFactory = Thread.ofVirtual().factory();

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void virtualPrimesTo_1000() {
        Experiment03_PrimeStreams.futurePrimes33(1_000, virtualThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void virtualPrimesTo_10_000() {
        Experiment03_PrimeStreams.futurePrimes33(10_000, virtualThreadFactory);
    }

//    @Benchmark
//    @BenchmarkMode(Mode.SingleShotTime)
//    @OutputTimeUnit(TimeUnit.MILLISECONDS)
//    public void virtualPrimesTo_10_000_000() {
//        Experiment03_Primes.futurePrimes33(10_000_000, virtualThreadFactory);
//    }


    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void platformPrimesTo_1000() {
        Experiment03_PrimeStreams.futurePrimes33(1_000, platformThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void platformPrimesTo_10_000() {
        Experiment03_PrimeStreams.futurePrimes33(10_000, platformThreadFactory);
    }

//    @Benchmark
//    @BenchmarkMode(Mode.SingleShotTime)
//    @OutputTimeUnit(TimeUnit.MILLISECONDS)
//    public void platformPrimesTo_10_000_000() {
//        Experiment03_Primes.futurePrimes33(10_000_000, platformThreadFactory);
//    }

}
