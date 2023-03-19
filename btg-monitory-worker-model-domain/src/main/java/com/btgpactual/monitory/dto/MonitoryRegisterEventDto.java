package com.btgpactual.monitory.dto;

import java.util.Set;

public class MonitoryRegisterEventDto {

    private String tenantCode;
    private String umbrellaCode;
    private String identifierCode;

    private Set<MonitoryRequeterDto> requesters;

    public void setIdentifierCode(String identifierCode) {
        this.identifierCode = identifierCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public void setUmbrellaCode(String umbrellaCode) {
        this.umbrellaCode = umbrellaCode;
    }

    public String getIdentifierCode() {
        return identifierCode;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public String getUmbrellaCode() {
        return umbrellaCode;
    }

    public void setRequesters(Set<MonitoryRequeterDto> requesters) {
        this.requesters = requesters;
    }

    public Set<MonitoryRequeterDto> getRequesters() {
        return requesters;
    }

    @Override
    public String toString() {
        return "MonitoryRegisterEventDto{" +
                "tenantCode='" + tenantCode + '\'' +
                ", umbrellaCode='" + umbrellaCode + '\'' +
                ", identifierCode='" + identifierCode + '\'' +
                ", requesters=" + requesters +
                '}';
    }
}
