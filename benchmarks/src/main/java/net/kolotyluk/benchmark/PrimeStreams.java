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
public class PrimeStreams {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(PrimeStreams.class.getSimpleName())
//                .mode(Mode.AverageTime)
//                .timeUnit(TimeUnit.SECONDS)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void serialPrimesTo_1000() {
        Experiment10_PrimeStreams.serialPrimes(1000);
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void serialPrimesTo_10_000() {
        Experiment10_PrimeStreams.serialPrimes(10_000);
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void serialPrimesTo_10_000_000() {
        Experiment10_PrimeStreams.serialPrimes(10_000_000);
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void parallelPrimesTo_1000() {
        Experiment10_PrimeStreams.parallelPrimes(1000);
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void parallelPrimesTo_10_000() {
        Experiment10_PrimeStreams.parallelPrimes(10_000);
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void parallelPrimesTo_10_000_000() {
        Experiment10_PrimeStreams.parallelPrimes(10_000_000);
    }

}
