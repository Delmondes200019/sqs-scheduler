package com.sqsscheduler.sqs.scheduler;

import com.sqsscheduler.sqs.deserializer.sns.DefaultEventMessageDeserializer;
import com.sqsscheduler.sqs.deserializer.sns.EventMessageDeserializer;
import com.sqsscheduler.sqs.verticle.SqsVerticle;
import com.sqsscheduler.sqs.verticle.SqsVerticleDomain;
import io.quarkus.arc.All;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

public class SQSMessageScheduler {

    private static final String QUEUE_NAME = "btg-monitory";
    private static final String SCHEDULE_EXECUTION_TIME = "10s";

    private static final Log logger = LogFactory.getLog(SQSMessageScheduler.class);

    @Inject
    EventBus eventBus;

    @Inject
    SqsAsyncClient sqsAsyncClient;

    @All
    @Inject
    List<SqsVerticle> sqsVerticles;

    @Scheduled(every = SCHEDULE_EXECUTION_TIME, skipExecutionIf = SqsSkiptPredicate.class)
    Uni<Void> execute() {
        return Multi.createFrom()
                .iterable(sqsVerticles)
                .map(SqsVerticle::sqsVerticleDomain)
                .call(sqsVerticleDomain -> processQueueMessage(sqsVerticleDomain))
                .collect()
                .last()
                .replaceWithVoid();
    }

    private Uni<Void> processQueueMessage(SqsVerticleDomain sqsVerticleDomain) {
        return getGetQueueUrl(sqsVerticleDomain.getQueueName())
                .chain(getQueueUrlResponse -> receiveQueueMessage(getQueueUrlResponse)
                        .chain(receiveMessageResponse -> assertQueueHasMessagesOrReturnNull(receiveMessageResponse))
                        .onItem()
                        .ifNotNull()
                        .call(receiveMessageResponse -> processQueueMessage(sqsVerticleDomain, receiveMessageResponse))
                        .replaceWithVoid());
    }

    private Uni<DeleteMessageResponse> processQueueMessage(SqsVerticleDomain sqsVerticleDomain, ReceiveMessageResponse receiveMessageResponse) {
        return getFirstMessage(receiveMessageResponse)
                .chain(message -> sendEventBusRequest(sqsVerticleDomain, message)
                        .chain(objectMessage -> deleteMessageFromQueue(sqsVerticleDomain.getQueueName(), message)));
    }

    private static Uni<ReceiveMessageResponse> assertQueueHasMessagesOrReturnNull(ReceiveMessageResponse receiveMessageResponse) {
        if (!receiveMessageResponse.hasMessages()) {
            return Uni.createFrom().nullItem();
        }
        return Uni.createFrom().item(receiveMessageResponse);
    }

    private Uni<DeleteMessageResponse> deleteMessageFromQueue(String queueName, Message message) {
        return getGetQueueUrl(queueName)
                .chain(getQueueUrlResponse -> deleteMessageFromQueue(message, getQueueUrlResponse));
    }

    private Uni<DeleteMessageResponse> deleteMessageFromQueue(Message message, GetQueueUrlResponse getQueueUrlResponse) {
        return Uni.createFrom()
                .completionStage(sqsAsyncClient.deleteMessage(DeleteMessageRequest.builder()
                        .queueUrl(getQueueUrlResponse.queueUrl())
                        .receiptHandle(message.receiptHandle())
                        .build()))
                .onFailure()
                .invoke(throwable -> logger.error("Fail to delete message. ".concat(throwable.getMessage())));
    }

    private Uni<io.vertx.mutiny.core.eventbus.Message<Object>> sendEventBusRequest(SqsVerticleDomain sqsVerticleDomain, Message message) {
        EventMessageDeserializer eventMessageDeserializer = Optional.ofNullable(sqsVerticleDomain.getEventMessageDeserializer())
                .orElse(new DefaultEventMessageDeserializer());

        return eventBus.request(sqsVerticleDomain.getEventBusConsumer(), eventMessageDeserializer.deserialize(message.body()))
                .onFailure()
                .invoke(throwable -> logger.error("Error processing Queue message. ".concat(throwable.getMessage())));
    }

    private static Uni<Message> getFirstMessage(ReceiveMessageResponse receiveMessageResponse) {
        return Uni.createFrom().item(receiveMessageResponse.messages().get(0));
    }

    private Uni<ReceiveMessageResponse> receiveQueueMessage(GetQueueUrlResponse getQueueUrlResponse) {
        return Uni.createFrom().completionStage(sqsAsyncClient
                        .receiveMessage(ReceiveMessageRequest.builder()
                                .queueUrl(getQueueUrlResponse.queueUrl())
                                .build()))
                .onFailure()
                .invoke(throwable -> logger.error(throwable));
    }

    private Uni<GetQueueUrlResponse> getGetQueueUrl(String queueName) {
        return Uni.createFrom().completionStage(sqsAsyncClient.getQueueUrl(GetQueueUrlRequest.builder().
                queueName(queueName)
                .build()));
    }
}
