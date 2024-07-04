package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeployerVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsVerticle.class);

    @Override
    public void start() throws Exception {
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject()
                        .put("port",8083)
                        .put("connection_string","mongodb+srv://admin:admin123@cluster0.hsvt2xs.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0")
                        .put("db_name","college_db")
                        .put("useObjectId",true)
                        .put("emailHostName","smtp.gmail.com")
                        .put("emailPort",587)
                        .put("emailUsername","prathmesh.wadiya17@gmail.com")
                        .put("emailPassword","wbqx lsbq nnbl xsyg")
                );
        vertx.deployVerticle(new FirstHttpServer(),options);
        vertx.deployVerticle(new StatisticsVerticle(),options);
        vertx.deployVerticle(new EmailVerticle(),options);
        logger.info("Verticle Deployed!");
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new DeployerVerticle());
    }
}
