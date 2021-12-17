package Listeners;

import Util.ConfigUtil;
import com.amazonaws.services.ec2.model.CpuOptions;
import com.amazonaws.services.ec2.model.Instance;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.List;
import java.util.Map;

public class DetailsCommand extends AwsCommand {
    public DetailsCommand(DiscordApi api) {
        super(api, "details");
    }

    @Override
    public void doAwsAction(MessageCreateEvent messageCreateEvent) {
        messageCreateEvent.getChannel().sendMessage("Getting details");
        Map<String, Instance> instance = awsEc2Service.getEC2Details();
        CpuOptions cpuOptions = instance.get(ConfigUtil.getEC2InstanceId()).getCpuOptions();
        messageCreateEvent.getChannel().sendMessage("InstanceId: " + ConfigUtil.getEC2InstanceId()
                +  " State: " + instance.get(ConfigUtil.getEC2InstanceId()).getState().getName() + "\n"
        + "CPU: " + cpuOptions);
    }
}
