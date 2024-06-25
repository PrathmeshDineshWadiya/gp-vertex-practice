package com.globalpayex;

import io.vertx.core.*;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.OpenOption;

public class MultiOperationVerticle4 extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(MultiOperationVerticle4.class);

    private void performAddition(int a,int b){
        logger.info("Addition : {}",a+b);
    }

    private void performMultiplication(int a,int b){
        logger.info("Multiplication : {}",a*b);
    }

    private int computeFibonacciSeries(int n){
        int a = 0;
        int b = 1;
        logger.info("{} where n = {}",a,n);
        logger.info("{} where n = {}",b,n);
        int c = 0;
        int i = 2;
        while(i<n){
            c = a + b;
            logger.info("{} where n = {}",c,n);

            //deliberate blocking (delay)
            try{
                Thread.sleep(100);
            }
            catch (InterruptedException e){
                throw new RuntimeException(e);
            }

            a = b;
            b = c;
            i++;
        }
        return c;
    }

    /* private void computeFibonacciSeries(int n){
        int a = 0;
        int b = 1;
        logger.info("{} where n = {}",a,n);
        logger.info("{} where n = {}",b,n);
        int i = 2;
        while(i<n){
            int c = a + b;
            logger.info("{} where n = {}",c,n);

            //deliberate blocking (delay)
            try{
                Thread.sleep(100);
            }
            catch (InterruptedException e){
                throw new RuntimeException(e);
            }

            a = b;
            b = c;
            i++;
        }
    } */

    private void readFile(String filePath){
        //Non-blocking I/O
        OpenOptions options = new OpenOptions()
                .setCreate(false)
                .setRead(true);
        Future<AsyncFile> readFileFuture = vertx.fileSystem().open(filePath,options);
        readFileFuture.onSuccess(asyncFile -> {
            asyncFile.handler(buffer -> System.out.println(buffer))
                    .exceptionHandler(exception -> logger.info("Error in Reading FIle - {}",
                            exception.getMessage()));
            /* asyncFile.handler(System.out::println)
                    .exceptionHandler(exception -> logger.info("Error in Reading FIle - {}",
                            exception.getMessage())); */
        });
        readFileFuture.onFailure(exception -> logger.info("Error in Opening FIle - {}",
                exception.getMessage()));
    }

    @Override
    public void start() throws Exception {
        int a = config().getInteger("a");
        int b = config().getInteger("b");

        vertx.setTimer(1000,id -> this.readFile("build.gradl"));

        //schedule blocking operation on Event loop Thread
        vertx.setTimer(1000,id -> this.performAddition(a,b));
        vertx.setTimer(1000,id -> this.performMultiplication(a,b));
        //schedule blocking operation on Worker Thread
        /* vertx.executeBlocking(() ->{
            this.computeFibonacciSeries(a);
            return 0;
        });
        vertx.executeBlocking(() ->{
            this.computeFibonacciSeries(b);
            return 0;
        }); */
        vertx.executeBlocking(
                () -> this.computeFibonacciSeries(a),
                ar -> {
                    //ar - AsyncResult
                    if(ar.succeeded()){
                        int r = ar.result();
                        logger.info("Blocking Operation result is {}",r);
                    }
                });

        vertx.executeBlocking(
                () -> this.computeFibonacciSeries(b),
                ar -> {
                    //ar - AsyncResult
                    if(ar.succeeded()){
                        int r = ar.result();
                        logger.info("Blocking Operation result is {}",r);
                    }
                });
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        /* JsonObject config = new JsonObject()
                .put("a",100)
                .put("b",200);
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(config); */
        DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("a",10).put("b",25));
        vertx.deployVerticle(new MultiOperationVerticle4(),options);
    }
}
