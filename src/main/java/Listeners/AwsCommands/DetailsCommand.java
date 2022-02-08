package Listeners.AwsCommands;

import Listeners.AwsCommand;
import Listeners.CommandDescriptions;
import Util.ConfigUtil;
import com.amazonaws.services.ec2.model.CpuOptions;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.InstanceTypeInfo;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TimeZone;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static Util.ConfigUtil.getAwsRegion;
import static Util.ConfigUtil.getInstanceAlias;
import static Util.ConfigUtil.hasAlias;
import static Util.MessageUtil.*;
import static Util.ConfigUtil.getEC2InstanceId;

public class DetailsCommand<T extends Instance> extends AwsCommand {

    private static Logger log = Logger.getLogger(DetailsCommand.class.getName());

    public DetailsCommand(DiscordApi api, String command) {
        super(api, command);
        this.description = CommandDescriptions.details;
    }

    @Override
    public void doAwsAction(MessageCreateEvent messageCreateEvent) {
        TextChannel channel = messageCreateEvent.getChannel();
        Map<String, Instance> ec2DetailsMap = awsEc2Service.getEC2DetailsMap();
        Set<String> instanceTypeSet = ec2DetailsMap.values()
                .stream()
                .map(Instance::getInstanceType)
                .collect(Collectors.toSet());
        Map<String, InstanceTypeInfo> instanceTypeInfoMap = awsEc2Service
                .getInstanceTypeInfo(new ArrayList<>(instanceTypeSet));

        if (ec2DetailsMap.isEmpty()) {
            getSimpleEmbedMessage("No instances for " + ConfigUtil.getAwsRegion(), Color.RED).send(channel);
            return;
        }
        Set<String> commandArgs = getMessageArgsSet(messageCreateEvent);

        if (commandArgs.contains("all")) {
            showAllInstances(ec2DetailsMap, instanceTypeInfoMap).send(messageCreateEvent.getChannel());
        }
        else {
            showTrackedInstance(ec2DetailsMap, instanceTypeInfoMap).send(messageCreateEvent.getChannel());
        }
    }

    private MessageBuilder showAllInstances(Map<String, Instance> instanceSet, Map<String,InstanceTypeInfo> instanceTypeInfoMap) {
        return new MessageBuilder().addEmbeds(ec2Details(instanceSet.values(), instanceTypeInfoMap));
    }

    private MessageBuilder showTrackedInstance(Map<String, Instance> instanceSet, Map<String,InstanceTypeInfo> instanceTypeInfoMap) {
        if (instanceSet.containsKey(getEC2InstanceId())) {
            List<Instance> singleInstanceList = new ArrayList<>();
            singleInstanceList.add(instanceSet.get(getEC2InstanceId()));
            return new MessageBuilder().addEmbeds(ec2Details(singleInstanceList, instanceTypeInfoMap));
        }
        else {
            return getInvalidInstanceMessage();
        }
    }

    private EmbedBuilder[] ec2Details(Collection<Instance> instanceList, Map<String,InstanceTypeInfo> instanceTypeInfoMap) {
        List<EmbedBuilder> embedBuilderList = new ArrayList<>();

        instanceList.forEach(instance -> {
            InstanceState state = instance.getState();
            Date launchTime = instance.getLaunchTime();
            String instanceType = instance.getInstanceType();
            float memSize = instanceTypeInfoMap.get(instanceType).getMemoryInfo().getSizeInMiB().floatValue() / 1024F ;
            String memSizeStr = Float.toString(memSize);
            String alias = hasAlias(instance.getInstanceId()) ? String.format(" (%s)",getInstanceAlias(instance.getInstanceId()))  : "";
            String clockSpeed = getOrDefault(instanceTypeInfoMap.get(instanceType).getProcessorInfo().getSustainedClockSpeedInGhz().toString());

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("EC2 instance: " + instance.getInstanceId() + alias);
            CpuOptions cpuOptions = instance.getCpuOptions();
            embedBuilder.addInlineField("CPU Cores: ", cpuOptions.getCoreCount().toString());
            embedBuilder.addInlineField("CPU Threads: ", cpuOptions.getThreadsPerCore().toString());
            embedBuilder.addInlineField("Clock Speed: ", clockSpeed + " Ghz");
            embedBuilder.addInlineField("Memory:", String.format("%s GB",memSizeStr));
            embedBuilder.addInlineField("Status: ", state.getName());
            embedBuilder.addInlineField("Instance Type:", instance.getInstanceType());

            Date now = new Date(System.currentTimeMillis());
            if (state.getCode() == 16)
                embedBuilder.addInlineField("Up Time: ", timeSince(launchTime, now));

            try {
                BufferedImage image = ImageIO.read(ClassLoader.getSystemResource("Ec2.png"));
                embedBuilder.setThumbnail(image);
            } catch (IOException e) {
                log.info("Problem trying to embed image");
            }
            DateFormat dateFormat = new SimpleDateFormat("EEE, MMM d HH:mm:ss z yyyy");
            dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
            embedBuilder.setFooter(String.format("Region: %s \nLast Launched: %s ", getAwsRegion(), dateFormat.format(instance.getLaunchTime())));
            embedBuilder.setColor(stateColorHandler(state.getCode()));
            embedBuilderList.add(embedBuilder);
        });

        return embedBuilderList.toArray(new EmbedBuilder[embedBuilderList.size()]);
    }

    private String timeSince(Date then, Date now) {
        StringJoiner joiner = new StringJoiner("-");
        // parse method is used to parse
        // the text from a string to
        // produce the date
        Date d1 = then;
        Date d2 = now;

        // Calucalte time difference
        // in milliseconds
        long difference_In_Time = d2.getTime() - d1.getTime();

        // Calucalte time difference in
        // seconds, minutes, hours, years,
        // and days
        long difference_In_Seconds
                = (difference_In_Time
                / 1000)
                % 60;

        long difference_In_Minutes
                = (difference_In_Time
                / (1000 * 60))
                % 60;

        long difference_In_Hours
                = (difference_In_Time
                / (1000 * 60 * 60))
                % 24;

        long difference_In_Years
                = (difference_In_Time
                / (1000l * 60 * 60 * 24 * 365));

        long difference_In_Days
                = (difference_In_Time
                / (1000 * 60 * 60 * 24))
                % 365;

        // Print the date difference in
        // years, in days, in hours, in
        // minutes, and in seconds
        return (joiner.add(difference_In_Days+"d").add(difference_In_Hours+"h").add(difference_In_Minutes+"m").add(difference_In_Seconds+"s").toString());

    }

}
