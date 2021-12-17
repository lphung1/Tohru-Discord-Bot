package Listeners;

import Util.ConfigUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;

import java.io.IOException;
import java.util.logging.Logger;

public class SetInstanceIdCommand extends AwsCommand {

    static Logger log = Logger.getLogger(SetInstanceIdCommand.class.getName());

    public SetInstanceIdCommand(DiscordApi api) {
        super(api, "setInstanceId");
    }

    @Override
    public void doAwsAction(MessageCreateEvent messageCreateEvent) {
        String [] strArr = messageCreateEvent.getMessageContent().split(" ");

        if (strArr != null && strArr.length > 2) {
            try {
                ConfigUtil.setEc2InstanceId(strArr[2]);
                messageCreateEvent.getChannel().sendMessage("Set AWS EC2 instanceId to : " + strArr[2]);
            } catch (IOException e) {
                messageCreateEvent.getChannel().sendMessage("Issue trying to set instance id");
            }
        }


    }
}
