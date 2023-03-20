package com.btgpactual.monitory.verticle;

import com.btgpactual.monitory.consumer.ttlreached.TTLlReachedConsumer;
import com.btgpactual.monitory.sqs.verticle.SqsVerticle;
import com.btgpactual.monitory.sqs.verticle.SqsVerticleDomain;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MonitoryTTLReachedVerticle implements SqsVerticle  {

    @ConfigProperty(name = "sqs.btgmonitory-ttl-reached.name")
    String queueName;

    @Override
    public SqsVerticleDomain sqsVerticleDomain() {
        return new SqsVerticleDomain(queueName, TTLlReachedConsumer.CONSUMER_ALIAS);
    }
}
