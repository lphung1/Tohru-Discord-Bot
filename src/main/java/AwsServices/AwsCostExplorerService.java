package AwsServices;

import Util.ConfigUtil;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.costexplorer.AWSCostExplorer;
import com.amazonaws.services.costexplorer.AWSCostExplorerClientBuilder;
import com.amazonaws.services.costexplorer.model.DateInterval;
import com.amazonaws.services.costexplorer.model.Expression;
import com.amazonaws.services.costexplorer.model.GetCostAndUsageRequest;
import com.amazonaws.services.costexplorer.model.GetCostAndUsageResult;
import com.amazonaws.services.costexplorer.model.Group;
import com.amazonaws.services.costexplorer.model.GroupDefinition;
import com.amazonaws.services.costexplorer.model.GroupDefinitionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

import static Util.ConfigUtil.getBasicAwsCredentials;

public class AwsCostExplorerService {

    private static final Logger log = LoggerFactory.getLogger(AwsCostExplorerService.class);

    private static AwsCostExplorerService singletonInstance;
    private AWSCostExplorer awsCostExplorer;

    private AwsCostExplorerService(){
        log.info("Initializing aws cost explorer");
        awsCostExplorer = buildAwsCostExplorerClient();
        log.info("Initialized");
    }

    private AWSCostExplorer buildAwsCostExplorerClient() {
        return AWSCostExplorerClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(getBasicAwsCredentials()))
                .withRegion(ConfigUtil.getAwsRegion())
                .build();
    }

    private GetCostAndUsageRequest getCostAndUsageRequest() {
        LocalDate todaydate = LocalDate.now();

        return new GetCostAndUsageRequest()
                .withGranularity("MONTHLY")
                .withTimePeriod(new DateInterval().withStart(todaydate.withDayOfMonth(1).toString()).withEnd(todaydate.toString()))
                .withMetrics("UnblendedCost");
    }

    public static synchronized AwsCostExplorerService getService() {
        if (singletonInstance == null) {
            singletonInstance = new AwsCostExplorerService();
        }
        return singletonInstance;
    }

    public GetCostAndUsageResult getMonthToDateCostUsageResult() {
        return awsCostExplorer.getCostAndUsage(getCostAndUsageRequest());
    }

    public GetCostAndUsageResult getMonthToDateCostGranular() {
        return awsCostExplorer.getCostAndUsage(getCostAndUsageRequest().withGroupBy(new GroupDefinition()
                .withType(GroupDefinitionType.DIMENSION)
                .withKey("SERVICE")));
    }

    public void updateService() {
        awsCostExplorer = buildAwsCostExplorerClient();
    }


}
