package com.dev.luqman.calculator;

import com.grpc.calculator.CalculatorServiceGrpc;
import com.grpc.calculator.Numbers;
import com.grpc.calculator.Request;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {

    public void calculator(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        Request request = Request.newBuilder().setNumbers(Numbers.newBuilder().setFirstNumber(100).setSecondNumber(10)).build();

        System.out.println("Sum :" + stub.sum(request).getResult());
        System.out.println("Sub :" + stub.sub(request).getResult());
        System.out.println("Multi :" + stub.multi(request).getResult());
        System.out.println("Div :" + stub.div(request).getResult());
    }
    public static void main(String[] args) {

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9001).usePlaintext().build();
        CalculatorClient client = new CalculatorClient();
        client.calculator(channel);


        channel.shutdown();
    }
}
