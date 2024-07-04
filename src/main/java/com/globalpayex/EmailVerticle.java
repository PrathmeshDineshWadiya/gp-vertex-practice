package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.mail.StartTLSOptions;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(EmailVerticle.class);

    private static MongoClient mongoClient;
    private static MailClient mailClient;

    @Override
    public void start() throws Exception {
        mongoClient = MongoClient.createShared(vertx,config());
        MailConfig mailConfig = new MailConfig()
                .setHostname(config().getString("emailHostName"))
                        .setPort(config().getInteger("emailPort"))
                                .setStarttls(StartTLSOptions.REQUIRED)
                                        .setUsername(config().getString("emailUsername"))
                                                .setPassword(config().getString("emailPassword"));
        mailClient = MailClient.create(vertx, mailConfig);
        vertx
                .eventBus()
                .consumer("new.student",this::handleNewStudentEmail);
    }

    private void handleNewStudentEmail(Message<JsonObject> message) {
        String newStudentId = message.body().getString("_id");
        JsonObject query = new JsonObject()
                .put("_id",newStudentId);
        this.mongoClient.findOne("students",query,null)
                .onSuccess(this::handleStudentJson)
                .onFailure(this::handleStudentFailure);
    }

    private void handleStudentFailure(Throwable throwable) {
        logger.error("Error in getting Student {}",throwable.getMessage());
    }

    private void handleStudentJson(JsonObject studentObjJson) {
        if(studentObjJson != null){
            MailMessage message = new MailMessage();
            message.setFrom(config().getString("emailUsername"));
            message.setTo(studentObjJson.getString("email"));
            message.setSubject("Welcome to the Student Portal");
            message.setText(
                    String.format("Hey %s,Welcome to the college portal",studentObjJson.getString("username"))
            );
            mailClient.sendMail(message)
                    .onSuccess(mailResult ->
                            logger.info("Email sent successfully on Gmail account {}",studentObjJson.getString("email"))
                    )
                    .onFailure(exception ->
                            logger.info("Error sending email {}",exception.getMessage())
                    );
            logger.info("Email sent successfully to student email-id '{}'",studentObjJson.getString("email"));
        }

    }

}
