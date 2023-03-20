package com.btgpactual.monitory.verticle;

import com.btgpactual.monitory.consumer.sourcesystemupdate.SourceSystemUpdateConsumer;
import com.btgpactual.monitory.sqs.verticle.SqsVerticle;
import com.btgpactual.monitory.sqs.verticle.SqsVerticleDomain;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MonitorySourceSystemUpdateVerticle implements SqsVerticle {

    @ConfigProperty(name = "sqs.btgmonitory-sourcesystem-update.name")
    String queueName;
    @Override
    public SqsVerticleDomain sqsVerticleDomain() {
        return new SqsVerticleDomain(queueName, SourceSystemUpdateConsumer.CONSUMER_ALIAS);
    }
}
