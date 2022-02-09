package Listeners.OwnerCommands;

import Listeners.CommandDescriptions;
import Listeners.OwnerCommand;
import Util.MessageUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;

import static Util.ConfigUtil.*;

public class SetDebug extends OwnerCommand {

    public SetDebug(DiscordApi api, String command) {
        super(api, command);
        description = CommandDescriptions.debug;
    }

    @Override
    public void doOwnerCommand(MessageCreateEvent messageCreateEvent) {
        if (setDebug()) {
            MessageUtil.getSimpleEmbedMessage("Debug mode set to " + isDebug()).send(messageCreateEvent.getChannel());
        } else {
            MessageUtil.getSimpleErrorMessage("Problem trying to set debug").send(messageCreateEvent.getChannel());
        }

    }
}
