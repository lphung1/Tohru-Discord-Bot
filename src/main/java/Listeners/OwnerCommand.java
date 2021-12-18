package Listeners;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;

import static Util.MessageUtil.getSimpleEmbedMessage;

public abstract class OwnerCommand extends CommandWrapper {

    OwnerCommand(DiscordApi api, String command) {
        super(api, command);
    }

    @Override
    public void doAction(MessageCreateEvent messageCreateEvent) {
        User thisUser = messageCreateEvent.getMessage().getUserAuthor().get();
        if (thisUser.isBotOwner()) {
            doOwnerCommand(messageCreateEvent);
        }
        else {
            getSimpleEmbedMessage("You do not have permissions to use that command", Color.RED)
                    .send(messageCreateEvent.getChannel());
        }
    }

    public abstract void doOwnerCommand(MessageCreateEvent messageCreateEvent);
}
