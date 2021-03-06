package Util;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageDecoration;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static Util.ConfigUtil.getAwsRegion;
import static Util.ConfigUtil.isDebug;
import static Util.ConfigUtil.getEC2InstanceId;

public class MessageUtil {

    static final Color defaultColor = Color.DARK_GRAY;

    public static final Map<Integer, Color> statusToColorMap = new HashMap() {{
        /**
         * 0 : pending
         * 16 : running
         * 32 : shutting-down
         * 48 : terminated
         * 64 : stopping
         * 80 : stopped
         */
        put(16, Color.GREEN);
        put(32, Color.orange);
        put(80, Color.RED);
        put(64, new Color(245, 129, 29));
        put(0, Color.yellow);
    }};

    public static final Map<Integer, Color> statusCodeColorMap = new HashMap() {{
        // TODO Add more colors based on http response
        put(200, Color.GREEN);

    }};

    public static Color stateColorHandler(Integer state) {
        return statusToColorMap.getOrDefault(state, defaultColor);
    }

    public static MessageBuilder getSimpleEmbedMessage(String title, String description) {
        return getSimpleEmbedMessage(title, description, defaultColor);
    }

    public static EmbedBuilder getSimpleEmbed(String title, String description, Color color) {
        return new EmbedBuilder().setDescription(description)
                .setTitle(title)
                .setColor(color);
    }


    public static EmbedBuilder getSimpleEmbed(String title) {
        return new EmbedBuilder()
                .setTitle(title)
                .setColor(defaultColor);
    }

    public static EmbedBuilder getSimpleEmbed(String title, Color color) {
        return new EmbedBuilder().setTitle(title)
                .setColor(color);
    }

    public static MessageBuilder getSimpleEmbedMessage(String title, String description, Color color) {
        return new MessageBuilder().addEmbed(
                getSimpleEmbed(title, description, color)
        );
    }

    public static MessageBuilder getSimpleEmbedMessage(String message) {
        return getSimpleEmbedMessage(message, defaultColor);
    }

    public static MessageBuilder getSimpleEmbedMessage(String message, Color color) {
        return new MessageBuilder().addEmbed(
                getSimpleEmbed(message, color)
        );
    }

    public static MessageBuilder getSimpleErrorMessage(String message) {
        return getSimpleEmbedMessage(message, Color.RED);
    }

    public static MessageBuilder getInvalidInstanceMessage() {
        return getSimpleEmbedMessage(String.format("[%s] is not a valid instance ID for region %s, please set a valid id", getEC2InstanceId()), getAwsRegion(), Color.RED );
    }

    public static String getOrDefault(String string, String stringIfNull) {
        return (string == null || string.isEmpty()) ? stringIfNull : string;
    }

    public static String getOrDefault(String string) {
        return getOrDefault(string, "-");
    }

    public static boolean isDigit(String string) {
        return string.matches("[0-9]+");
    }

    public static void printStackTrace(MessageCreateEvent messageCreateEvent, Exception e) {
        // don't print stack trace if debug mode is false
        if (isDebug()) {
            StringBuilder sb = new StringBuilder().append(e + "\n");
            Arrays.stream(e.getStackTrace())
                    .map(StackTraceElement::toString)
                    .forEach(stack -> sb.append(stack + "\n\t"));
            sb.setLength(1500);

            new MessageBuilder()
                    .append(String.format("Something went wrong"), MessageDecoration.BOLD)
                    .appendCode("java", sb.toString())
                    .send(messageCreateEvent.getChannel());
        }
    }

}
