package Services.AwsServices;

import Util.ConfigUtil;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AwsLambdaService {

    private static final Logger log = LoggerFactory.getLogger(AwsLambdaService.class);

    static AWSLambda client;

    private static AwsLambdaService service;

    private AwsLambdaService() {
        this.updateService();
    }

    public void updateService() {
        log.info("Aws Lambda Service initializing");
        client = AWSLambdaClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(ConfigUtil.getBasicAwsCredentials()))
                .withRegion(ConfigUtil.getAwsRegion()).build();
        log.info("Initializing done");
    }

    public boolean doLambdaFunction(int num) {
        log.info("Invoking lambda function");
        InvokeRequest req = new InvokeRequest()
                .withFunctionName(ConfigUtil.getLambdaFunction(num));
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
