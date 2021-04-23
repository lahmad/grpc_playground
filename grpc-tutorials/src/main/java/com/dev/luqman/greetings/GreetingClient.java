package com.dev.luqman.greetings;

import com.grpc.calculator.CalculatorServiceGrpc;
import com.grpc.calculator.Numbers;
import com.grpc.calculator.Request;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

    public static void main(String[] args) throws InterruptedException {
        final GreetingClient client = new GreetingClient();

        // Channel is using non-ssl
        final ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9000)
                .usePlaintext()
                .build();
       // client.greetUnary(channel);
       // client.calculatorUnary(channel);
       // client.greetMultipleTimes(channel);

       // client.longGreet(channel);

        client.greetEveryOne(channel);

        channel.shutdown();
    }

    public void longGreet(ManagedChannel channel) throws InterruptedException {

        // Latch to wait to receive messages from server
        CountDownLatch latch = new CountDownLatch(1);

        //Async client streaming
        GreetingServiceGrpc.GreetingServiceStub async = GreetingServiceGrpc.newStub(channel);

        StreamObserver<LongGreetRequest> request = async.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse longGreetResponse) {
                // Server response
                System.out.println("Received message from the server");
                System.out.println(longGreetResponse.getResult());
            }

            @Override
            public void onError(Throwable throwable) {
                //Server error
                System.err.println("Server Failure ex:" + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                // Server completed
                System.out.println("Server completed!");
                latch.countDown(); //decrease the latch
            }
        });

        // Send multiple requests to the server using streaming one one TCP connection
        Arrays.asList("John", "Paul", "Peter", "Marc")
                .forEach(name -> {
                    request.onNext(LongGreetRequest.newBuilder()
                            .setGreetings(Greeting.newBuilder().setFirstName(name))
                            .build());
                });

        request.onCompleted();
        latch.await(30000, TimeUnit.MILLISECONDS); // wait for seconds on this thread
    }

    public void greetEveryOne(ManagedChannel channel) throws InterruptedException {
        // Latch to wait to receive messages from server
        CountDownLatch latch = new CountDownLatch(1);

        //Async client streaming
        GreetingServiceGrpc.GreetingServiceStub async = GreetingServiceGrpc.newStub(channel);

        StreamObserver<GreetEveryOneRequest> request = async.greetEveryOne(new StreamObserver<GreetEveryOneResponse>() {
            @Override
            public void onNext(GreetEveryOneResponse longGreetResponse) {
                // Server response
                System.out.println("Received message from the server");
                System.out.println(longGreetResponse.getResult());
            }

            @Override
            public void onError(Throwable throwable) {
                //Server error
                latch.countDown();
                System.err.println("Server Failure ex:" + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                // Server completed
                System.out.println("Server completed!");
                latch.countDown(); //decrease the latch
            }
        });

        // Send multiple requests to the server using streaming one one TCP connection
        Arrays.asList("John", "Paul", "Peter", "Marc")
                .forEach(name -> {
                    request.onNext(GreetEveryOneRequest.newBuilder()
                            .setGreetings(Greeting.newBuilder().setFirstName(name))
                            .build());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                });

        request.onCompleted();
        latch.await(30000, TimeUnit.MILLISECONDS); // wait for seconds on this thread
    }
    public void greetMultipleTimes(ManagedChannel channel) {
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
        GreetManyTimesRequest request = GreetManyTimesRequest.newBuilder()
                .setGreeting(Greeting.newBuilder().setFirstName("John").setLastName("Doe")).build();
        // Block for each response
        stub.greetManyTimes(request).forEachRemaining(response -> {
            System.out.println("Server Response -> " + response.getResult());
        });
    }
    public void greetUnary(ManagedChannel channel) {

        // Blocking call
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
        GreetingRequest request = GreetingRequest
                .newBuilder()
                .setGreeting(Greeting.newBuilder().setFirstName("John").setLastName("Doe"))
                .build();
        GreetingResponse response = stub.greet(request);
        System.out.println("Received from server " + response);
    }

    public void calculatorUnary(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        Request request = Request.newBuilder().setNumbers(Numbers.newBuilder().setFirstNumber(10).setSecondNumber(20)).build();

        System.out.println("Sum Result " + stub.sum(request));
        System.out.println("Sub Result " + stub.sub(request));
        System.out.println("Multi Result " + stub.multi(request));
        System.out.println("Div Result " + stub.div(request));

        try {
            System.out.println("Div Error " + stub.div(Request.newBuilder().setNumbers(Numbers.newBuilder().setFirstNumber(10).setSecondNumber(0)).build()));

        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
