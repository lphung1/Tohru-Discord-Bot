package Listeners.OwnerCommands;

import Listeners.CommandDescriptions;
import Listeners.OwnerCommand;
import Util.ConfigUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;

import static Util.MessageUtil.*;

public class SetAwsRoleId extends OwnerCommand {

    public SetAwsRoleId(DiscordApi api) {
        super(api, "setAwsRole");
    }

    public SetAwsRoleId(DiscordApi api, String command) {
        super(api, command);
        this.description = CommandDescriptions.setAwsRole;
    }

    @Override
    public void doOwnerCommand(MessageCreateEvent messageCreateEvent) {
        TextChannel channel = messageCreateEvent.getChannel();
        if (hasArgument()) {
            if (isValidRole(messageCreateEvent, getLastArgument())) {
                ConfigUtil.setAwsRoleId(getLastArgument());
                getSimpleEmbedMessage("New aws role has been set", Color.BLUE)
                        .send(channel);
            }
            else {
                getSimpleErrorMessage("That's not a valid role Id for this server")
                        .send(channel);
            }
        }
        else {
            getSimpleEmbedMessage("No arguments found, please enter a roleId", Color.RED)
                    .send(channel);
        }
    }

    private boolean isValidRole(MessageCreateEvent messageCreateEvent, String input) {
       return messageCreateEvent.getServer().get()
                .getRoles()
                .stream()
                .map(Role::getIdAsString)
                .anyMatch(id -> id.equals(input));
    }

}
