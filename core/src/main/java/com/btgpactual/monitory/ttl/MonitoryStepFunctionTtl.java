package com.btgpactual.monitory.ttl;

import com.btgpactual.monitory.dto.MonitoryRegisterEventDto;
import com.btgpactual.monitory.dto.MonitoryRequesterDto;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.sfn.SfnAsyncClient;
import software.amazon.awssdk.services.sfn.model.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MonitoryStepFunctionTtl {

    @Inject
    SfnAsyncClient sfnAsyncClient;

    @ConfigProperty(name = "monitory.ttl.stepfunction-arn")
    String monitoryTtlStepFunctionArn;

    @ConfigProperty(name = "monitory.ttl.stepfunction-execution-arn-template")
    String monitoryStepFunctionExecutionArnTemplate;

    private static final Log logger = LogFactory.getLog(MonitoryStepFunctionTtl.class);

    public Multi<MonitoryRequesterDto> generateTtl(MonitoryRegisterEventDto monitoryRegisterEventDto) {

        return Multi.createFrom().iterable(monitoryRegisterEventDto.getRequesters())
                .onItem()
                .transformToUni(monitoryRequesterDto -> startStepFunctionExecution(monitoryRegisterEventDto, monitoryRequesterDto)
                        .map(startExecutionResponse -> {
                            monitoryRequesterDto.setLastExecutionArn(startExecutionResponse.executionArn());
                            return monitoryRequesterDto;
                        }))
                .concatenate();
    }

    private Uni<Void> stopStepFunctionExecution(MonitoryRegisterEventDto monitoryRegisterEventDto, MonitoryRequesterDto monitoryRequesterDto) {
        return Uni.createFrom().completionStage(sfnAsyncClient.stopExecution(StopExecutionRequest.builder()
                        .executionArn(resolveStepFunctionExecutionArn(monitoryRegisterEventDto, monitoryRequesterDto))
                        .cause("Update event received")
                        .build()))
                .onFailure(ExecutionDoesNotExistException.class)
                .recoverWithNull()
                .onFailure()
                .invoke(throwable -> {
                    throwable.printStackTrace();
                    logger.error("Error stopping ttl execution ".concat(throwable.getMessage()).concat(" ").
                            concat(monitoryRegisterEventDto.toString()));
                })
                .replaceWithVoid();
    }

    private String resolveStepFunctionExecutionArn(MonitoryRegisterEventDto monitoryRegisterEventDto, MonitoryRequesterDto monitoryRequesterDto) {
        return monitoryStepFunctionExecutionArnTemplate.replace("EXECUTION_NAME",
                buildStepFunctionExecutionName(monitoryRegisterEventDto, monitoryRequesterDto));
    }

    private Uni<StartExecutionResponse> startStepFunctionExecution(MonitoryRegisterEventDto monitoryRegisterEventDto, MonitoryRequesterDto monitoryRequesterDto) {
        return Uni.createFrom().completionStage(sfnAsyncClient.startExecution(StartExecutionRequest
                        .builder()
                        .stateMachineArn(monitoryTtlStepFunctionArn)
                        .input(JsonObject.mapFrom(monitoryRegisterEventDto).toString())
                        .name(buildStepFunctionExecutionName(monitoryRegisterEventDto, monitoryRequesterDto))
                        .build()))
                .invoke(startExecutionResponse -> logger.info("Execution ARN ".concat(startExecutionResponse.executionArn())))
                .onFailure(ExecutionAlreadyExistsException.class)
                .recoverWithNull()
                .onFailure()
                .invoke(throwable -> logger.error("Error generating ttl execution for ".concat(monitoryRegisterEventDto.toString())));
    }

    private String buildStepFunctionExecutionName(MonitoryRegisterEventDto monitoryRegisterEventDto, MonitoryRequesterDto monitoryRequeterDtos) {
//        "1#DADOS_CADASTRAIS#OPIN_CUSTOMER_REGISTERDATA_LESSWORKER
        return monitoryRegisterEventDto.getTenantCode()
                .concat("_")
                .concat(monitoryRegisterEventDto.getUmbrellaCode())
                .concat("_")
                .concat(monitoryRequeterDtos.getSourceSystem());
    }
}
