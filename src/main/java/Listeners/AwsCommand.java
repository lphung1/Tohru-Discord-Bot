package Listeners;

import AwsServices.AwsEc2Service;
import AwsServices.AwsLambdaService;
import Util.ConfigUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageDecoration;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.List;

import static Util.MessageUtil.*;

public abstract class AwsCommand extends CommandWrapper {

    AwsLambdaService awsLambdaService;
    AwsEc2Service awsEc2Service;
    Role awsRole;

    AwsCommand(DiscordApi api, String command) {
        super(api, command);
        awsLambdaService = AwsLambdaService.getService();
        awsEc2Service = AwsEc2Service.getService();
        api.getRoleById(ConfigUtil.getAwsDiscordRole()).ifPresent(role -> awsRole = role);
    }

    @Override
    public void doAction(MessageCreateEvent messageCreateEvent) {
        User thisUser = messageCreateEvent.getMessage().getUserAuthor().get();
        List<Role> userRoles = thisUser.getRoles(messageCreateEvent.getServer().get());
        if (userRoles.contains(awsRole) || messageCreateEvent.getMessageAuthor().isBotOwner()) {
            doAwsAction(messageCreateEvent);
            awsEc2Service.updateBotStatus(api);
        }
        else {
            getSimpleEmbedMessage("You do not have permissions to use that command", Color.RED)
                    .send(messageCreateEvent.getChannel());
        }
    }

    public abstract void doAwsAction(MessageCreateEvent messageCreateEvent);

}
