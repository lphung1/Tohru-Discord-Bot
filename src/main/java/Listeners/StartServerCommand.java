package Listeners;

import com.amazonaws.services.ec2.model.StartInstancesResult;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;

import static Util.ConfigUtil.getEC2InstanceId;
import static Util.MessageUtil.*;

public class StartServerCommand extends AwsCommand {

    public StartServerCommand(DiscordApi api) {
        super(api, "startServer");
    }

    @Override
    public void doAwsAction(MessageCreateEvent messageCreateEvent) {
        TextChannel channel = messageCreateEvent.getChannel();
        if (awsEc2Service.isValidInstanceId(getEC2InstanceId())) {
            StartInstancesResult result = awsEc2Service.startEc2Instance().join();
            Integer httpRespCode = result.getSdkHttpMetadata().getHttpStatusCode();
            getSimpleEmbedMessage(String.format("Request completed with response [%s]", httpRespCode)
            , statusCodeColorMap.getOrDefault(httpRespCode, Color.orange)).send(channel);
        }
        else {
            getInvalidInstanceMessage().send(channel);
        }
    }

}
