package com.dev.luqman.calculator;

import com.grpc.calculator.CalculatorServiceGrpc;
import com.grpc.calculator.DivResponse;
import com.grpc.calculator.Request;
import com.grpc.calculator.Response;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {

    @Override
    public void sum(Request request, StreamObserver<Response> responseObserver) {
        responseObserver.onNext(Response.newBuilder().setResult(request.getNumbers().getFirstNumber() + request.getNumbers().getSecondNumber()).build());
        responseObserver.onCompleted();
    }

    @Override
    public void sub(Request request, StreamObserver<Response> responseObserver) {
        responseObserver.onNext(Response.newBuilder().setResult(request.getNumbers().getFirstNumber() - request.getNumbers().getSecondNumber()).build());
        responseObserver.onCompleted();
    }

    @Override
    public void multi(Request request, StreamObserver<Response> responseObserver) {
        responseObserver.onNext(Response.newBuilder().setResult(request.getNumbers().getFirstNumber() * request.getNumbers().getSecondNumber()).build());
        responseObserver.onCompleted();
    }

    @Override
    public void div(Request request, StreamObserver<DivResponse> responseObserver) {

        Long firstNumber = request.getNumbers().getFirstNumber();
        Long secondNumber = request.getNumbers().getSecondNumber();

        if (secondNumber == 0) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Second number cannot be 0")
            .augmentDescription("Second number is " + secondNumber)
            .asRuntimeException());
        }
        else {
            Double result = (double) (firstNumber / secondNumber);
            responseObserver.onNext(DivResponse.newBuilder().setResult(result).build());
            responseObserver.onCompleted();
        }
    }
}
