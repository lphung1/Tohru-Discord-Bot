package Listeners.AwsCommands;

import Listeners.AwsCommand;
import Listeners.CommandDescriptions;
import com.amazonaws.services.ec2.model.Instance;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;

import static Util.ConfigUtil.*;
import static Util.MessageUtil.*;

public class InstanceIpCommand extends AwsCommand {

    public InstanceIpCommand(DiscordApi api) {
        super(api, "serverIp");
    }

    public InstanceIpCommand(DiscordApi api, String command) {
        super(api, command);
        this.description = CommandDescriptions.serverIp;
    }

    @Override
    public void doAwsAction(MessageCreateEvent messageCreateEvent) {
        if (awsEc2Service.isValidInstanceId(getEC2InstanceId())) {
            Instance inst = awsEc2Service.getEC2DetailsMap().get(getEC2InstanceId());
            User user = messageCreateEvent.getMessageAuthor().asUser().get();
            messageCreateEvent.getMessage().addReaction("✅");
            new MessageBuilder()
                    .addEmbed(new EmbedBuilder().setTitle("Shhh, please keep this between us")
                            .setDescription("Server IP details for " + getEC2InstanceId())
                            .addField("Public Ip Address", getOrDefault(inst.getPublicIpAddress(), "-"))
                            .addField("Ipv6Address", "" + getOrDefault(inst.getIpv6Address(), "-"))
                            .addField("Public DNS", "" + getOrDefault(inst.getPublicDnsName(), "-"))
                            .setColor(new Color(48, 97, 219))).send(user);
        } else {
            getInvalidInstanceMessage().send(messageCreateEvent.getChannel());
        }

    }
}
