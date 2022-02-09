package Listeners.AwsCommands;

import Listeners.AwsCommand;
import Listeners.CommandDescriptions;
import Util.ConfigUtil;
import Util.MessageUtil;
import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Monitor extends AwsCommand {

    public Monitor(DiscordApi api, String command) {
        super(api, command);
        description = CommandDescriptions.monitor;
    }

    @Override
    public void doAwsAction(MessageCreateEvent messageCreateEvent) {

        try {
            InputStream is;
            int timeRange;

            if (hasArgument() && MessageUtil.isDigit(getLastArgument())) {
                timeRange = Integer.parseInt(getLastArgument());
            }
            else {
                timeRange = 1;
            }

            is = getImageInputStream(awsCloudWatchService.getMetricStatisticsResult(timeRange).getMetricWidgetImage());

            if (is == null) {
                MessageUtil.getSimpleErrorMessage("Error reading image from response");
            }
            String instanceId = ConfigUtil.getEC2InstanceId();

            String alias = (ConfigUtil.hasAlias(instanceId)) ? String.format("- (%s)", ConfigUtil.getInstanceAlias(instanceId)) : "";
            String hourStr = (timeRange > 1) ? "hours" : "hour";

            new MessageBuilder()
                    .addEmbed(new EmbedBuilder()
                            .setImage(is, "png")
                            .setTitle(String.format("Stats for last %d %s %s", timeRange, hourStr, alias)))
                    .send(messageCreateEvent.getChannel());

        } catch (IOException e) {
            MessageUtil.getSimpleErrorMessage("Error reading image from response");
        }
    }

    public InputStream getImageInputStream(ByteBuffer imageByteBuffer) throws IOException {
        try (InputStream is = new ByteBufferBackedInputStream(imageByteBuffer)) {
            return is;
        }
    }
}
