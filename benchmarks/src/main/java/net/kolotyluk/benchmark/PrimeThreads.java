package net.kolotyluk.benchmark;

import net.kolotyluk.loom.Experiment03_Primes;
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
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void virtualPrimesTo_1000() {
        Experiment03_Primes.futurePrimes22(1_000, virtualThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void virtualPrimesTo_10_000() {
        Experiment03_Primes.futurePrimes22(10_000, virtualThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void virtualPrimesTo_10_000_000() {
        Experiment03_Primes.futurePrimes22(10_000_000, virtualThreadFactory);
    }


    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void platformPrimesTo_1000() {
        Experiment03_Primes.futurePrimes22(1_000, platformThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void platformPrimesTo_10_000() {
        Experiment03_Primes.futurePrimes22(10_000, platformThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void platformPrimesTo_10_000_000() {
        Experiment03_Primes.futurePrimes22(10_000_000, platformThreadFactory);
    }

}
