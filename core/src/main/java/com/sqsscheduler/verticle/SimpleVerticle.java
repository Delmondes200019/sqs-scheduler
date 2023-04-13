package com.sqsscheduler.verticle;

import com.sqsscheduler.consumer.SimpleConsumer;
import com.sqsscheduler.sqs.verticle.SqsVerticle;
import com.sqsscheduler.sqs.verticle.SqsVerticleDomain;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SimpleVerticle implements SqsVerticle {

    @ConfigProperty(name = "sqs.simple-queue")
    String queueName;

    @Override
    public SqsVerticleDomain sqsVerticleDomain() {
        return new SqsVerticleDomain(queueName, SimpleConsumer.CONSUMER_ALIAS);
    }
}
