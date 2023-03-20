package com.btgpactual.monitory.dto;

public class RequesterStepFunctionExecutionIdHolder {

    private final MonitoryRegisterEventDto monitoryRegisterEventDto;
    private final MonitoryRequesterDto monitoryRequesterDto;

    private final String stepFunctionExecutionArn;

    public RequesterStepFunctionExecutionIdHolder(MonitoryRegisterEventDto monitoryRegisterEventDto, MonitoryRequesterDto monitoryRequesterDto,
                                                   String stepFunctionExecutionArn){
        this.monitoryRegisterEventDto = monitoryRegisterEventDto;
        this.monitoryRequesterDto = monitoryRequesterDto;
        this.stepFunctionExecutionArn = stepFunctionExecutionArn;
    }

    public MonitoryRegisterEventDto getMonitoryRegisterEventDto() {
        return monitoryRegisterEventDto;
    }

    public MonitoryRequesterDto getMonitoryRequesterDto() {
        return monitoryRequesterDto;
    }

    public String getStepFunctionExecutionArn() {
        return stepFunctionExecutionArn;
    }

    @Override
    public String toString() {
        return "RequesterStepFunctionExecutionIdHolder{" +
                "monitoryRegisterEventDto=" + monitoryRegisterEventDto +
                ", monitoryRequesterDto=" + monitoryRequesterDto +
                ", stepFunctionExecutionArn='" + stepFunctionExecutionArn + '\'' +
                '}';
    }
}
