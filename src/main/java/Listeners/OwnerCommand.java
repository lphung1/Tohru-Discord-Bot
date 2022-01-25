package Listeners;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import static Util.MessageUtil.getSimpleErrorMessage;

public abstract class OwnerCommand extends CommandWrapper {

    public OwnerCommand(DiscordApi api, String command) {
        super(api, command);
    }

    @Override
    public void doAction(MessageCreateEvent messageCreateEvent) {
        User thisUser = messageCreateEvent.getMessage().getUserAuthor().get();
        if (thisUser.isBotOwner()) {
            doOwnerCommand(messageCreateEvent);
        }
        else {
            getSimpleErrorMessage("You do not have permissions to use that command")
                    .send(messageCreateEvent.getChannel());
        }
    }

    public abstract void doOwnerCommand(MessageCreateEvent messageCreateEvent);
}
