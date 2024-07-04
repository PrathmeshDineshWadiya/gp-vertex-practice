package com.globalpayex.routes;

import com.globalpayex.MultiOperationVerticle4;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.print.Book;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BooksRoute {

    private static final Logger logger = LoggerFactory.getLogger(BooksRoute.class);

//    private static int lastUsedId=3;

//    /* private static List<Book> books = Arrays.asList(
//            new Book(1,"Miracle Morning",60,650),
//            new Book(2,"Rich Dad Poor Dad",178,850),
//            new Book(3,"Java Programing",120,375)
//    ); */

//    private static ArrayList<Book> books;
//
//    public static Router init(Router router){
//
//        books = new ArrayList<>();
//        books.add(new Book(1,"Book-1",80,200));
//        books.add(new Book(2,"Book-2",120,450));
//        books.add(new Book(3,"Book-3",200,650));

//        List<Book> books = Arrays.asList(
//                new Book(1,"Miracle Morning",60,650),
//                new Book(2,"Rich Dad Poor Dad",178,850),
//                new Book(3,"Java Programing",120,375)
//        );
//        router.get("/books").handler(BooksRoute::getAllBooks);
//        router.get("/books/:bookId").handler(BooksRoute::getBook);
//        router.post("/books").handler(BooksRoute::newBook);
//
//
//        router.get("/books").handler(routingContext -> {
//            JsonArray data = new JsonArray(books);
//            routingContext
//                    .response()
//                    .putHeader("Content-Type","application/json")
//                    .end(data.encode());
//        });
//        return router;
//    }
    private static MongoClient mongoClient;

    public static Router init(Router router, Vertx vertx, JsonObject config){
        mongoClient = MongoClient.createShared(vertx,config);
        router.get("/books").handler(BooksRoute::getAllBooks);
//        router.get("/books/:bookId").handler(BooksRoute::getBook);
//        router.post("/books").handler(BooksRoute::newBook);
        return router;
    }

//    private static void newBook(RoutingContext routingContext) {
//        Book book = routingContext.body().asPojo(Book.class);
//        book.setId(++lastUsedId);
//        books.add(book);
//        logger.info("New Book {}",book);
//        routingContext.response()
//                .setStatusCode(201)
//                .putHeader("Content-Type","application/json")
//                .end(JsonObject.mapFrom(book).encode());
//    }

//    private static void getBook(RoutingContext routingContext) {
//        var bookId = Integer.parseInt(routingContext.pathParam("bookId"));
//        List<Book> foundBookList = books.stream()
//                .filter(book -> book.getId() == bookId)
//                .collect(Collectors.toList());
//        if(!foundBookList.isEmpty()){
//            Book book = foundBookList.get(0);
//            routingContext
//                    .response()
//                    .putHeader("Content-Type","application/json")
//                    .end(JsonObject.mapFrom(book).encode());
//        }
//        else{
//            JsonObject data = new JsonObject()
//                    .put("message",String.format("Book with id %s not found",bookId));
//            routingContext
//                    .response()
//                    .putHeader("Content-Type","application/json")
//                    .setStatusCode(404)
//                    .end(data.encode());
//        }
//    }

//    private static void getAllBooks(RoutingContext routingContext) {
//        JsonArray data = new JsonArray(books);
//        routingContext
//                .response()
//                .putHeader("Content-Type","application/json")
//                .end(data.encode());
//    }
    private static void getAllBooks(RoutingContext routingContext) {
        JsonObject query = buildGetAllBooksQuery(routingContext);

        Future<List<JsonObject>> future = mongoClient
                .find("books",query);
        future.onSuccess(bookJsonObjects -> {
            logger.info("Books {}",bookJsonObjects);
            List<JsonObject> responseJson = bookJsonObjects
                    .stream()
                    .map(BooksRoute::mapDbToResponseJson)
                    .collect(Collectors.toList());
            JsonArray responseData = new JsonArray(responseJson);
            routingContext
                    .response()
                    .putHeader("Content-Type","application/json")
                    .end(responseData.encode());
        });
        future.onFailure(exception -> {
            logger.error("Error in fetching books data {} - Reason {}",exception.getMessage(),exception.getCause());
            routingContext
                    .response()
                    .setStatusCode(500)
                    .end("Server Connection Error");
        });
    }

    private static JsonObject buildGetAllBooksQuery(RoutingContext routingContext){
        List<String> priceQp = routingContext.queryParam("price");
        List<String> pagesQp = routingContext.queryParam("pages");
        JsonObject query = new JsonObject();

        JsonArray orCondition = new JsonArray();

        if(!priceQp.isEmpty()){
            orCondition.add(new JsonObject().put(
                    "details.price",
                    new JsonObject().put("$gt",
                            Integer.parseInt(priceQp.get(0)))));
        }
        if(!pagesQp.isEmpty()){
            orCondition.add(new JsonObject().put(
                    "details.pages",
                    new JsonObject().put("$gt",
                            Integer.parseInt(pagesQp.get(0)))));
        }
        query.put("$or",orCondition);
        if(query.isEmpty())
        {
            return new JsonObject();
        }
        return query;
    }

    private static void handleGetAllBooksFailure(Throwable throwable){
        logger.error("Error in getting books {}",throwable.getMessage());

    }

    private static void handleGetAllBooks(List<JsonObject> dbJson, RoutingContext routingContext){
        logger.info("db json {}",dbJson);
        List<JsonObject> responseJson = dbJson
                .stream()
                .map(BooksRoute::mapDbToResponseJson)
                .collect(Collectors.toList());
        routingContext
                .response()
                .putHeader("Content-Type","application/json")
                .end(new JsonArray(responseJson).encode());
    }

    private static JsonObject mapDbToResponseJson(JsonObject dbJson) {
        return new JsonObject()
                .put("_id",dbJson.getString("_id"))
                .put("title",dbJson.getString("title"))
                .put("price",dbJson.getJsonObject("details").getInteger("price"))
                .put("pages",dbJson.getJsonObject("details").getInteger("pages"));
    }
}
