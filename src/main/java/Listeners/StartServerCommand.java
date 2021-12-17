package Listeners;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

public class StartServerCommand extends AwsCommand {

    public StartServerCommand(DiscordApi api) {
        super(api, "startServer");
    }

    @Override
    public void doAwsAction(MessageCreateEvent messageCreateEvent) {
        MessageBuilder messageBuilder = new MessageBuilder();
        messageCreateEvent.getChannel().sendMessage("startServer called");
        if (awsLambdaService.startEc2()) {
            messageCreateEvent.getChannel().sendMessage("Request to start successful");
        }
        else {
            messageCreateEvent.getChannel().sendMessage("Issue with request");
        }
    }



}
