package net.kolotyluk.loom;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * <h1>Flow Experiments</h1>
 * <p>
 *     {@link Flow} is Java's default <a href="http://www.reactive-streams.org/">Reactive Streams</a>
 *     framework that addresses the
 *     <a href="https://en.wikipedia.org/wiki/Producer%E2%80%93consumer_problem">Producer–Consumer Problem</a>
 *     by restricting how many messages the Producer (Publisher) can send the Consumer (Subscriber), such that
 *     the Producer will block when the Consumer cannot consume any more messages, using a technique called
 *     <a href="https://www.reactivemanifesto.org/glossary#Back-Pressure">Back Pressure</a>.
 * </p>
 * <h2 style="padding-top: 12pt;">Why Loom</h2>
 * <p>
 *     As we have learned before, using Java Parallel Streams with Transactional Tasks is problematic, where
 *     Virtual Threads handle Transactional Tasks better than Platform Threads. So, in terms of a classic
 *     Producer/Consumer application, where the Producers and/or the Consumers are Transactional, using
 *     Virtual Threads can be an effective improvement over regular thread pools.
 * </p>
 * <h2 style="padding-top: 12pt;">Why Flow</h2>
 * <p>
 *     If Project Loom solves several problems that were solved by Reactive Programming methods, why do we
 *     want to continue using Reactive Programming methods such as Reactive Streams?
 *     <ol>
 *         <li>
 *             Given there is an existing and growing ecosystem of Reactive Solutions, we will have to
 *             go with the momentum for a while.
 *         </li>
 *         <li>
 *             It is expected that most Reactive Frameworks will refactor their code to use Project Loom
 *             capabilities to exploit the obvious advantages, such as support in the JVM.
 *         </li>
 *         <li>
 *             Backpressure is a really valuable technique for some applications, and that is not a feature
 *             of Project Loom.
 *         </li>
 *     </ol>
 * </p>
 * <p>
 *     Increasingly many APIs are providing both Synchronous and Asynchronous capabilities to better handle the
 *     reality of asynchronous and concurrent programming practices. In some case, an API will return a {@link Future}
 *     or {@link CompletableFuture} as a result, and it is up to the API User to best decide how to consume that.
 *     Increasingly, however, many APIs are now returning a {@link java.util.concurrent.Flow.Publisher}, or taking
 *     one as an argument to the API; similarly for {@link java.util.concurrent.Flow.Subscriber}. Such patterns
 *     are often useful when the data sets are large, and/or dynamically generated. For example:
 * </p>
 * <pre>
 * var myPublisher  = new MyPublisher();  // produces an HTTP Request  Body
 * var mySubscriber = new MySubscriber(); // consumes an HTTP Response Body
 *
 * var request = HttpRequest.newBuilder()
 *     .uri(URI.create("https://www.boredapi.com/api/activity"))
 *     .POST(myPublisher)
 *     .build();
 *
 * client
 *     .sendAsync(request, info -> HttpResponse.BodySubscribers.ofPublisher())
 *     .thenAccept(action -> {action.body().subscribe(mySubscriber);})
 *     .join();
 *
 * // or
 *
 * CompletableFuture&lt;HttpResponse&lt;Flow.Publisher&lt;List&lt;ByteBuffer>>>> response =
 *     client.sendAsync(request, info ->
 *         HttpResponse.BodySubscribers.ofPublisher());
 *
 * response
 *     .thenAccept(action -> {action.body().subscribe(mySubscriber);})
 *     .join();
 * </pre>
 * <p>
 *     where <tt>myPublisher</tt> will produce the HTTP Request Body and <tt>mySubscriber</tt> will consume the
 *     HTTP Response Body. <em>Note: I wanted to reveal how complex the result type of <tt>response</tt> would be.</em>
 *     Yes, these are rather elaborate ways to send and receive HTTP bodies, but sometimes these kinds of data
 *     transport are non-trivial. Whether it be Request Body Production, or Response Body Consumption, we may want
 *     to maximize/optimize <strong><em>throughput</em></strong> and ideally both Flow and Virtual Threads can help.
 * </p>
 * <p>
 *     In the world of <a href="https://en.wikipedia.org/wiki/HTTP/2">HTTP/2</a> and
 *     <a href="https://en.wikipedia.org/wiki/HTTP/3">HTTP/3</a>, these protocols allow for the transport
 *     of very large payloads, and even dynamic streaming, where using Reactive Streams such as Flow can
 *     better <em>produce</em>, <em>process</em>, and <em>consume</em> such data. But, will Project Loom improve the
 *     throughput of these applications? However, we will explore this more in future experiments related to HTTP,
 *     because I just wanted to introduce one of the leading rationales for using Flow.
 * </p>
 * <h1 style="padding-top: 12pt;">Publisher/Subscriber</h1>
 * <p>
 * However, {@link Flow} is also a
 * <a href="https://en.wikipedia.org/wiki/Publish%E2%80%93subscribe_pattern">Publish-Subscribe Pattern</a>
 * </p>
 * @see <a href="https://www.tutorialspoint.com/how-to-implement-reactive-streams-using-flow-api-in-java-9">How to implement reactive streams using Flow API in Java 9?</a>
 * @see <a href="http://www.reactive-streams.org/">Reactive Streams</a>
 * @see <a href="https://www.reactivemanifesto.org/glossary#Back-Pressure">Back Pressure</a>
 * @see <a href="https://www.youtube.com/watch?v=_stAxdjx8qk">Don’t use Reactive Streams in Java 9+</a>
 * @see <a href="https://www.javacodegeeks.com/2019/09/should-parallel-streams-transaction-context.html">Can/Should I use parallel streams in a transaction context?</a>
 */
