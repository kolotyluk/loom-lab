package net.kolotyluk.loom;

import java.net.http.HttpResponse;
import java.time.Duration;

import java.time.Instant;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.URI;

public class Experiment20_HttpClient {

    public static void main2(String args[]) {
        Context.printHeader(Experiment20_HttpClient.class);

        var virtualThreadFactory  = Thread.ofVirtual().factory();

        try (var executorService = Executors.newThreadPerTaskExecutor(virtualThreadFactory)) {

            // https://apipheny.io/free-api/

            var client = HttpClient.newBuilder()
                    .executor(executorService)
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            var request = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.boredapi.com/api/activity"))
                    .build();

            Callable<String> getActivity = () -> {
//                var response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                        .thenApply(HttpResponse::body)
//                        .join();
                var response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
                System.out.printf("early result = %s\n", response);
                return response;
            };

            var result1 = executorService.submit(getActivity);

            System.out.printf("result1 = %s", result1.get());
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        Context.printHeader(Experiment20_HttpClient.class);

        try (var structuredExecutor = StructuredExecutor.open("Experiment20")) {

            // https://apipheny.io/free-api/

            var client = HttpClient.newBuilder()
                    .executor(structuredExecutor)
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            var request = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.boredapi.com/api/activity"))
                    .build();

            Callable<String> getActivity = () -> {
                var response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenApply(HttpResponse::body)
                        .join();
//                var response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
                System.out.printf("early result = %s\n", response);
                return response;
            };

            var result1 = structuredExecutor.fork(getActivity);

            try {
                structuredExecutor.joinUntil(Instant.now().plusSeconds(20));
            }
            catch (TimeoutException e) {
                System.out.println("TimeoutException");
                structuredExecutor.shutdown();
                // structuredExecutor.join();
                System.out.printf("result = %s", result1.resultNow());
            }

            System.out.printf("result1 = %s", result1.get());
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
