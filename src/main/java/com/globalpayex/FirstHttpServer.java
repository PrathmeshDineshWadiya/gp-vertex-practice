package com.globalpayex;

import com.globalpayex.entities.Book;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class FirstHttpServer extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(FirstHttpServer.class);

    @Override
    public void start() throws Exception {
        //dummy database
        List<Book> books = Arrays.asList(
                new Book(1,"Miracle Morning",60,650),
                new Book(2,"Rich Dad Poor Dad",178,850),
                new Book(3,"Java Programing",120,375)
        );

        Router router = Router.router(vertx);
        router.get("/books").handler(routingContext -> {
            JsonArray data = new JsonArray(books);
            routingContext
                    .response()
                    .putHeader("Content-Type","application/json")
                    .end(data.encode());
        });

        /* Future<HttpServer> serverFuture = vertx.createHttpServer()
                .requestHandler(request -> request.response().end("Hello World"))
                .listen(config().getInteger("port")); */
        Future<HttpServer> serverFuture = vertx.createHttpServer()
                .requestHandler(router)
                .listen(config().getInteger("port"));

        serverFuture.onSuccess(
                httpServer -> logger.info("Server running on Port {}",httpServer.actualPort())
        );
        serverFuture.onFailure(
                httpServer -> logger.info("Cannot start server {}",httpServer.getMessage())
        );
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("port",8083));
        vertx.deployVerticle(new FirstHttpServer(),options);
    }
}
