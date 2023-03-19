package com.btgpactual.monitory.verticle;

import com.btgpactual.monitory.sqs.verticle.SqsVerticle;
import com.btgpactual.monitory.sqs.verticle.SqsVerticleDomain;
import com.btgpactual.monitory.consumer.BtgMonitoryConsumer;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BtgMonitorySqsVerticleImpl implements SqsVerticle {

    @ConfigProperty(name = "sqs.btgmonitory.name")
    String queueName;

    @Override
    public SqsVerticleDomain sqsVerticleDomain() {
        return new SqsVerticleDomain(queueName, BtgMonitoryConsumer.CONSUMER_ALIAS);
    }
}
