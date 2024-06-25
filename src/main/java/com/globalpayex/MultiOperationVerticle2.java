package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiOperationVerticle2 extends AbstractVerticle {

    private  int a = 10;
    private  int b = 20;
    private static final Logger logger = LoggerFactory.getLogger(MultiOperationVerticle2.class);

    private Future<Integer> performAddition(){
        Promise<Integer> promise = Promise.promise();
        vertx.setTimer(5000,id1 -> {
            int aresult = a + b;
            promise.complete(aresult);
        });
        return promise.future();
    }

    private Future<Integer> performMultiplication(int result){
        Promise<Integer> promise = Promise.promise();
        vertx.setTimer(3000,id -> {
            int mresult = (a * b) + result;
            promise.complete(mresult);
        });
        return promise.future();
    }

    @Override
    public void start() throws Exception {
        Future<Integer> additionFuture = performAddition();
        //additionFuture.onSuccess(additionResult -> logger.info("Addition : {}",additionResult));
        Future<Integer> multiplicationFuture = additionFuture
                .compose(additionResult -> {
                    logger.info("Addition : {}",additionResult);
                    return performMultiplication(additionResult);
                });
        multiplicationFuture.onSuccess(multiplicationResult ->
                logger.info("Multiplication : {}",multiplicationResult));
        logger.info("Start Initialized");
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MultiOperationVerticle2());
    }

}
