package Listeners;

import AwsServices.AwsEc2Service;
import AwsServices.AwsLambdaService;
import Util.ConfigUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.List;

public abstract class AwsCommand extends CommandWrapper {

    static AwsLambdaService awsLambdaService;
    static AwsEc2Service awsEc2Service;
    static Role awsRole;
    static {
        awsLambdaService = new AwsLambdaService();
        awsEc2Service = new AwsEc2Service();
    }

    AwsCommand(DiscordApi api, String command) {
        super(api, command);
        api.getRoleById(ConfigUtil.getAwsDiscordRole()).ifPresent(role -> awsRole = role);
    }

    @Override
    public void doAction(MessageCreateEvent messageCreateEvent) {
        User thisUser = messageCreateEvent.getMessage().getUserAuthor().get();
        List<Role> userRoles = thisUser.getRoles(messageCreateEvent.getServer().get());
        if (awsRole == null || userRoles.contains(awsRole)) {
            doAwsAction(messageCreateEvent);
        }
        else {
            messageCreateEvent.getChannel().sendMessage("You do not have permissions to execute that command.");
        }
    }

    public abstract void doAwsAction(MessageCreateEvent messageCreateEvent);
}
