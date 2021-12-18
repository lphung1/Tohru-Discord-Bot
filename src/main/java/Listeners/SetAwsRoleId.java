package Listeners;

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

    @Override
    public void doOwnerCommand(MessageCreateEvent messageCreateEvent) {
        String[] strArr = messageCreateEvent.getMessageContent().split(" ");
        TextChannel channel = messageCreateEvent.getChannel();
        if (hasInput(strArr, channel)) {

            if (isValidRole(messageCreateEvent, strArr[2])) {
                ConfigUtil.setAwsRoleId(strArr[2]);
                getSimpleEmbedMessage("New aws role has been set", Color.BLUE)
                        .send(channel);
            }
            else {
                getSimpleEmbedMessage("That's not a valid role Id for this server", Color.RED)
                        .send(channel);
            }
        }
    }

    private boolean isValidRole(MessageCreateEvent messageCreateEvent, String input) {
       return messageCreateEvent.getServer().get()
                .getRoles()
                .stream()
                .map(Role::getIdAsString)
                .anyMatch(id -> id.equals(input));
    }

    private boolean hasInput(String[] strArr, TextChannel channel) {
        if (strArr != null && strArr.length > 2) {
            return true;
        }
        else {
            getSimpleEmbedMessage("No arguments found, please enter a roleId", Color.RED)
                    .send(channel);
            return false;
        }
    }

}
