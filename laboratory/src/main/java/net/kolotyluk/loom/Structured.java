package net.kolotyluk.loom;

import jdk.incubator.concurrent.StructuredTaskScope;

import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Thread.sleep;

public class Structured {

  public static void main(String[] args) {

    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

      var results = IntStream.range(0, 10)
        .mapToObj(item -> scope.fork(new Task(item))
      ).collect(Collectors.toList());

      scope.join();

      results.forEach(result -> System.out.println(result.resultNow()));
    }
    catch (InterruptedException e) {
        e.printStackTrace();
    }
  }
  record Task(int item) implements Callable<String> {

    @Override
    public String call() throws Exception {
      long wait = (long) (Math.random() * 1000);
      try {
        sleep(wait);
        return Thread.currentThread() +
          " item " + item + " waited " + wait + " milliseconds";
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
      return "error";
    }
  }
}
