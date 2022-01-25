package Listeners;
import Util.ConfigUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageDecoration;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class CommandWrapper implements MessageCreateListener {

    protected DiscordApi api;
    protected static String prefix;
    protected String command;
    protected String description;
    protected List<String> argumentList;

    public CommandWrapper(DiscordApi api, String command) {
        this.api = api;
        this.command = command;
        String prefixConfig = ConfigUtil.getPrefix();
        prefix = (prefixConfig == null || prefixConfig.equalsIgnoreCase("@mention")) ? "" : prefixConfig;
    }

    protected Function<MessageCreateEvent, Boolean> botInvoked = (messageCreateEvent -> {
        if (prefix.equals("")) {
            return (messageCreateEvent.getMessage().getMentionedUsers().contains(api.getYourself()));
        }
        return (messageCreateEvent.getMessageContent().startsWith(prefix));
    });

    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        try {
            if (botInvoked.apply(messageCreateEvent) && messageCreateEvent.getMessageContent().contains(command)) {
                argumentList = getMessageArgList(messageCreateEvent);
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

    protected Set<String> getMessageArgsSet(MessageCreateEvent messageCreateEvent) {
        return Arrays.stream(messageCreateEvent.getMessageContent()
                        .split(" +"))
                .map(String::trim)
                .collect(Collectors.toSet());
    }

    protected List<String> getMessageArgList(MessageCreateEvent messageCreateEvent) {
        return Arrays.stream(messageCreateEvent.getMessageContent()
                        .split(" +"))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    protected String getLastArgument() {
        int argSize = argumentList.size();
        String lastArg = argumentList.get(argSize - 1);
        return (hasArgument()) ? lastArg : null;
    }

    protected boolean hasArgument() {
        int argSize = argumentList.size();
        if (argSize > 1) {
            String lastArg = argumentList.get(argSize - 1);
            return !lastArg.equalsIgnoreCase(command);
        }
        return false;
    }

    public abstract void doAction(MessageCreateEvent messageCreateEvent);

    public String getCommandName() {
        return command;
    }

    public String getCommandDescription() {
        return description;
    }

}
