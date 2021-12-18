package Listeners;

import com.amazonaws.services.ec2.model.StopInstancesResult;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;

import static Util.ConfigUtil.getEC2InstanceId;
import static Util.MessageUtil.getInvalidInstanceMessage;
import static Util.MessageUtil.getSimpleEmbedMessage;
import static Util.MessageUtil.statusCodeColorMap;

public class StopServerCommand extends AwsCommand {

    public StopServerCommand(DiscordApi api) {
        super(api, "stopServer");
    }

    @Override
    public void doAwsAction(MessageCreateEvent messageCreateEvent) {
        TextChannel channel = messageCreateEvent.getChannel();
        if (awsEc2Service.isValidInstanceId(getEC2InstanceId())) {
            StopInstancesResult result = awsEc2Service.stopEc2Instance().join();
            Integer httpRespCode = result.getSdkHttpMetadata().getHttpStatusCode();
            getSimpleEmbedMessage(String.format("Request completed with response [%s]", httpRespCode)
                    , statusCodeColorMap.getOrDefault(httpRespCode, Color.orange)).send(channel);
        }
        else {
            getInvalidInstanceMessage().send(channel);
        }
    }
}
