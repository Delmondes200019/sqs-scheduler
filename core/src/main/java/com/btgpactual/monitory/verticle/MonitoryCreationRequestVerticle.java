package com.btgpactual.monitory.verticle;

import com.btgpactual.monitory.consumer.create.BtgMonitoryConsumer;
import com.btgpactual.monitory.sqs.deserializer.sns.SNSMessageDeserialzer;
import com.btgpactual.monitory.sqs.verticle.SqsVerticle;
import com.btgpactual.monitory.sqs.verticle.SqsVerticleDomain;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MonitoryCreationRequestVerticle implements SqsVerticle {

    @ConfigProperty(name = "sqs.btgmonitory-create.name")
    String queueName;

    @Override
    public SqsVerticleDomain sqsVerticleDomain() {

        SqsVerticleDomain sqsVerticleDomain = new SqsVerticleDomain(queueName, BtgMonitoryConsumer.CONSUMER_ALIAS);
        sqsVerticleDomain.setEventMessageDeserializer(new SNSMessageDeserialzer());
        return sqsVerticleDomain;
    }
}
