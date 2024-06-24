package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiOperationVerticle extends AbstractVerticle {
    private  int a = 10;
    private  int b = 20;
    private static final Logger logger = LoggerFactory.getLogger(MultiOperationVerticle.class);

    @Override
    public void start() throws Exception {
        logger.info("Verticle Start");
        vertx.setTimer(5000,id1 -> {
            int aresult = a + b;
            logger.info("Addition : {}", aresult);
            vertx.setTimer(3000,id2 -> {
                int mresult = (a*b)+aresult;
                logger.info("Multiplication : {}",mresult);
            });
        });
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MultiOperationVerticle());
//        vertx.deployVerticle(new WinnerGreeterVerticle());
//        DeploymentOptions options = new DeploymentOptions()
//                .setInstances(1);
//        vertx.deployVerticle("com.globalpayex.MultiOperationVerticle",options);
    }

}
