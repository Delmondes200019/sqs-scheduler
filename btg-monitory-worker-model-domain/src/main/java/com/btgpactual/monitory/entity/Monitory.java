package com.btgpactual.monitory.entity;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class Monitory {

    private String pk;
    private String sk;

    public void setPk(String pk) {
        this.pk = pk;
    }

    public void setSk(String sk) {
        this.sk = sk;
    }


    @DynamoDbSortKey
    public String getSk() {
        return sk;
    }

    @DynamoDbPartitionKey
    public String getPk() {
        return pk;
    }
}
