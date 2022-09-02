package net.kolotyluk.loom;

import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.time.Duration;

import java.time.Instant;
import java.util.concurrent.*;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.URI;

public class Experiment04_HttpClient {
//
//    public static void main2(String args[]) {
//        Context.printHeader(Experiment04_HttpClient.class);
//
//        var virtualThreadFactory  = Thread.ofVirtual().factory();
//
//        try (var executorService = Executors.newThreadPerTaskExecutor(virtualThreadFactory)) {
//
//            // https://apipheny.io/free-api/
//
//            var client = HttpClient.newBuilder()
//                    .executor(executorService)
//                    .connectTimeout(Duration.ofSeconds(10))
//                    .build();
//
//            var request = HttpRequest.newBuilder()
//                    .uri(URI.create("https://www.boredapi.com/api/activity"))
//                    .build();
//
//            Callable<String> getActivity = () -> {
////                var response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
////                        .thenApply(HttpResponse::body)
////                        .join();
//                var response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
//                System.out.printf("early result = %s\n", response);
//                return response;
//            };
//
//            var result1 = executorService.submit(getActivity);
//
//            System.out.printf("result1 = %s", result1.get());
//        }
//        catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    static class MyPublisher implements HttpRequest.BodyPublisher {
//
//        @Override
//        public long contentLength() {
//            return 0;
//        }
//
//        @Override
//        public void subscribe(Flow.Subscriber<? super ByteBuffer> subscriber) {
//
//        }
//    }
//
//    static class MySubscriber implements HttpResponse.BodySubscriber {
//
//        @Override
//        public CompletionStage getBody() {
//            return null;
//        }
//
//        @Override
//        public void onSubscribe(Flow.Subscription subscription) {
//
//        }
//
//        @Override
//        public void onNext(Object item) {
//
//        }
//
//        @Override
//        public void onError(Throwable throwable) {
//
//        }
//
//        @Override
//        public void onComplete() {
//
//        }
//    }
//
//    public static void main(String args[]) {
//        Context.printHeader(Experiment04_HttpClient.class);
//
//        try (var structuredExecutor = StructuredExecutor.open("Experiment20")) {
//
//            // https://apipheny.io/free-api/
//
//            var client = HttpClient.newBuilder()
//                    //.executor(structuredExecutor)
//                    .connectTimeout(Duration.ofSeconds(10))
//                    .build();
//
//            var request = HttpRequest.newBuilder()
//                    .uri(URI.create("https://www.boredapi.com/api/activity"))
//                    .build();
//
//            var request2 = HttpRequest.newBuilder()
//                    .uri(URI.create("https://www.boredapi.com/api/activity"))
//                    .POST(new MyPublisher())
//                    .build();
//
//            var response2 =
//                    client
//                        .sendAsync(request2, info -> HttpResponse.BodySubscribers.ofPublisher())
//                        .thenAccept(action -> {action.body().subscribe(new MySubscriber()); })
//                        .join();
//
//            var response3 =
//                    client.sendAsync(request2, info -> HttpResponse.BodySubscribers.ofPublisher());
//
//            var result3 = response3
//                    .thenAccept(action -> {action.body().subscribe(new MySubscriber()); })
//                    .join();
//
//            Callable<String> getActivity = () -> {
//                var response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                        .thenApply(HttpResponse::body)
//                        .join();
////                var response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
//                System.out.printf("early result = %s\n", response);
//                return response;
//            };
//
//            var result1 = structuredExecutor.fork(getActivity);
//
//            try {
//                structuredExecutor.joinUntil(Instant.now().plusSeconds(20));
//            }
//            catch (TimeoutException e) {
//                System.out.println("TimeoutException");
//                structuredExecutor.shutdown();
//                // structuredExecutor.join();
//                System.out.printf("result = %s", result1.resultNow());
//            }
//
//            System.out.printf("result1 = %s", result1.get());
//        }
//        catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
}
