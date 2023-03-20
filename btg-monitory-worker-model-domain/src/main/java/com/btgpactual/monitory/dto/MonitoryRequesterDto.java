package com.btgpactual.monitory.dto;

public class MonitoryRequesterDto {

    private String sourceSystem;

    private String lastExecutionArn;

    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public void setLastExecutionArn(String lastExecutionArn) {
        this.lastExecutionArn = lastExecutionArn;
    }

    public String getLastExecutionArn() {
        return lastExecutionArn;
    }

    @Override
    public String toString() {
        return "MonitoryRequeterDto{" +
                "sourceSystem='" + sourceSystem + '\'' +
                '}';
    }

}
