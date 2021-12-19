package AwsServices;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.AwsRegionProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstanceTypesRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceTypesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceTypeInfo;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesResult;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.activity.ActivityType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static Util.ConfigUtil.*;

public class AwsEc2Service {

    private static AwsEc2Service singletonInstance;

    static AmazonEC2 ec2;

    private static final Logger log = Logger.getLogger(AwsEc2Service.class.getCanonicalName());

    private AwsEc2Service() {
        log.info("Ec2 Service initializing");
        ec2 = buildEc2Client();
        log.info("Initializing Complete");
    }

    public void updateService() {
        ec2 = buildEc2Client();
    }

    private AmazonEC2 buildEc2Client() {
        return AmazonEC2ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(getBasicAwsCredentials()))
                .withRegion(getAwsRegion())
                .build();
    }

    public CompletableFuture<RebootInstancesResult> restartEC2Instance() {
        String instanceId = getEC2InstanceId();

        log.info("Restarting ec2 instance with ID: " + instanceId);
        RebootInstancesRequest request = new RebootInstancesRequest().withInstanceIds(instanceId);

        return CompletableFuture.supplyAsync( () -> ec2.rebootInstances(request));
    }

    public CompletableFuture<StartInstancesResult> startEc2Instance() {
        String instanceId = getEC2InstanceId();
        log.info("Starting ec2 instance with ID: " + instanceId);
        StartInstancesRequest request = new StartInstancesRequest()
                .withInstanceIds(getEC2InstanceId());
        return CompletableFuture.supplyAsync(() -> ec2.startInstances(request));
    }

    public CompletableFuture<StopInstancesResult> stopEc2Instance() {
        String instanceId = getEC2InstanceId();
        log.info("Stopping ec2 instance with ID: " + instanceId);
        StopInstancesRequest request = new StopInstancesRequest()
                .withInstanceIds(getEC2InstanceId());
        return CompletableFuture.supplyAsync(() -> ec2.stopInstances(request));
    }

    public Map<String, InstanceTypeInfo> getInstanceTypeInfo(List<String> instanceType) {
        DescribeInstanceTypesRequest request = new DescribeInstanceTypesRequest().withInstanceTypes(instanceType);
        Map<String, InstanceTypeInfo> instanceTypeInfoMap = new HashMap<>();
        log.info("Getting instance type details");
        boolean done = false;
        while(!done) {
            DescribeInstanceTypesResult response = ec2.describeInstanceTypes(request);
            response.getInstanceTypes()
                    .stream()
                    .forEach(instanceTypeInfo -> {
                instanceTypeInfoMap.putIfAbsent(instanceTypeInfo.getInstanceType(),instanceTypeInfo);
            });
            request.setNextToken(response.getNextToken());
            if(response.getNextToken() == null) {
                done = true;
            }
        }

        return instanceTypeInfoMap;
    }

    public Map<String, Instance> getEC2DetailsMap() {
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

    public void updateBotStatus(DiscordApi api) {
        log.info("Updating bot status");
        Instance trackedInstance = getEC2DetailsMap().get(getEC2InstanceId());
        if (trackedInstance != null) {
            String ec2StatusActivity = String.format("Server Status: %s", trackedInstance.getState().getName().intern());
            api.updateActivity(ActivityType.WATCHING, ec2StatusActivity);
        }
        else {
            api.updateActivity(ActivityType.WATCHING, String.format("No valid instanceIds : [%s] for region %s",getEC2InstanceId(), getAwsRegion()));
        }
    }

    public boolean isValidInstanceId(String instanceId) {
        return getEC2DetailsMap().containsKey(instanceId);
    }

    public static synchronized AwsEc2Service getService() {
        if (singletonInstance == null) {
            singletonInstance = new AwsEc2Service();
        }
        return singletonInstance;
    }

}
