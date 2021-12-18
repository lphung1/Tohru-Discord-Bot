package Listeners;

import Util.ConfigUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.io.IOException;
import java.util.logging.Logger;

import static Util.MessageUtil.*;

public class SetInstanceIdCommand extends AwsCommand {

    static Logger log = Logger.getLogger(SetInstanceIdCommand.class.getName());

    public SetInstanceIdCommand(DiscordApi api) {
        super(api, "setInstanceId");
    }

    @Override
    public void doAwsAction(MessageCreateEvent messageCreateEvent) {
        String[] strArr = messageCreateEvent.getMessageContent().split(" ");
        TextChannel channel = messageCreateEvent.getChannel();
        if (strArr != null && strArr.length > 2) {
            if (ConfigUtil.setEc2InstanceId(strArr[2])) {
                getSimpleEmbedMessage(String.format("Now tracking instanceId : %s", strArr[2]))
                        .send(channel);
            } else {
                getSimpleEmbedMessage("Issue with setting instance Id.", Color.RED)
                        .send(channel);
            }
        }
    }
}
