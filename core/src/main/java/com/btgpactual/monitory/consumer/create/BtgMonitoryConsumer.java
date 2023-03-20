package com.btgpactual.monitory.consumer.create;

import com.btgpactual.monitory.dto.MonitoryRegisterEventDto;
import com.btgpactual.monitory.dto.MonitoryRequesterDto;
import com.btgpactual.monitory.enums.MonitoryStatus;
import com.btgpactual.monitory.ttl.MonitoryStepFunctionTtl;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class BtgMonitoryConsumer {


    public static final String CONSUMER_ALIAS = "monitory";

    @ConfigProperty(name = "monitory.dynamodb.table-name")
    String monitoryDynamoDbTableName;

    @Inject
    DynamoDbAsyncClient dynamoDbAsyncClient;

    @Inject
    MonitoryStepFunctionTtl monitoryStepFunctionTtl;

    private static final Log logger = LogFactory.getLog(BtgMonitoryConsumer.class);

    @ConsumeEvent(CONSUMER_ALIAS)
    public Uni<Void> consume(JsonObject message) {

        return Uni.createFrom().item(message.mapTo(MonitoryRegisterEventDto.class))
                .invoke(monitoryRegisterEventDto -> logger.info("Received ".concat(monitoryRegisterEventDto.toString())))
                .call(this::persistPrincipal)
                .call(this::persistRequesters)
                .call(monitoryRegisterEventDto -> monitoryStepFunctionTtl.generateTtl(monitoryRegisterEventDto)
                        .call(monitoryRequesterDto -> mapToRequesterEntity(monitoryRegisterEventDto, monitoryRequesterDto)
                                .chain(stringAttributeValueMap -> persistEntity(stringAttributeValueMap)))
                        .collect()
                        .last()
                        .replaceWithVoid())
                .replaceWithVoid();
    }

    private Uni<PutItemResponse> persistPrincipal(MonitoryRegisterEventDto monitoryRegisterEventDto) {
        return mapToEntity(monitoryRegisterEventDto)
                .chain(stringAttributeValueMap -> persistEntity(stringAttributeValueMap));
    }

    private Uni<MonitoryRequesterDto> persistRequesters(MonitoryRegisterEventDto monitoryRegisterEventDto) {
        return Multi.createFrom().iterable(monitoryRegisterEventDto.getRequesters())
                .onItem()
                .call(monitoryRequesterDto -> mapToRequesterEntity(monitoryRegisterEventDto, monitoryRequesterDto)
                        .chain(stringAttributeValueMap -> persistEntity(stringAttributeValueMap)))
                .collect()
                .last();
    }

    private Uni<Map<String, AttributeValue>> mapToRequesterEntity(MonitoryRegisterEventDto monitoryRegisterEventDto, MonitoryRequesterDto monitoryRequesterDto) {
        Map<String, AttributeValue> monitoryRequesterEntity = new HashMap<>();

        monitoryRequesterEntity.put("pk", AttributeValue.builder()
                .s(buildPk(monitoryRegisterEventDto))
                .build());
        monitoryRequesterEntity.put("sk", AttributeValue.builder()
                .s(buildSk(monitoryRequesterDto))
                .build());

        Optional.ofNullable(monitoryRequesterDto.getLastExecutionArn()).ifPresent(lasExecutionArn -> {
            monitoryRequesterEntity.put("lastExecutionArn", AttributeValue.builder()
                    .s(monitoryRequesterDto.getLastExecutionArn())
                    .build());
        });

        return Uni.createFrom().item(monitoryRequesterEntity);
    }

    private String buildSk(MonitoryRequesterDto monitoryRequeterDto) {
        return "SOURCESYSTEM"
                .concat("#")
                .concat(monitoryRequeterDto.getSourceSystem());
    }

    private Uni<PutItemResponse> persistEntity(Map<String, AttributeValue> stringAttributeValueMap) {
        return Uni.createFrom().completionStage(dynamoDbAsyncClient.putItem(PutItemRequest.builder()
                        .tableName(monitoryDynamoDbTableName)
                        .item(stringAttributeValueMap)
                        .build()))
                .onFailure().invoke(throwable -> logger.error("Error persisting in dynamodb. ".concat(throwable.getMessage())));
    }

    private Uni<Map<String, AttributeValue>> mapToEntity(MonitoryRegisterEventDto monitoryRegisterEventDto) {
        Map<String, AttributeValue> monitoryEntity = new HashMap<>();

        monitoryEntity.put("pk", AttributeValue.builder()
                .s(buildPk(monitoryRegisterEventDto))
                .build());
        monitoryEntity.put("sk", AttributeValue.builder()
                .s(buildPk(monitoryRegisterEventDto))
                .build());
        monitoryEntity.put("period", AttributeValue.builder()
                .n(monitoryRegisterEventDto.getPeriod().toString())
                .build());
        monitoryEntity.put("status", AttributeValue.builder()
                .s(MonitoryStatus.OK.description)
                .build());
        monitoryEntity.put("lastUpdateDate", AttributeValue.builder()
                .s(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(LocalDateTime.now()))
                .build());

        return Uni.createFrom().item(monitoryEntity);
    }

    private String buildPk(MonitoryRegisterEventDto monitoryRequestDto) {
        return monitoryRequestDto.getTenantCode()
                .concat("#")
                .concat(monitoryRequestDto.getUmbrellaCode())
                .concat("#")
                .concat(monitoryRequestDto.getIdentifierCode());
    }


}
