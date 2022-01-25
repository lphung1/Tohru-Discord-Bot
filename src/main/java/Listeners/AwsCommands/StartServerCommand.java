package Listeners.AwsCommands;

import Listeners.AwsCommand;
import Listeners.CommandDescriptions;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static Util.ConfigUtil.getEC2InstanceId;
import static Util.MessageUtil.*;

public class StartServerCommand extends AwsCommand {

    public StartServerCommand(DiscordApi api, String command) {
        super(api, command);
        this.description = CommandDescriptions.start;
    }

    @Override
    public void doAwsAction(MessageCreateEvent messageCreateEvent) {
        TextChannel channel = messageCreateEvent.getChannel();
        CompletableFuture<Message> message = getSimpleEmbedMessage("Request Sent").send(channel);
        if (awsEc2Service.isValidInstanceId(getEC2InstanceId())) {
            StartInstancesResult result = awsEc2Service.startEc2Instance().join();
            Integer httpRespCode = result.getSdkHttpMetadata().getHttpStatusCode();
            message.join().edit(
            getSimpleEmbed(String.format("Request completed with response [%s]", httpRespCode)
            , statusCodeColorMap.getOrDefault(httpRespCode, Color.orange)));
        }
        else {
            message.join().delete();
            getInvalidInstanceMessage().send(channel);
        }
    }

}
