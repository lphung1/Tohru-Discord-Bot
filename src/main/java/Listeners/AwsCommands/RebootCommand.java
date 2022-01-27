package Listeners.AwsCommands;

import Listeners.AwsCommand;
import Listeners.CommandDescriptions;
import com.amazonaws.services.ec2.model.RebootInstancesResult;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

import static Util.ConfigUtil.getEC2InstanceId;
import static Util.MessageUtil.getInvalidInstanceMessage;
import static Util.MessageUtil.getSimpleEmbed;
import static Util.MessageUtil.getSimpleEmbedMessage;
import static Util.MessageUtil.statusCodeColorMap;

public class RebootCommand extends AwsCommand {

    public RebootCommand(DiscordApi api) {
        super(api, "reboot");
    }

    public RebootCommand(DiscordApi api, String command) {
        super(api, command);
        this.description = CommandDescriptions.reboot;
    }

    @Override
    public void doAwsAction(MessageCreateEvent messageCreateEvent) {
        TextChannel channel = messageCreateEvent.getChannel();
        if (awsEc2Service.isValidInstanceId(getEC2InstanceId())) {
            RebootInstancesResult result = awsEc2Service.restartEC2Instance().join();
            Integer httpRespCode = result.getSdkHttpMetadata().getHttpStatusCode();
            String content = String.format("Request completed with response [%s]", httpRespCode);
            getSimpleEmbedMessage(content, statusCodeColorMap.getOrDefault(httpRespCode, Color.orange)).send(channel);
        }
        else {
            getInvalidInstanceMessage().send(channel);
        }
    }
}
