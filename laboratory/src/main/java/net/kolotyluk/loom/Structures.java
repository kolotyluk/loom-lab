package net.kolotyluk.loom;

import jdk.incubator.concurrent.StructuredTaskScope;

import static java.lang.Thread.sleep;

public class Structures {

  public static void main(String[] args) {

    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
      for (int x = 0; x < 10; x++) {
        var result = scope.fork(() -> {
          long wait = (long) (Math.random() * 1000);
          try {
            sleep(wait);
            System.out.println(Thread.currentThread() +
              " waited " + wait + " milliseconds");
          }
          catch (InterruptedException e) {
            e.printStackTrace();
          }
          return wait;
        });
      }

      scope.join();

    }
    catch (InterruptedException e) {
        e.printStackTrace();
    }
  }
}
