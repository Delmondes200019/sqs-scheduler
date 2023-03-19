package com.btgpactual.monitory.dto;

public class MonitoryRequeterDto {

    private String sourceSystem;

    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    @Override
    public String toString() {
        return "MonitoryRequeterDto{" +
                "sourceSystem='" + sourceSystem + '\'' +
                '}';
    }
}
