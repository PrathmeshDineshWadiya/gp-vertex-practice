package com.globalpayex.routes;

import com.globalpayex.StatisticsVerticle;
import com.globalpayex.entities.Book;
import io.vertx.core.Vertx;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class AppRouter {

    private static final Logger logger = LoggerFactory.getLogger(AppRouter.class);

    public static Router init(Vertx vertx, JsonObject config){
//        List<Book> books = Arrays.asList(
//                new Book(1,"Miracle Morning",60,650),
//                new Book(2,"Rich Dad Poor Dad",178,850),
//                new Book(3,"Java Programing",120,375)
//        );
        Router router = Router.router(vertx);
        router.post().handler(BodyHandler.create());
        router = BooksRoute.init(router,vertx,config);
        router = StudentsRoute.init(router,vertx,config);

        router.get("/download")
                .handler(routingContext -> handleDownload(routingContext,vertx));

        /* router.get("/books").handler(routingContext -> {
            JsonArray data = new JsonArray(books);
            routingContext
                    .response()
                    .putHeader("Content-Type","application/json")
                    .end(data.encode());
        }); */
        return router;
    }


    private static void handleDownload(RoutingContext routingContext,Vertx vertx) {
        OpenOptions options = new OpenOptions()
                .setRead(true);
        vertx.fileSystem().open("audio/Music.mp3",options)
                .onSuccess(asyncFile -> {
                    HttpServerResponse response = routingContext.response();
                    response
                            .setStatusCode(200)
                            .putHeader("Content-Type","audio/mp3")
                            .putHeader("Content-Disposition","attachment")
                            .setChunked(true);

                    asyncFile.pipeTo(response); //Internally manages Back Pressure

                    /* asyncFile.handler(buffer -> {
                        response.write(buffer);
                        if(response.writeQueueFull()){
                            asyncFile.pause();
                            response.drainHandler(v -> asyncFile.resume());
                        }
                    });
                    asyncFile.endHandler(v -> response.end()); */
                })
                .onFailure(exception ->
                        logger.error("{}",exception.getMessage()));
    }
}
