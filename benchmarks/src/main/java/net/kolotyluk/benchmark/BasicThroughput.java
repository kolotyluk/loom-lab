package net.kolotyluk.benchmark;

import net.kolotyluk.loom.Experiment02_Throughput;
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
public class BasicThroughput {

    static ThreadFactory platformThreadFactory = Thread.ofPlatform().factory();
    static ThreadFactory virtualThreadFactory = Thread.ofVirtual().factory();

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BasicThroughput.class.getSimpleName())
//                .mode(Mode.AverageTime)
//                .timeUnit(TimeUnit.SECONDS)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void baselineStream1To_10() {
        Experiment02_Throughput.baselineStream(Experiment02_Throughput.doubleIt, 10);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void baselineStream1To_100() {
        Experiment02_Throughput.baselineStream(Experiment02_Throughput.doubleIt,100);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void baselineStream1To_1000() {
        Experiment02_Throughput.baselineStream(Experiment02_Throughput.doubleIt,1000);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void baselineStream1To_10000() {
        Experiment02_Throughput.baselineStream(Experiment02_Throughput.doubleIt,10000);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void baselineStream2To_10() {
        Experiment02_Throughput.baselineStream(Experiment02_Throughput.isPrime, 10);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void baselineStream2To_100() {
        Experiment02_Throughput.baselineStream(Experiment02_Throughput.isPrime,100);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void baselineStream2To_1000() {
        Experiment02_Throughput.baselineStream(Experiment02_Throughput.isPrime,1000);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void baselineStream2To_10000() {
        Experiment02_Throughput.baselineStream(Experiment02_Throughput.isPrime,10000);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void parallelStream1To_10() {
        Experiment02_Throughput.parallelStream(Experiment02_Throughput.doubleIt,10);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void parallelStream1To_100() {
        Experiment02_Throughput.parallelStream(Experiment02_Throughput.doubleIt,100);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void parallelStream1To_1000() {
        Experiment02_Throughput.parallelStream(Experiment02_Throughput.doubleIt,1000);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void parallelStream1To_10000() {
        Experiment02_Throughput.parallelStream(Experiment02_Throughput.doubleIt,10000);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void parallelStream2To_10() {
        Experiment02_Throughput.parallelStream(Experiment02_Throughput.isPrime,10);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void parallelStream2To_100() {
        Experiment02_Throughput.parallelStream(Experiment02_Throughput.isPrime,100);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void parallelStream2To_1000() {
        Experiment02_Throughput.parallelStream(Experiment02_Throughput.isPrime,1000);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void parallelStream2To_10000() {
        Experiment02_Throughput.parallelStream(Experiment02_Throughput.isPrime,10000);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void structuredPlatformThreads1To_10() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.doubleIt,10, platformThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void structuredPlatformThreads1To_100() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.doubleIt,100, platformThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void structuredPlatformThreads1To_1000() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.doubleIt,1000, platformThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void structuredPlatformThreads1To_10000() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.doubleIt,10000, platformThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void structuredPlatformThreads2To_10() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.isPrime,10, platformThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void structuredPlatformThreads2To_100() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.isPrime,100, platformThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void structuredPlatformThreads2To_1000() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.isPrime,1000, platformThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void structuredPlatformThreads2To_10000() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.isPrime,10000, platformThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void structuredVirtualThreads1To_10() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.doubleIt,10, virtualThreadFactory);
    }


    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void structuredVirtualThreads1To_100() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.doubleIt,100, virtualThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void structuredVirtualThreads1To_1000() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.doubleIt,1000, virtualThreadFactory);
    }
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void structuredVirtualThreads1To_10000() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.doubleIt,10000, virtualThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void structuredVirtualThreads2To_10() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.isPrime,10, virtualThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void structuredVirtualThreads2To_100() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.isPrime,100, virtualThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void structuredVirtualThread2sTo_1000() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.isPrime,1000, virtualThreadFactory);
    }
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void structuredVirtualThreads2To_10000() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.isPrime,10000, virtualThreadFactory);
    }

///////////////////////////////////////////////////

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalBaselineStream1To_10() {
        Experiment02_Throughput.baselineStream(Experiment02_Throughput.doubleItTransactionally, 10);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalBaselineStream1To_100() {
        Experiment02_Throughput.baselineStream(Experiment02_Throughput.doubleItTransactionally,100);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalBaselineStream1To_1000() {
        Experiment02_Throughput.baselineStream(Experiment02_Throughput.doubleItTransactionally,1000);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalBaselineStream1To_10000() {
        Experiment02_Throughput.baselineStream(Experiment02_Throughput.doubleItTransactionally,10000);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalBaselineStream2To_10() {
        Experiment02_Throughput.baselineStream(Experiment02_Throughput.isPrimeTransactionally, 10);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalBaselineStream2To_100() {
        Experiment02_Throughput.baselineStream(Experiment02_Throughput.isPrimeTransactionally,100);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalBaselineStream2To_1000() {
        Experiment02_Throughput.baselineStream(Experiment02_Throughput.isPrimeTransactionally,1000);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalBaselineStream2To_10000() {
        Experiment02_Throughput.baselineStream(Experiment02_Throughput.isPrimeTransactionally,10000);
    }



    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalParallelStream1To_10() {
        Experiment02_Throughput.parallelStream(Experiment02_Throughput.doubleItTransactionally,10);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalParallelStream1To_100() {
        Experiment02_Throughput.parallelStream(Experiment02_Throughput.doubleItTransactionally,100);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalParallelStream1To_1000() {
        Experiment02_Throughput.parallelStream(Experiment02_Throughput.doubleItTransactionally,1000);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalParallelStream1To_10000() {
        Experiment02_Throughput.parallelStream(Experiment02_Throughput.doubleItTransactionally,10000);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalParallelStream2To_10() {
        Experiment02_Throughput.parallelStream(Experiment02_Throughput.isPrimeTransactionally,10);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalParallelStream2To_100() {
        Experiment02_Throughput.parallelStream(Experiment02_Throughput.isPrimeTransactionally,100);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalParallelStream2To_1000() {
        Experiment02_Throughput.parallelStream(Experiment02_Throughput.isPrimeTransactionally,1000);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalParallelStream2To_10000() {
        Experiment02_Throughput.parallelStream(Experiment02_Throughput.isPrimeTransactionally,10000);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalStructuredPlatformThreads1To_10() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.doubleItTransactionally,10, platformThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalStructuredPlatformThreads1To_100() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.doubleItTransactionally,100, platformThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalStructuredPlatformThreads1To_1000() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.doubleItTransactionally,1000, platformThreadFactory);
    }


    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalStructuredPlatformThreads1To_10000() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.doubleItTransactionally,10000, platformThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalStructuredPlatformThreads2To_10() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.isPrimeTransactionally,10, platformThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalStructuredPlatformThreads2To_100() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.isPrimeTransactionally,100, platformThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalStructuredPlatformThreads2To_1000() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.isPrimeTransactionally,1000, platformThreadFactory);
    }


    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalStructuredPlatformThreads2To_10000() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.isPrimeTransactionally,10000, platformThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalStructuredVirtualThreads1To_10() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.doubleItTransactionally,10, virtualThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalStructuredVirtualThreads1To_100() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.doubleItTransactionally,100, virtualThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalStructuredVirtualThreads1To_1000() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.doubleItTransactionally,1000, virtualThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalStructuredVirtualThreads1To_10000() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.doubleItTransactionally,10000, virtualThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalStructuredVirtualThreads2To_10() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.isPrimeTransactionally,10, virtualThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalStructuredVirtualThreads2To_100() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.isPrimeTransactionally,100, virtualThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalStructuredVirtualThreads2To_1000() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.isPrimeTransactionally,1000, virtualThreadFactory);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transactionalStructuredVirtualThreads2To_10000() {
        Experiment02_Throughput.structuredThreads(Experiment02_Throughput.isPrimeTransactionally,10000, virtualThreadFactory);
    }

}
