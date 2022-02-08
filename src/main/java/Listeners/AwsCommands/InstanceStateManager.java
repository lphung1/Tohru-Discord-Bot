package Listeners.AwsCommands;

import Listeners.AwsCommand;
import Services.ExecutorProvider;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.concurrent.TimeUnit;

import static Util.ConfigUtil.getEC2InstanceId;
import static Util.MessageUtil.getInvalidInstanceMessage;
import static Util.MessageUtil.getSimpleEmbedMessage;
import static Util.MessageUtil.isDigit;

public abstract class InstanceStateManager extends AwsCommand {

    public InstanceStateManager(DiscordApi api, String command) {
        super(api, command);
    }

    /**
     * Executes the according start/stop/reboot command with the validated instanceId
     * @param messageCreateEvent
     * @param instanceId
     */
    public abstract void changeInstanceState(MessageCreateEvent messageCreateEvent, String instanceId);

    @Override
    public void doAwsAction(MessageCreateEvent messageCreateEvent) {
        TextChannel channel = messageCreateEvent.getChannel();
        if (!hasArgument() && isValidInstance(getEC2InstanceId())) { // No args, execute on current instance
            //no params, execute on tracked instance now
            changeInstanceState(messageCreateEvent, getEC2InstanceId());
        }
        else if(hasArgument(2) && isDigit(getLastArgument()) && isValidInstance(getSecondLastArgument())){
            // <instanceId> <delayInMinutes>
            Runnable runnable = () -> changeInstanceState(messageCreateEvent, getSecondLastArgument());
            sendScheduledMessage(getLastArgument(), channel);
            ExecutorProvider.getSingleScheduledExecutor().schedule( runnable, Integer.parseInt(getLastArgument()), TimeUnit.MINUTES);
        }
        else if (hasArgument()) { // Has either delay or id in command
            // <delayInMinutes>
            if (isDigit(getLastArgument())) {
                sendScheduledMessage(getLastArgument(), channel);
                Runnable runnable = () -> changeInstanceState(messageCreateEvent, getEC2InstanceId());
                ExecutorProvider.getSingleScheduledExecutor().schedule(runnable, Integer.parseInt(getLastArgument()), TimeUnit.MINUTES);
            } // <instanceId>
            else if (isValidInstance(getLastArgument())) {
                changeInstanceState(messageCreateEvent, getLastArgument());
            }
        }
        else {
            getInvalidInstanceMessage().send(channel);
        }
    }

    public void sendScheduledMessage(String time, TextChannel channel) {
        getSimpleEmbedMessage(String.format("Will execute %s in %s minutes", command, time), Color.BLUE).send(channel);
    }

    protected boolean isValidInstance(String instanceId) {
        return awsEc2Service.isValidInstanceId(instanceId);
    }
}
