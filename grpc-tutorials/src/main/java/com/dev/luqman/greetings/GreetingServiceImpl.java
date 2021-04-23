package com.dev.luqman.greetings;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.stream.IntStream;

public class GreetingServiceImpl extends GreetingServiceGrpc.GreetingServiceImplBase {
    @Override
    public void greet(GreetingRequest request, StreamObserver<GreetingResponse> responseObserver) {
        responseObserver.onNext(GreetingResponse
                .newBuilder()
                .setResult(java.lang.String.format("Hello! %s %s", request.getGreeting().getFirstName(), request.getGreeting().getLastName()))
                .build());

        responseObserver.onCompleted();
    }

    @Override
    public void greetManyTimes(GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver) {

        final java.lang.String firstName = request.getGreeting().getFirstName();
        // This is a Server Streaming so Server will respond multiple times using onNext
        IntStream.range(0, 110)
                .forEach(i -> {
                    try {
                        // Keep sending the response to the client
                        responseObserver.onNext(GreetManyTimesResponse
                                .newBuilder()
                                .setResult(String.format("Hello From Server %s - Index %d", firstName, i))
                                .build());

                        // Wait for one second b/w responses
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        responseObserver.onError(Status.INTERNAL.withDescription("Thread interrupted while sleeping").asRuntimeException());
                    }
                });
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<LongGreetRequest> longGreet(StreamObserver<LongGreetResponse> responseObserver) {

        StringBuilder sb = new StringBuilder("Hello!");
        StreamObserver<LongGreetRequest> requestStreamObserver = new StreamObserver<LongGreetRequest>() {
            @Override
            public void onNext(LongGreetRequest longGreetRequest) {
                sb.append(" ").append(longGreetRequest.getGreetings().getFirstName());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Got an Error, keep continue  ex:"+ throwable.getMessage());
                // Or throw error response
                // responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                // Client has finished sending request;
                responseObserver.onNext(LongGreetResponse.newBuilder().setResult(sb.toString()).build());
                responseObserver.onCompleted();
            }
        };

        return requestStreamObserver;
    }


    @Override
    public StreamObserver<GreetEveryOneRequest> greetEveryOne(StreamObserver<GreetEveryOneResponse> responseObserver) {

        StreamObserver<GreetEveryOneRequest> requestStreamObserver = new StreamObserver<GreetEveryOneRequest>() {
            @Override
            public void onNext(GreetEveryOneRequest greetEveryOneRequest) {
                System.out.println("Server received request for " + greetEveryOneRequest.getGreetings().getFirstName());
                responseObserver.onNext(GreetEveryOneResponse.newBuilder()
                .setResult(String.format("Hello from server %s", greetEveryOneRequest.getGreetings().getFirstName())).build());
            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
        return requestStreamObserver;
    }
}
