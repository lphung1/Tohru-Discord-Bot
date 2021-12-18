package AwsServices;

import Util.ConfigUtil;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;

import java.util.logging.Logger;

public final class AwsLambdaService {

    static Logger log = Logger.getLogger(AwsLambdaService.class.getName());
    AWSLambdaClientBuilder awsLambdaClientBuilder;

    AWSLambda client;

    private static AwsLambdaService service;

    private AwsLambdaService() {
        log.info("Aws Lambda Service initializing");
        awsLambdaClientBuilder = AWSLambdaClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(ConfigUtil.getBasicAwsCredentials()))
                .withRegion(ConfigUtil.getAwsRegion());

        client = awsLambdaClientBuilder.build();
        log.info("Initializing done");
    }

    public boolean startEc2() {
        log.info("Invoking start lambda function");
        InvokeRequest req = new InvokeRequest()
                .withFunctionName(ConfigUtil.getStartLambdaName());
        InvokeResult response = client.invoke(req);
        log.info("Request complete");
        return (response.getStatusCode().equals(200));
    }

    public boolean stopEc2() {
        log.info("Invoking stop lambda function");
        InvokeRequest req = new InvokeRequest()
                .withFunctionName(ConfigUtil.getStopLambdaName());
        InvokeResult response = client.invoke(req);
        log.info("Request complete");
        return (response.getStatusCode().equals(200));
    }

    public static synchronized AwsLambdaService getService() {
        if (service == null) {
            service = new AwsLambdaService();
        }

        return service;
    }

}
