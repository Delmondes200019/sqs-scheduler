package com.btgpactual.monitory.sqs.deserializer.sns;

import io.vertx.core.json.JsonObject;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SNSMessageDeserialzer {

    public JsonObject deserialize(String message){

        JsonObject entry = new JsonObject(message);
        return new JsonObject(entry.getString("Message"));
    }
}
