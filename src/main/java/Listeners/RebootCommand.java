package Listeners;

import AwsServices.AwsEc2Service;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesResult;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class RebootCommand extends AwsCommand {

    public RebootCommand(DiscordApi api) {
        super(api, "reboot");
    }

    @Override
    public void doAwsAction(MessageCreateEvent messageCreateEvent) {
        messageCreateEvent.getChannel().sendMessage("Restarting instance");
        try {
            RebootInstancesResult result = awsEc2Service.restartEC2Instance().get();
            messageCreateEvent.getChannel().sendMessage("Completed with response code :" + result.getSdkHttpMetadata().getHttpStatusCode());
        } catch (Exception e) {
            messageCreateEvent.getChannel().sendMessage("Issue with restarting service :" + e.getMessage());
        }
    }
}
