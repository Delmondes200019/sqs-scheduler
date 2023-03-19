package com.btgpactual.monitory.consumer;

import com.btgpactual.monitory.dto.MonitoryRegisterEventDto;
import com.btgpactual.monitory.dto.MonitoryRequeterDto;
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
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class BtgMonitoryConsumer {


    public static final String CONSUMER_ALIAS = "monitory";

    @ConfigProperty(name = "monitory.dynamodb.table-name")
    String monitoryDynamoDbTableName;

    @Inject
    DynamoDbAsyncClient dynamoDbAsyncClient;

    private static final Log logger = LogFactory.getLog(BtgMonitoryConsumer.class);

//pk    TENANT_ID#UMBRELACODE#IDENTIFIERCODE TENANT_ID#UMBRELACODE#IDENTIFIERCODE
//sk    TENANT_ID#UMBRELACODE#IDENTIFIERCODE SOURCE_SYSTEM#DADOS_CADASTRAIS

    @ConsumeEvent(CONSUMER_ALIAS)
    public Uni<Void> consume(JsonObject message) {

        return Uni.createFrom().item(message.mapTo(MonitoryRegisterEventDto.class))
                .invoke(monitoryRegisterEventDto -> logger.info("Received ".concat(monitoryRegisterEventDto.toString())))
                .call(this::persistPrincipal)
                .call(this::persistRequesters)
                .replaceWithVoid();
    }

    private Uni<PutItemResponse> persistPrincipal(MonitoryRegisterEventDto monitoryRegisterEventDto) {
        return mapToEntity(monitoryRegisterEventDto)
                .chain(stringAttributeValueMap -> persistEntity(stringAttributeValueMap));
    }

    private Uni<MonitoryRequeterDto> persistRequesters(MonitoryRegisterEventDto monitoryRegisterEventDto) {
        return Multi.createFrom().iterable(monitoryRegisterEventDto.getRequesters())
                .onItem()
                .call(monitoryRequesterDto -> mapToRequesterEntity(monitoryRegisterEventDto, monitoryRequesterDto)
                        .chain(stringAttributeValueMap -> persistEntity(stringAttributeValueMap)))
                .collect()
                .last();
    }

    private Uni<Map<String, AttributeValue>> mapToRequesterEntity(MonitoryRegisterEventDto monitoryRegisterEventDto,
                                                                  MonitoryRequeterDto monitoryRequeterDto) {

        Map<String, AttributeValue> monitoryRequesterEntity = new HashMap<>();

        monitoryRequesterEntity.put("pk", AttributeValue.builder()
                .s(buildPk(monitoryRegisterEventDto))
                .build());
        monitoryRequesterEntity.put("sk", AttributeValue.builder()
                .s(buildSk(monitoryRequeterDto))
                .build());

        return Uni.createFrom().item(monitoryRequesterEntity);
    }

    private String buildSk(MonitoryRequeterDto monitoryRequeterDto) {
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
