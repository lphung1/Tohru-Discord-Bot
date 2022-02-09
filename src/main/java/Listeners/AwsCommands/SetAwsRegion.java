package Listeners.AwsCommands;

import Listeners.AwsCommand;
import Listeners.CommandDescriptions;
import Util.ConfigUtil;
import com.amazonaws.regions.Regions;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.Arrays;

import static Util.MessageUtil.getSimpleEmbedMessage;
import static Util.MessageUtil.getSimpleErrorMessage;

public class SetAwsRegion extends AwsCommand {

    public SetAwsRegion(DiscordApi api, String command) {
        super(api, command);
        this.description = CommandDescriptions.setRegion;
    }

    @Override
    public void doAwsAction(MessageCreateEvent messageCreateEvent) {
        TextChannel channel = messageCreateEvent.getChannel();
        if (isValidRegion(getLastArgument(), channel)) {
            if (ConfigUtil.setAwsRegion(getLastArgument())) {
                awsEc2Service.updateService();
                costExplorerService.updateService();
                awsCloudWatchService.updateService();
                getSimpleEmbedMessage(String.format("Updated Region : %s", getLastArgument()))
                        .send(channel);
            } else {
                getSimpleErrorMessage("Issue with setting region.")
                        .send(channel);
            }
        }
    }

    private boolean isValidRegion(String regionInput, TextChannel channel) {
        boolean valid = Arrays.stream(Regions.values()).anyMatch(regions -> regions.getName().equals(regionInput));

        if(!valid) {
            StringBuilder sb = new StringBuilder();
            Arrays.stream(Regions.values())
                    .filter(regions -> regions.getName().contains("us"))
                    .filter(regions -> !regions.getName().contains("gov"))
                    .filter(regions -> !regions.getName().contains("iso"))
                            .forEach(regions -> sb.append(String.format("%s - %s \n", regions.getName(), regions.getDescription())));

            getSimpleEmbedMessage(String.format("%s is not a valid region.", regionInput), Color.RED)
                    .send(channel);

            new MessageBuilder().append("Use one of the following valid regions")
                    .appendCode("java", sb.toString())
                    .send(channel);
        }

        return valid;
    }
}
