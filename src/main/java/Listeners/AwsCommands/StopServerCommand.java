package Listeners.AwsCommands;

import Listeners.CommandDescriptions;
import Listeners.CommandWrapper;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

import static Util.MessageUtil.getSimpleEmbedMessage;
import static Util.MessageUtil.statusCodeColorMap;

public class StopServerCommand extends InstanceStateManager {

    public StopServerCommand(DiscordApi api, String command) {
        super(api, command);
        this.description = CommandDescriptions.stop;
    }

    @Override
    public CompletableFuture<Message> changeInstanceState(MessageCreateEvent messageCreateEvent, String instanceId) {
        StopInstancesResult result = awsEc2Service.stopEc2Instance(instanceId).join();
        Integer httpRespCode = result.getSdkHttpMetadata().getHttpStatusCode();
        return getSimpleEmbedMessage(String.format("%s request completed with response [%s]", command, httpRespCode)
                , statusCodeColorMap.getOrDefault(httpRespCode, Color.orange)).send(messageCreateEvent.getChannel());
    }
}
