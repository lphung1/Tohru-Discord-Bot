package Listeners.OwnerCommands;

import AwsServices.AwsCostExplorerService;
import Listeners.CommandDescriptions;
import Listeners.OwnerCommand;
import com.amazonaws.services.costexplorer.model.DateInterval;
import com.amazonaws.services.costexplorer.model.GetCostAndUsageResult;
import com.amazonaws.services.costexplorer.model.Group;
import com.amazonaws.services.costexplorer.model.MetricValue;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;

public class BillingCommand extends OwnerCommand {

    AwsCostExplorerService costExplorerService;

    public BillingCommand(DiscordApi api, String command) {
        super(api, command);
        costExplorerService = AwsCostExplorerService.getService();
        this.description = CommandDescriptions.bill;
    }

    @Override
    public void doOwnerCommand(MessageCreateEvent messageCreateEvent) {
        User user = messageCreateEvent.getMessageAuthor().asUser().get();

        GetCostAndUsageResult costAndUsageResult = costExplorerService.getMonthToDateCostGranular();
        DateInterval interval = costAndUsageResult.getResultsByTime().get(0).getTimePeriod();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Month to date cost")
                .setFooter(String.format("%s - %s", interval.getStart(), interval.getEnd()))
                .setColor(new Color(48, 97, 219));

        float totalCost = 0;

        for (Group g : costAndUsageResult.getResultsByTime().get(0).getGroups()) {
            if ((Float.parseFloat(g.getMetrics().get("UnblendedCost").getAmount()) > 0.01)) {
                MetricValue m = g.getMetrics().get("UnblendedCost");
                totalCost+=Float.valueOf(m.getAmount());
                embedBuilder.addField(g.getKeys().get(0), String.format("$%.2f %s", Float.valueOf(m.getAmount()), m.getUnit()));
            }
        }

        embedBuilder.setDescription(String.format("Total Month to Date $%.2f", totalCost));

        new MessageBuilder()
                .addEmbed(embedBuilder).send(user);
    }
}
