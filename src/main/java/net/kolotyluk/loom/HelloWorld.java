package net.kolotyluk.loom;

import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * https://wiki.openjdk.java.net/display/loom/Getting+started
 */
public class HelloWorld {
    public static void main(String args[]) {
        System.out.println("Hello World");
        System.out.println("PID = " + ProcessHandle.current().pid());
    }
}
