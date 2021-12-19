package Listeners;

import Util.ConfigUtil;
import com.amazonaws.regions.Regions;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.Arrays;

import static Util.MessageUtil.getSimpleEmbedMessage;

public class SetAwsRegion extends AwsCommand {

    public SetAwsRegion(DiscordApi api) {
        super(api, "setRegion");
    }

    @Override
    public void doAwsAction(MessageCreateEvent messageCreateEvent) {
        String[] strArr = messageCreateEvent.getMessageContent().split(" ");
        TextChannel channel = messageCreateEvent.getChannel();
        if (strArr != null && strArr.length > 2 && isValidRegion(strArr[2], channel)) {
            String arg = strArr[2];
            if (ConfigUtil.setAwsRegion(arg)) {
                awsEc2Service.updateService();
                getSimpleEmbedMessage(String.format("Updated Region : %s", arg))
                        .send(channel);
            } else {
                getSimpleEmbedMessage("Issue with setting instance Id.", Color.RED)
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
