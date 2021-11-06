package net.kolotyluk.benchmark;

import net.kolotyluk.loom.Experiment03_Primes;
import org.openjdk.jmh.annotations.Benchmark;

// import net.kolotyluk.loom.Primes;

public class MyBenchmark {

    @Benchmark
    public void testMethod1() {
        // This is a demo/sample template for building your JMH benchmarks. Edit as needed.
        // Put your benchmark code here.
        Experiment03_Primes.serialPrimes(10_000);
    }
//
//    @Benchmark
//    public void testMethod2() {
//        // This is a demo/sample template for building your JMH benchmarks. Edit as needed.
//        // Put your benchmark code here.
//        Experiment03_Primes.parallelPrimes(10_000);
//    }

}
