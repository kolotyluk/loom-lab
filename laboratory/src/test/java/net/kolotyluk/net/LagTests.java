package net.kolotyluk.net;

import net.kolotyluk.loom.Lag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;


public class LagTests {
    static Logger logger = LoggerFactory.getLogger(LagTests.class);

    Duration minimumDuration = Duration.ofMillis(10);
    Duration maximumDuration = Duration.ofMillis(20);

    /**
     * <h1>Sleep Tests</h1>
     * <p>
     *     There are many ways to deal with interrupted threads, but one feature of {@link Lag#sleep(Consumer)} is that
     *     we can pass in an interrupt handler so we don't have to use all the try-catch-InterruptedException
     *     boilerplate.
     * </p>
     * @throws InterruptedException
     */
    @Test
    void sleep() throws InterruptedException {
        AtomicInteger value = new AtomicInteger();

        Assertions.assertEquals(0, value.get());

        var lag = new Lag(minimumDuration, maximumDuration);

        Runnable task = () -> {
            value.set(1);
            lag.sleep(cause -> value.set(2));
            // if (! Thread.interrupted()) value.set(2);
            if (value.get() == 1) value.set(3);
        };

        var thread1 = new Thread(task);
        thread1.start();
        Thread.sleep(minimumDuration.dividedBy(2));

        Assertions.assertEquals(1, value.get());

        // Thread.sleep(maximumDuration);
        thread1.join();
        Assertions.assertEquals(3, value.get());

        var thread2 = new Thread(task);
        thread2.start();
        Thread.sleep(Duration.ofMillis(5));

        Assertions.assertEquals(1, value.get());

        thread2.interrupt();

        Thread.sleep(Duration.ofMillis(15));
        Assertions.assertEquals(2, value.get());

        System.out.println("Testing...");

        var testTask = new TestTask(value, lag);


        var thread3 = new Thread(testTask);
        thread3.start();
        // Thread.sleep(maximumDuration.multipliedBy(11));

        thread3.join();

        Assertions.assertEquals(3, value.get());

        var thread4 = new Thread(testTask);
        thread4.start();

        Thread.sleep(minimumDuration.multipliedBy(5));
        thread4.interrupt();
        // Thread.yield();
        thread4.join();

        System.out.println("            value = " + value.get());

        Assertions.assertEquals(2, value.get());
    }

    record TestTask(AtomicInteger value, Lag lag) implements Runnable {

        @Override
        public void run() {
            var count = 0;
            try {
                value.set(1);
//                for (int i = 0; i < 10; i++) {
//                    count++;
//                    System.out.println("i = " + i);
//                    lag.sleep(cause -> {
//                        System.out.println("Interrupted");
//                        value.set(2);
//                    });
//                }

//                for (int i = 0; i < 10; i++) {
//                    count++;
//                    System.out.println("i = " + i);
//                    System.out.println("duration" + lag.sleep());
//                }

                for (int i = 0; i < 10; i++) {
                    count++;
                    System.out.println("i = " + i);
                    if (Thread.currentThread().isInterrupted()) {
                        logger.atDebug().log("Interrupted value = {}", value.get());
                        //System.out.println("Interrupted value = " + value.get());
                        value.set(2);
                        logger.atDebug().log("            value = {}", value.get());
                        //System.out.println("            value = " + value.get());
                        break;
                    }
                    else lag.sleep();
                }
                if (value.get() == 1) value.set(3);
                logger.atDebug().log("count = {}", count);
                // System.out.println("count = " + count);
            } catch (Exception e) {
                logger.atDebug().log("caught InterruptedException");
                // System.out.println("caught InterruptedException");
            }

        }
    }


}
