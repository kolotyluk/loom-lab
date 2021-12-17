package net.kolotyluk.benchmark;

import net.kolotyluk.loom.Experiment10_PrimeStreams;
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
 * $ mvn clean install
 * $ java -jar target/benchmarks.jar PrimeNumbers
 * </pre>
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
 */
public class PrimeThreads {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(PrimeThreads.class.getSimpleName())
//                .mode(Mode.AverageTime)
//                .timeUnit(TimeUnit.SECONDS)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    static final ThreadFactory platformThreadFactory = Thread.ofPlatform().factory();
    static final ThreadFactory virtualThreadFactory = Thread.ofVirtual().factory();

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void virtualPrimesTo_1000() {
        Experiment10_PrimeStreams.futurePrimes22(1_000, virtualThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void virtualPrimesTo_10_000() {
        Experiment10_PrimeStreams.futurePrimes22(10_000, virtualThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void virtualPrimesTo_10_000_000() {
        Experiment10_PrimeStreams.futurePrimes22(10_000_000, virtualThreadFactory);
    }


    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void platformPrimesTo_1000() {
        Experiment10_PrimeStreams.futurePrimes22(1_000, platformThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void platformPrimesTo_10_000() {
        Experiment10_PrimeStreams.futurePrimes22(10_000, platformThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void platformPrimesTo_10_000_000() {
        Experiment10_PrimeStreams.futurePrimes22(10_000_000, platformThreadFactory);
    }

}
