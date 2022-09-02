package net.kolotyluk.loom;

public class Task extends Thread {
    public static void main(String[] args) {
        for (int x = 0; x < 10; x++)
            new Task().start();
    }

    public void run() {
        try {
            long wait = (long) (Math.random() * 1000);
            sleep(wait);
            System.out.println(Thread.currentThread() +
              " waited " + wait + " milliseconds");
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
