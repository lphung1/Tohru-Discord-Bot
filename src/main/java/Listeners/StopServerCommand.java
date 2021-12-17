package Listeners;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

public class StopServerCommand extends AwsCommand {

    public StopServerCommand(DiscordApi api) {
        super(api, "stopServer");
    }

    @Override
    public void doAwsAction(MessageCreateEvent messageCreateEvent) {
        messageCreateEvent.getChannel().sendMessage("stopServer called");
        MessageBuilder messageBuilder = new MessageBuilder();

        if (awsEc2Service.stopEc2()) {
            messageCreateEvent.getChannel().sendMessage("Request to stop successful");
        }
        else {
            messageCreateEvent.getChannel().sendMessage("Issue with request to stop server");
        }
    }
}
