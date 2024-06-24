package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WinnerGreeterVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(WinnerGreeterVerticle.class);

    public static final List<String> students = Arrays.asList("Mehul","Prathmesh","Viraj","Jane","Anirudha");


    @Override
    public void start() throws Exception {
        logger.info("Verticle Start");

        vertx.setTimer(1000,id -> logger.info("And"));
        vertx.setTimer(2000,id -> logger.info("the"));
        vertx.setTimer(3000,id -> logger.info("Winner"));
        vertx.setTimer(4000,id -> logger.info("is"));
        vertx.setTimer(9000,this::printWinner);

        logger.info("Timers initialized in the Verticle");
    }

    private void printWinner(Long aLong) {
        var random = new Random();
        String winner = students.get(random.nextInt(students.size()));
        logger.info(winner);
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        //vertx.deployVerticle(new WinnerGreeterVerticle());
        DeploymentOptions options = new DeploymentOptions()
                .setInstances(2);
        vertx.deployVerticle("com.globalpayex.WinnerGreeterVerticle",options);

    }
}
