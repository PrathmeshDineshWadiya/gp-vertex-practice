package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.ThreadingModel;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FiboSeriesVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(FiboSeriesVerticle.class);

    private void computeFibonacciSeries(int n){
        int first = 0;
        int second = 1;
        //compute the Fibonacci Series
        for(int i = 0;i < n;i++)
        {
            logger.info("{} where n = {}",first,n);
            int next = first + second;

            //deliberate blocking (delay)
            try{
                Thread.sleep(100);
            }
            catch (InterruptedException e){
                throw new RuntimeException(e);
            }

            first = second;
            second = next;
        }
    }

    @Override
    public void start() throws Exception {
        JsonObject config = config();
        int num1 = config.getInteger("n1");
        int num2 = config.getInteger("n2");
//        vertx.setTimer(2000,id -> this.computeFibonacciSeries(num1));
//        vertx.setTimer(2000,id -> this.computeFibonacciSeries(num1));
        this.computeFibonacciSeries(num1);
        this.computeFibonacciSeries(num2);
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        JsonObject config = new JsonObject()
                .put("n1",10)
                .put("n2",20);
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(config)
                        .setThreadingModel(ThreadingModel.WORKER);
        vertx.deployVerticle(new FiboSeriesVerticle(),options);
    }
}
