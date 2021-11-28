package net.kolotyluk.loom;

public class Context {

    static int availableProcessors = Runtime.getRuntime().availableProcessors();
    static long maxMemory = Runtime.getRuntime().maxMemory();
    static long pid = ProcessHandle.current().pid();

    static void printHeader(Class clazz) {

        System.out.printf("""
                Hello %s
                PID       = %d
                CPU Cores = %d
                Heap Size = %d bytes
                ______________________________________________________________________________
                
                """, clazz.getName(), pid, availableProcessors, maxMemory);
    }

}
