package Services.AwsServices;

import Util.ConfigUtil;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;

import static Util.ConfigUtil.getBasicAwsCredentials;
import static Util.ConfigUtil.getEC2InstanceId;

public class AwsCloudWatchService {

    //TODO WIP
    private static final Logger log = LoggerFactory.getLogger(AwsCloudWatchService.class);

    private static AwsCloudWatchService singletonInstance;
    private static AmazonCloudWatch amazonCloudWatch;

    private AwsCloudWatchService(){
        log.info("Initializing aws cost explorer");
        amazonCloudWatch = buildCloudWatchClient();
        log.info("Initialized");
        getMetricStatistics(getMetricStatisticsRequest());
    }

    private AmazonCloudWatch buildCloudWatchClient() {
        return AmazonCloudWatchClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(getBasicAwsCredentials()))
                .withRegion(ConfigUtil.getAwsRegion())
                .build();
    }

    public static synchronized AwsCloudWatchService getService() {
        if (singletonInstance == null) {
            singletonInstance = new AwsCloudWatchService();
        }
        return singletonInstance;
    }

    public GetMetricStatisticsResult getMetricStatistics(GetMetricStatisticsRequest request){
        return amazonCloudWatch.getMetricStatistics(request);
    }

    public GetMetricStatisticsRequest getMetricStatisticsRequest() {
        return getMetricStatisticsRequest(1, getEC2InstanceId());
    }

    public GetMetricStatisticsRequest getMetricStatisticsRequest(int hourRangeFromNow) {
        return getMetricStatisticsRequest(hourRangeFromNow, ConfigUtil.getEC2InstanceId());
    }

    public GetMetricStatisticsRequest getMetricStatisticsRequest(int hourRangeFromNow, String instanceId) {
        Dimension d = new Dimension().withName("InstanceId").withValue(instanceId);
        GetMetricStatisticsRequest statisticsRequest = new GetMetricStatisticsRequest().withMetricName("CPUUtilization")
                .withNamespace("AWS/EC2")
                .withStatistics("Maximum")
                .withStatistics("Average")
                .withStartTime(hoursAgoFromNow(hourRangeFromNow))
                .withEndTime(hoursAgoFromNow(0))
                .withPeriod(300)
                .withDimensions(d);

        return statisticsRequest;
    }

    public static Date hoursAgoFromNow(int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -i);
        Date ago = calendar.getTime();
        return ago;
    }

    public void updateService() {
        amazonCloudWatch = buildCloudWatchClient();
    }
}
