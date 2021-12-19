package Listeners;

import Util.ConfigUtil;
import Util.MessageUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;

public class GetAwsRegion extends AwsCommand {

    public GetAwsRegion(DiscordApi api) {
        super(api, "getRegion");
    }

    @Override
    public void doAwsAction(MessageCreateEvent messageCreateEvent) {
        MessageUtil.getSimpleEmbedMessage("Region currently set to " + ConfigUtil.getAwsRegion())
                .send(messageCreateEvent.getChannel());
    }
}
