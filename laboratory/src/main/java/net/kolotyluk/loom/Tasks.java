package net.kolotyluk.loom;

import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

public class Tasks implements Runnable {

  public static void main(String[] args) {
    for (int x = 0; x < 10; x++)
      Executors.newFixedThreadPool(10).submit(new Tasks());
  }

  @Override
  public void run() {
    long wait = (long) (Math.random() * 1000);
    try {
      sleep(wait);
      System.out.println(Thread.currentThread() +
              " waited " + wait + " milliseconds");
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
