package Listeners;

import AwsServices.AwsEc2Service;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;

import static Util.ConfigUtil.getEC2InstanceId;
import static Util.MessageUtil.getInvalidInstanceMessage;
import static Util.MessageUtil.getSimpleEmbedMessage;
import static Util.MessageUtil.statusCodeColorMap;

public class RebootCommand extends AwsCommand {

    public RebootCommand(DiscordApi api) {
        super(api, "reboot");
    }

    @Override
    public void doAwsAction(MessageCreateEvent messageCreateEvent) {
        TextChannel channel = messageCreateEvent.getChannel();
        if (awsEc2Service.isValidInstanceId(getEC2InstanceId())) {
            RebootInstancesResult result = awsEc2Service.restartEC2Instance().join();
            Integer httpRespCode = result.getSdkHttpMetadata().getHttpStatusCode();
            getSimpleEmbedMessage(String.format("Request completed with response [%s]", httpRespCode)
                    , statusCodeColorMap.getOrDefault(httpRespCode, Color.orange)).send(channel);
        }
        else {
            getInvalidInstanceMessage().send(channel);
        }
    }
}
