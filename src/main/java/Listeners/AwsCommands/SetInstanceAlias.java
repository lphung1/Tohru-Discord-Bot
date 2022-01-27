package Listeners.AwsCommands;

import Listeners.AwsCommand;
import Listeners.CommandDescriptions;
import Util.ConfigUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.message.MessageCreateEvent;

import static Util.MessageUtil.getSimpleEmbedMessage;
import static Util.MessageUtil.getSimpleErrorMessage;

public class SetInstanceAlias extends AwsCommand {

    public SetInstanceAlias(DiscordApi api, String command) {
        super(api, command);
        this.description = CommandDescriptions.alias;
    }

    @Override
    public void doAwsAction(MessageCreateEvent messageCreateEvent) {
        TextChannel channel = messageCreateEvent.getChannel();
        if (hasArgument(2)) {
            setAlias(getSecondLastArgument(), getLastArgument(), channel);
        }
        else if (hasArgument()) {
            setAlias(ConfigUtil.getEC2InstanceId(), getLastArgument(), channel);
        }
        else {
            getSimpleErrorMessage("Please provide at least 1 arguments <nickname> or <instanceId> <nickname>").send(channel);
        }
    }

    public void setAlias(String id, String nickname, TextChannel channel) {
        if (ConfigUtil.setAwsAlias(id, nickname)) {
            getSimpleEmbedMessage(String.format("Updated nickname [%s] for instance %s", nickname, id))
                    .send(channel);
        } else {
            getSimpleErrorMessage("Issue giving nickname")
                    .send(channel);
        }
    }
}
