package com.dev.luqman.greetings;

import com.dev.luqman.calculator.CalculatorServiceImpl;
import com.dev.luqman.greetings.GreetingServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GreetingServer {

    public static void main(String[] args) {
        io.grpc.Server server = ServerBuilder.forPort(9000)
                .addService(new GreetingServiceImpl())
                .addService(new CalculatorServiceImpl())
                .build();
        try {
            server.start();
            System.out.println("Server Started...");
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Server received shutdown");
                server.shutdown();
            }));

            server.awaitTermination();
        } catch (Exception ex) {
            System.err.println("Failed to start server ex:" + ex.getMessage());
            ex.printStackTrace();
        }

    }
}
