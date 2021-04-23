package com.dev.luqman.calculator;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class CalculatorServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(9001)
                .addService(new CalculatorServiceImpl())
                .build();

        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            System.out.println("Server received shutdown event");
            server.shutdown();
            System.out.println("Server shutdown.");
        }));

        server.awaitTermination();
    }
}
