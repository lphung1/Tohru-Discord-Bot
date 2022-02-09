package Services.AwsServices;

import Util.ConfigUtil;
import Util.MessageUtil;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.cloudwatch.model.GetMetricWidgetImageRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricWidgetImageResult;
import org.json.JSONArray;
import org.json.JSONObject;
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

    private GetMetricStatisticsRequest getMetricStatisticsRequest() {
        return getMetricStatisticsRequest(1, getEC2InstanceId());
    }

    public GetMetricWidgetImageResult getMetricStatisticsResult(int hoursAgo) {
        return amazonCloudWatch.getMetricWidgetImage(getMetricWidgetImageRequest(hoursAgo));
    }

    public GetMetricWidgetImageRequest getMetricWidgetImageRequest(int hoursAgo) {

        String hoursAgoStr = (hoursAgo < 0) ? "-PT1H" : String.format("-PT%dH", hoursAgo);
        JSONObject root = new JSONObject();
        root.put("width", 800);
        root.put("height", 395);

        JSONObject averageStat = new JSONObject().put("stat", "Average");
        JSONObject maxStat = new JSONObject().put("stat", "Maximum");
        JSONArray metrics = new JSONArray();
        JSONArray metric = new JSONArray();

        metric.put("AWS/EC2");
        metric.put("CPUUtilization");
        metric.put("InstanceId");
        metric.put(ConfigUtil.getEC2InstanceId());
        metric.put(averageStat);

        metrics.put(metric);

        root.put("metrics", metrics);

        root.put("period", 60);
        root.put("start", hoursAgoStr);
        root.put("end", "-PT0H");
        root.put("title", String.format("Average cpu usage for instance %s", ConfigUtil.getEC2InstanceId() ));
        root.put("view", "timeSeries");

        return new GetMetricWidgetImageRequest().withMetricWidget(root.toString());
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
