package Listeners;

import Util.ConfigUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.List;

import static Util.MessageUtil.getOrDefault;

public class HelpCommand extends CommandWrapper {

    List<CommandWrapper> allCommands;

    public HelpCommand(DiscordApi api, String command, List<CommandWrapper> allCommands) {
        super(api, command);
        this.allCommands = allCommands;
        this.description = CommandDescriptions.help;
    }

    @Override
    public void doAction(MessageCreateEvent messageCreateEvent) {
        String prefix = (ConfigUtil.getPrefix() != null) ? ConfigUtil.getPrefix() : "";
        String prefix2 = (prefix.equals("") || prefix.equals("@mention")) ? messageCreateEvent.getApi().getYourself().getMentionTag() + " " : prefix;
        EmbedBuilder embedBuilder = new EmbedBuilder();
        StringBuilder sb = new StringBuilder();
        allCommands.forEach(command -> sb.append(String.format("%s**%s** \n %s\n\n", prefix2 , command.getCommandName(), getOrDefault(command.getCommandDescription()) ) ));
        User user = messageCreateEvent.getMessageAuthor().asUser().get();
        new MessageBuilder()
                .addEmbed(embedBuilder
                        .setTitle("Available Commands")
                        .setDescription(sb.toString())
                        .setColor(new Color(48, 97, 219))).send(user);
        messageCreateEvent.getMessage().addReaction("✅");
    }
}
