package com.btgpactual.monitory.sqs.verticle;
public class SqsVerticleDomain {

    private String queueName;
    private String eventBusConsumer;

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
}
