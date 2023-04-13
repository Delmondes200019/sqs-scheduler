package com.sqsscheduler.sqs.verticle;

import com.sqsscheduler.sqs.deserializer.sns.EventMessageDeserializer;

public class SqsVerticleDomain {

    private final String queueName;
    private final String eventBusConsumer;

    private EventMessageDeserializer eventMessageDeserializer;

    public SqsVerticleDomain(String queueName, String eventBusConsumer){
        this.eventBusConsumer = eventBusConsumer;
        this.queueName = queueName;
    }

    public String getEventBusConsumer() {
        return eventBusConsumer;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setEventMessageDeserializer(EventMessageDeserializer eventMessageDeserializer) {
        this.eventMessageDeserializer = eventMessageDeserializer;
    }

    public EventMessageDeserializer getEventMessageDeserializer() {
        return eventMessageDeserializer;
    }
}
