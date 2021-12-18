package AwsServices;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesResult;
import com.amazonaws.services.ec2.model.Reservation;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.activity.ActivityType;

import java.util.ArrayList;
import java.util.Collection;
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
        ec2 = AmazonEC2ClientBuilder.standard()
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
            api.updateActivity(ActivityType.WATCHING, String.format(" invalid instanceId : [%s]",getEC2InstanceId()));
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
