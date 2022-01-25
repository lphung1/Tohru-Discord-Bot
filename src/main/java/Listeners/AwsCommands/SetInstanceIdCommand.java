package Listeners.AwsCommands;

import Listeners.AwsCommand;
import Listeners.CommandDescriptions;
import Util.ConfigUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.logging.Logger;

import static Util.MessageUtil.*;

public class SetInstanceIdCommand extends AwsCommand {

    static Logger log = Logger.getLogger(SetInstanceIdCommand.class.getName());

    public SetInstanceIdCommand(DiscordApi api) {
        super(api, "setInstanceId");
    }

    public SetInstanceIdCommand(DiscordApi api, String command) {
        super(api, command);
        this.description = CommandDescriptions.track;
    }

    @Override
    public void doAwsAction(MessageCreateEvent messageCreateEvent) {
        TextChannel channel = messageCreateEvent.getChannel();
        if (hasArgument()) {
            if (ConfigUtil.setEc2InstanceId(getLastArgument())) {
                getSimpleEmbedMessage(String.format("Now tracking instanceId : %s", getLastArgument()))
                        .send(channel);
            } else {
                getSimpleErrorMessage("Issue with setting instance Id.")
                        .send(channel);
            }
        }
        else {
            getSimpleErrorMessage("No arguments found. Please add arguments separated with a space").send(channel);
        }
    }
}
