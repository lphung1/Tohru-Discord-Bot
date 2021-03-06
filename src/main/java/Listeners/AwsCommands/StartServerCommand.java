package Listeners.AwsCommands;

import Listeners.CommandDescriptions;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

import static Util.MessageUtil.getSimpleEmbedMessage;
import static Util.MessageUtil.statusCodeColorMap;

public class StartServerCommand extends InstanceStateManager {

    public StartServerCommand(DiscordApi api, String command) {
        super(api, command);
        this.description = CommandDescriptions.start;
    }

    @Override
    public CompletableFuture<Message> changeInstanceState(MessageCreateEvent messageCreateEvent, String instanceId) {
        StartInstancesResult result = awsEc2Service.startEc2Instance(instanceId).join();
        Integer httpRespCode = result.getSdkHttpMetadata().getHttpStatusCode();
        return getSimpleEmbedMessage(String.format("%s request completed with response [%s]", command, httpRespCode)
                , statusCodeColorMap.getOrDefault(httpRespCode, Color.orange)).send(messageCreateEvent.getChannel());
    }
}
