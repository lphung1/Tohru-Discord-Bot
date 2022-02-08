package Listeners.AwsCommands;

import Listeners.CommandDescriptions;
import com.amazonaws.services.ec2.model.RebootInstancesResult;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import static Util.MessageUtil.getSimpleEmbedMessage;
import static Util.MessageUtil.statusCodeColorMap;

public class RebootCommand extends InstanceStateManager {

    public RebootCommand(DiscordApi api, String command) {
        super(api, command);
        this.description = CommandDescriptions.reboot;
    }

    @Override
    public void changeInstanceState(MessageCreateEvent messageCreateEvent, String instanceId) {
        RebootInstancesResult result = awsEc2Service.restartEC2Instance(instanceId).join();
        Integer httpRespCode = result.getSdkHttpMetadata().getHttpStatusCode();
        String content = String.format("%s request completed with response [%s]", command, httpRespCode);
        getSimpleEmbedMessage(content,
                statusCodeColorMap.getOrDefault(httpRespCode, Color.orange)).send(messageCreateEvent.getChannel());
    }
}
