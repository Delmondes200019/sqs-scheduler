package com.sqsscheduler.sqs.deserializer.sns;

import io.vertx.core.json.JsonObject;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DefaultEventMessageDeserializer implements EventMessageDeserializer {
    @Override
    public JsonObject deserialize(String message) {
        return new JsonObject(message);
    }
}
