package com.btgpactual.monitory.producer;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sfn.SfnAsyncClient;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.net.URI;

@ApplicationScoped
public class SfnAsyncClientProducer {

    @ConfigProperty(name = "aws.stepfunction-region")
    String stepFunctionRegion;

    @ConfigProperty(name = "aws.stepfunction.endpoint-override")
    String stepFunctionEndpointOverride;

    @Produces
    public SfnAsyncClient sfnAsyncClient(){

        return SfnAsyncClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.of(stepFunctionRegion))
                .endpointOverride(URI.create(stepFunctionEndpointOverride))
                .build();
    }
}
