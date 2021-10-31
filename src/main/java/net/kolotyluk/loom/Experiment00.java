package net.kolotyluk.loom;

import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * https://wiki.openjdk.java.net/display/loom/Getting+started
 */
public class Experiment00 {

    static long timestamp = 0;

    public static void main(String args[]) {
        System.out.println("Fiber Fun - Experiment 0");
        System.out.println("PID = " + ProcessHandle.current().pid());
        System.out.println("CPU Cores = " + Runtime.getRuntime().availableProcessors() + '\n');

        try (var executorService = Executors.newVirtualThreadExecutor()) {
            IntStream.range(0, 15).forEach(item -> {
                System.out.println("item = " + item + ", Thread ID = " + Thread.currentThread());
                executorService.submit(() -> {
                    System.out.println("\ttask = " + item + ", Thread ID = " + Thread.currentThread());
                });
            });
        }
    }

}
