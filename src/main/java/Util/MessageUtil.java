package Util;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MessageUtil {

    static final Color defaultColor = Color.DARK_GRAY;

    static final Map<Integer, Color> statusToColorMap = new HashMap() {{
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

    public static Color stateColorHandler(Integer state) {
        return statusToColorMap.getOrDefault(state, defaultColor);
    }

    public static MessageBuilder getSimpleEmbedMessage(String title, String description) {
        return getSimpleEmbedMessage(title, description, defaultColor);
    }

    public static MessageBuilder getSimpleEmbedMessage(String title, String description, Color color) {
        return new MessageBuilder().addEmbed(
                new EmbedBuilder().setDescription(description)
                        .setTitle(title)
                        .setColor(color)
        );
    }

    public static MessageBuilder getSimpleEmbedMessage(String message) {
        return getSimpleEmbedMessage(message, defaultColor);
    }

    public static MessageBuilder getSimpleEmbedMessage(String message, Color color) {
        return new MessageBuilder().addEmbed(
                new EmbedBuilder().setTitle(message)
                        .setColor(color)
        );
    }

}
