package com.sqsscheduler.consumer;

import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SimpleConsumer {

    public static final String CONSUMER_ALIAS = "simple-consumer";

    private static final Log LOG = LogFactory.getLog(SimpleConsumer.class);

    @ConsumeEvent(CONSUMER_ALIAS)
    public Uni<Void> consumeEvent(JsonObject eventMessage){
        return Uni.createFrom().voidItem()
                .invoke(() -> LOG.info("Received event ".concat(eventMessage.toString())));
    }
}