public class Experiment10_Flow {

    final static int MAXIMUM_BUFFER_CAPACITY = 1;

    public static void main(String args[]) {
        Context.printHeader(Experiment10_Flow.class);

        try (var structuredExecutor = StructuredExecutor.open("Experiment30");
             var submissionPublisher = new SubmissionPublisher<Integer>(structuredExecutor, MAXIMUM_BUFFER_CAPACITY)) {

            var subscriber1 = new MySubscriber(1);
            var subscriber2 = new MySubscriber(2);

            // As a rule, add our subscribers before producing any messages because it's the subscribers that are
            // buffered, not the publisher. Also, generally, each subscriber will get its own thread from the
            // Executor so that it can consume messages asynchronously.
            submissionPublisher.subscribe(subscriber1);
            submissionPublisher.subscribe(subscriber2);

            // submissionPublisher.offer(1, 10, TimeUnit.SECONDS, (subscriber, msg) -> {return false;});

            // submissionPublisher.submit(1);

            var result = IntStream.range(0, 16)
                    .mapToObj(item -> {
                        System.out.printf("item = %d, Thread ID = %s\n", item, Thread.currentThread());
                        // Note, the publisher may block here if the buffers on the subscribers are full,
                        // As this is a principle feature of back-pressure.
                        return Integer.valueOf(submissionPublisher.submit(item));
                    })
                    .toList();

            structuredExecutor.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try (var structuredExecutor = StructuredExecutor.open("Experiment30");
             var submissionPublisher = new SubmissionPublisher<Integer>(structuredExecutor, MAXIMUM_BUFFER_CAPACITY)) {

            var subscriber1 = new MySubscriber(1);
            var subscriber2 = new MySubscriber(2);

            submissionPublisher.subscribe(subscriber1);
            submissionPublisher.subscribe(subscriber2);

            // submissionPublisher.offer(1, 10, TimeUnit.SECONDS, (subscriber, msg) -> {return false;});

            // submissionPublisher.submit(1);

            var result = IntStream.range(0, 16)
                    .mapToObj(item -> {
                        System.out.printf("item = %d, Thread ID = %s\n", item, Thread.currentThread());
                        return structuredExecutor.fork(() -> {
                            System.out.printf("\tpublisher task = %d, Thread ID = %s\n", item, Thread.currentThread());
                            // By introducing random lag, we pretty much guarantee items are out of order in the buffers.
                            // However, when message are produced synchronously, they will always remain in order.
                            new Lag(Duration.ofMillis(1),Duration.ofMillis(10)).sleep();
                            // Note, the publisher may block here if the buffers on the subscribers are full,
                            // As this is a principle feature of back-pressure. However, any thread can produce
                            // messages, as we are doing here, and with Virtual Threads, we don't care about blocking
                            // as much as with Platform Threads, because we can design applications with many more
                            // Virtual Threads than Platform Threads.
                            return submissionPublisher.submit(item);
                        });
                    })
                    .toList();

            structuredExecutor.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class MySubscriber implements Flow.Subscriber<Integer> {
        final private int id;

        private Flow.Subscription subscription;

        // Thread Safe Concurrent Data Structure
        private BlockingQueue<String> queue = new ArrayBlockingQueue<String>(20);

        MySubscriber(int id) {
            this.id = id;
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            // Critical that we do this, because this is our sessions with our Publisher
            this.subscription = subscription;
            // Critical, also, we do this, because we cannot consume anything without requesting it
            subscription.request(1);
            System.out.printf("Subscriber %d Subscribed on %s\n", id, Thread.currentThread());
        }

        @Override
        public void onNext(Integer item) {
            var signature = "\tsubscriber %d onNext(%d), Thread ID = %s\n".formatted(id, item, Thread.currentThread());
            System.out.printf(signature);
            queue.add(signature);
            // System.out.println("Consumed " + item);
            // Critical, also, we do this, because we cannot consume anything without requesting it
            subscription.request(1);
        }

        @Override
        public void onError(Throwable throwable) {
            throwable.printStackTrace();
        }

        @Override
        public void onComplete() {
            System.out.printf("Subscriber %d Completed on %s\n", id, Thread.currentThread());
            queue.forEach(item -> System.out.print("\tqueue item:" + item));
        }
    }
}
