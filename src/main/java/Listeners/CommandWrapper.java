package Listeners;
import Util.ConfigUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageDecoration;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.NonThrowingAutoCloseable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class CommandWrapper implements MessageCreateListener {

    private static final Logger log = LoggerFactory.getLogger(CommandWrapper.class);

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
                try (NonThrowingAutoCloseable typingIndicator = messageCreateEvent.getChannel().typeContinuously(e -> log.error(e.toString()))) {
                    doAction(messageCreateEvent);
                    log.info("Command [{}] complete", command);
                }
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

    protected String getSecondLastArgument() {
        int argSize = argumentList.size();
        String secondLastArg = null;
        if (argSize > 2) {
            secondLastArg = argumentList.get(argSize - 2);
        }
        return (hasArgument()) ? secondLastArg : null;
    }

    protected boolean hasArgument() {
        return hasArgument(1);
    }

    protected boolean hasArgument(int numArguments) {
        int argSize = argumentList.size();
        if (argSize > numArguments) {
            String lastArg = argumentList.get(argSize - numArguments);
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
