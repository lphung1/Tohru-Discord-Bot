package Listeners;
import Util.ConfigUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.function.Function;

public abstract class CommandWrapper implements MessageCreateListener {

    DiscordApi api;
    static String prefix;
    String command;

    public CommandWrapper(DiscordApi api, String command) {
        this.api = api;
        this.command = command;
        String prefixConfig = ConfigUtil.getPrefix();
        prefix = (prefixConfig == null || prefixConfig.equalsIgnoreCase("@mention")) ? "" : prefixConfig;
    }

    Function<MessageCreateEvent, Boolean> botInvoked = (messageCreateEvent -> {
        if (prefix.equals("")) {
            return (messageCreateEvent.getMessage().getMentionedUsers().contains(api.getYourself()));
        }
        return (messageCreateEvent.getMessageContent().startsWith(prefix));
    });

    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        if (botInvoked.apply(messageCreateEvent) && messageCreateEvent.getMessageContent().contains(command)) {
            doAction(messageCreateEvent);
        }
    }

    public abstract void doAction(MessageCreateEvent messageCreateEvent);

}
