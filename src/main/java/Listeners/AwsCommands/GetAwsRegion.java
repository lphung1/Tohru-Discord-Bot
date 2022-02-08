package Listeners.AwsCommands;

import Listeners.AwsCommand;
import Listeners.CommandDescriptions;
import Util.ConfigUtil;
import Util.MessageUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;

public class GetAwsRegion extends AwsCommand {

    public GetAwsRegion(DiscordApi api, String command) {
        super(api, command);
        this.description = CommandDescriptions.getRegion;
    }

    @Override
    public void doAwsAction(MessageCreateEvent messageCreateEvent) {
        MessageUtil.getSimpleEmbedMessage("Region currently set to " + ConfigUtil.getAwsRegion())
                .send(messageCreateEvent.getChannel());
    }
}
