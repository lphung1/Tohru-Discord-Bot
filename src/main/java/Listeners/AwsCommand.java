package Listeners;

import Services.AwsServices.AwsCloudWatchService;
import Services.AwsServices.AwsCostExplorerService;
import Services.AwsServices.AwsEc2Service;
import Services.AwsServices.AwsLambdaService;
import Services.ExecutorProvider;
import Util.ConfigUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.ArrayList;
import java.util.List;

import static Util.MessageUtil.*;
import static Util.ConfigUtil.*;

public abstract class AwsCommand extends CommandWrapper {

    protected static AwsLambdaService awsLambdaService;
    protected static AwsEc2Service awsEc2Service;
    protected static AwsCostExplorerService costExplorerService;
    protected static AwsCloudWatchService awsCloudWatchService;

    public AwsCommand(DiscordApi api, String command) {
        super(api, command);
        awsLambdaService = AwsLambdaService.getService();
        awsEc2Service = AwsEc2Service.getService();
        costExplorerService = AwsCostExplorerService.getService();
        awsCloudWatchService = AwsCloudWatchService.getService();
    }

    @Override
    public void doAction(MessageCreateEvent messageCreateEvent) {
        User thisUser = messageCreateEvent.getMessage().getUserAuthor().get();
        boolean isTriggeredFromServer = messageCreateEvent.getServer().isPresent();
        List<Role> rolesForInvokedServer;
        Server server;
        Role awsRole;

        if (isTriggeredFromServer) {
            server = messageCreateEvent.getServer().get();
            rolesForInvokedServer = thisUser.getRoles(server);
            String serverId = server.getIdAsString();
            awsRole = (api.getRoleById(getAwsDiscordRole(serverId)).isPresent()) ? api.getRoleById(getAwsDiscordRole(serverId)).get() : null;
        } else {
            rolesForInvokedServer = new ArrayList<>();
            awsRole = null;
        }

        if (rolesForInvokedServer.contains(awsRole) || messageCreateEvent.getMessageAuthor().isBotOwner()) {
            doAwsAction(messageCreateEvent);
            ExecutorProvider.getExecutorService().submit(() -> awsEc2Service.updateBotStatus(api));
        }
        else {
            getSimpleErrorMessage("You do not have permissions to use that command")
                    .send(messageCreateEvent.getChannel());
        }
    }

    public abstract void doAwsAction(MessageCreateEvent messageCreateEvent);
}
