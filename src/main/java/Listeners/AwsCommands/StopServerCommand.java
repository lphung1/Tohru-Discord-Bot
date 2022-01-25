package Listeners.AwsCommands;

import Listeners.AwsCommand;
import Listeners.CommandDescriptions;
import com.amazonaws.services.ec2.model.StopInstancesResult;
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

public class StopServerCommand extends AwsCommand {

    public StopServerCommand(DiscordApi api) {
        super(api, "stopServer");
    }

    public StopServerCommand(DiscordApi api, String command) {
        super(api, command);
        this.description = CommandDescriptions.stop;
    }

    @Override
    public void doAwsAction(MessageCreateEvent messageCreateEvent) {
        TextChannel channel = messageCreateEvent.getChannel();
        CompletableFuture<Message> message = getSimpleEmbedMessage("Request Sent").send(channel);
        if (awsEc2Service.isValidInstanceId(getEC2InstanceId())) {
            StopInstancesResult result = awsEc2Service.stopEc2Instance().join();
            Integer httpRespCode = result.getSdkHttpMetadata().getHttpStatusCode();
            String content = String.format("Request completed with response [%s]", httpRespCode);
            message.join().edit( getSimpleEmbed(content,
                    statusCodeColorMap.getOrDefault(httpRespCode, Color.orange)));
        }
        else {
            message.join().delete();
            getInvalidInstanceMessage().send(channel);
        }
    }
}
