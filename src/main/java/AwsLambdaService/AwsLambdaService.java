package AwsLambdaService;

import Util.ConfigUtil;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;

public final class AwsLambdaService {

    BasicAWSCredentials credentials;

    AWSLambdaClientBuilder awsLambdaClientBuilder;

    AWSLambda client;

    public AwsLambdaService() {
        System.out.println("Aws Service initializing");
        credentials = new BasicAWSCredentials(ConfigUtil.getAwsAccessKey(),ConfigUtil.getAwsSecretKey());
        awsLambdaClientBuilder = AWSLambdaClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(ConfigUtil.getAwsRegion());

        client = awsLambdaClientBuilder.build();
        System.out.println("Initializing done");
    }

    public boolean startEc2() {
        System.out.println("Invoking start lambda function");
        InvokeRequest req = new InvokeRequest()
                .withFunctionName(ConfigUtil.getStartLambdaName());
        InvokeResult response = client.invoke(req);
        System.out.println("Request complete");
        return (response.getStatusCode().equals(200));
    }

    public boolean stopEc2() {
        System.out.println("Invoking stop lambda function");
        InvokeRequest req = new InvokeRequest()
                .withFunctionName(ConfigUtil.getStopLambdaName());
        InvokeResult response = client.invoke(req);
        System.out.println("Request complete");
        return (response.getStatusCode().equals(200));
    }

}
