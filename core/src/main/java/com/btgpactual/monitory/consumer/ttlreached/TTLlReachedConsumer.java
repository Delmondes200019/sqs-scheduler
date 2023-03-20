package com.btgpactual.monitory.consumer.ttlreached;

import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TTLlReachedConsumer {

    public static final String CONSUMER_ALIAS = "monitory-ttl";

    private static final Log logger = LogFactory.getLog(TTLlReachedConsumer.class);

    @ConsumeEvent(CONSUMER_ALIAS)
    public Uni<Void> consume(JsonObject message) {

        logger.info("TTL Event received. ".concat(message.toString()));
        return Uni.createFrom().voidItem();
    }
}
