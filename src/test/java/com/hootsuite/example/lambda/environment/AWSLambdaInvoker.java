package com.hootsuite.example.lambda.environment;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.google.gson.Gson;
import com.hootsuite.example.lambda.SampleLambdaRequest;

import java.nio.charset.StandardCharsets;

class AWSLambdaInvoker implements LambdaGenerator {

    private LambdaType lambdaType;
    private AWSLambda awsLambda;
    private InvokeRequest invokeRequest = new InvokeRequest();

    private static final String STAGING_FUNCTION = "sampleLambda_staging";
    private static final String PRODUCTION_FUNCTION = "sampleLambda_production";

    private static final String ALIAS = "SAMPLE_ALIAS";

    AWSLambdaInvoker(LambdaType lambdaType) {
        this.lambdaType = lambdaType;

        awsLambda = AWSLambdaClientBuilder
                .standard()
                .withRegion(Regions.US_WEST_2)
                .withCredentials(new AWSLambdaCredentialsProvider())
                .build();

        invokeRequest
                // TODO Uncomment to invoke Lambda with Alias
//                .withQualifier(ALIAS)
                .withFunctionName(getFunctionName());
    }

    private String getFunctionName() {
        if (lambdaType == LambdaType.PRODUCTION) {
            return PRODUCTION_FUNCTION;
        } else {
            return STAGING_FUNCTION;
        }
    }

    @Override
    public String invoke(SampleLambdaRequest input) {
        final Gson gson = new Gson();
        byte[] bytes = awsLambda.invoke(invokeRequest.withPayload(gson.toJson(input))).getPayload().array();
        return new String(bytes, StandardCharsets.UTF_8).replace("\"", "");
    }

    enum LambdaType {
        STAGING,
        PRODUCTION
    }
}
