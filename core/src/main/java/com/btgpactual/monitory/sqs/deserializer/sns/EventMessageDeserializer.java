package com.btgpactual.monitory.sqs.deserializer.sns;

import io.vertx.core.json.JsonObject;

public interface EventMessageDeserializer {

    JsonObject deserialize(String message);
}
