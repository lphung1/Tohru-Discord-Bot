package Services.models;

import com.amazonaws.services.cloudwatch.model.Metric;

import java.io.Serializable;
import java.util.ArrayList;

public class MetricWidget implements Serializable {

    int width;
    int height;

    String metricsss;
    /**
     *       "metrics":[
     *          [
     *             "AWS/EC2",
     *             "CPUUtilization",
     *             "InstanceId",
     *             "i-1234567890abcdef0",
     *             {
     *                "stat":"Average"
     *             }
     *          ],
     *          [
     *             "AWS/EC2",
     *             "CPUUtilization",
     *             "InstanceId",
     *             "i-0987654321abcdef0",
     *             {
     *                "stat":"Average"
     *             }
     *          ]
     *       ],
     */
    ArrayList<ArrayList<String>> metrics = new ArrayList();

}
