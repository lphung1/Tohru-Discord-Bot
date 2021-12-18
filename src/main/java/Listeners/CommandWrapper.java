package Listeners;
import Util.ConfigUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageDecoration;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class CommandWrapper implements MessageCreateListener {

    DiscordApi api;
    static String prefix;
    String command;
    String description;

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
        try {
            if (botInvoked.apply(messageCreateEvent) && messageCreateEvent.getMessageContent().contains(command)) {
                doAction(messageCreateEvent);
            }
        }
        catch (Exception e) {
            StringBuilder sb = new StringBuilder().append(e+"\n");

            Arrays.stream(e.getStackTrace())
                    .map(StackTraceElement::toString)
                    .forEach(stack -> sb.append(stack+"\n\t"));
            sb.setLength(1500);

            new MessageBuilder()
                    .append(String.format("Something went wrong") , MessageDecoration.BOLD)
                    .appendCode("java", sb.toString())
                    .send(messageCreateEvent.getChannel());
        }
    }

    Set<String> getMessageArgsSet(MessageCreateEvent messageCreateEvent) {
        return Arrays.stream(messageCreateEvent.getMessageContent()
                        .split(" "))
                .map(String::trim)
                .collect(Collectors.toSet());
    }

    public abstract void doAction(MessageCreateEvent messageCreateEvent);

    public String getCommandName() {
        return command;
    }

    public String getCommandDescription() {
        return description;
    }

}
