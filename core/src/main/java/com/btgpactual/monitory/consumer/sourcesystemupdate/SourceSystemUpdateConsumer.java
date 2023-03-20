package com.btgpactual.monitory.consumer.sourcesystemupdate;

import com.btgpactual.monitory.consumer.create.BtgMonitoryConsumer;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SourceSystemUpdateConsumer {

    public static final String CONSUMER_ALIAS = "monitory-source-system-update";

    private static final Log logger = LogFactory.getLog(BtgMonitoryConsumer.class);

    @ConsumeEvent(CONSUMER_ALIAS)
    public Uni<Void> consume(JsonObject message) {

        logger.info("Receveid source system update event ".concat(message.toString()));
        return Uni.createFrom().voidItem();
    }
}
