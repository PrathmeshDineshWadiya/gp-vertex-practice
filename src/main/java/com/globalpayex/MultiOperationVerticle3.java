package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiOperationVerticle3 extends AbstractVerticle {

    private  int a = 10;
    private  int b = 20;
    private static final Logger logger = LoggerFactory.getLogger(MultiOperationVerticle3.class);

    private Future<Integer> performAddition(){
        Promise<Integer> promise = Promise.promise();
        vertx.setTimer(3000,id1 -> {
            int aresult = a + b;
            promise.complete(aresult);
        });
        return promise.future();
    }

    private Future<Integer> performMultiplication(){
        Promise<Integer> promise = Promise.promise();
        vertx.setTimer(3000,id -> {
            int mresult = a * b;
            promise.complete(mresult);
        });
        return promise.future();
    }

    @Override
    public void start() throws Exception {
        Future<Integer> addititonFuture = performAddition();
        Future<Integer> multiplicationFuture = performMultiplication();
        Future.all(addititonFuture,multiplicationFuture)
                .onSuccess(result -> {
                    logger.info("Addition : {}",addititonFuture.result());
                    logger.info("Multiplication : {}",multiplicationFuture.result());
                });
        logger.info("Verticle Initialized - Parallel asynchronous operations");
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MultiOperationVerticle3());
    }
}
