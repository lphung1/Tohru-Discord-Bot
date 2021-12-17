package AwsServices;

import Util.ConfigUtil;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesResult;
import com.amazonaws.services.ec2.model.Reservation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AwsEc2Service {

    static AmazonEC2 ec2;

    private static final Logger log = Logger.getLogger(AwsEc2Service.class.getCanonicalName());

    public AwsEc2Service() {
        ec2 = AmazonEC2ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(ConfigUtil.getBasicAwsCredentials()))
                .withRegion(ConfigUtil.getAwsRegion())
                .build();
    }

    public CompletableFuture<RebootInstancesResult> restartEC2Instance() {
        String instanceId = ConfigUtil.getEC2InstanceId();

        log.info("Restarting ec2 instance with ID: " + instanceId);
        RebootInstancesRequest request = new RebootInstancesRequest().withInstanceIds(instanceId);

        return CompletableFuture.supplyAsync( () -> ec2.rebootInstances(request));
    }

    public Map<String, Instance> getEC2Details() {
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        boolean done = false;
        List<Reservation> reservationList = new ArrayList<>();

        while(!done) {
            DescribeInstancesResult response = ec2.describeInstances(request);
            reservationList.addAll(response.getReservations());

            request.setNextToken(response.getNextToken());
            if(response.getNextToken() == null) {
                done = true;
            }
        }

        Map<String,Instance> listInstance = reservationList.stream()
                .map(Reservation::getInstances)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(Instance::getInstanceId, Function.identity()));

        return listInstance;
    }

}
