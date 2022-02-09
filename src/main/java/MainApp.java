import Listeners.AwsCommands.Monitor;
import Listeners.OwnerCommands.SetDebug;
import Services.AwsServices.AwsEc2Service;
import Listeners.AwsCommands.DetailsCommand;
import Listeners.AwsCommands.GetAwsRegion;
import Listeners.AwsCommands.InstanceIpCommand;
import Listeners.AwsCommands.RebootCommand;
import Listeners.AwsCommands.SetAwsRegion;
import Listeners.AwsCommands.SetInstanceAlias;
import Listeners.CommandWrapper;
import Listeners.HelpCommand;
import Listeners.OwnerCommands.BillingCommand;
import Listeners.OwnerCommands.SetAwsRoleId;
import Listeners.AwsCommands.SetInstanceIdCommand;
import Listeners.AwsCommands.StartServerCommand;
import Listeners.AwsCommands.StopServerCommand;
import Services.ExecutorProvider;
import Util.ConfigUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainApp {
    private static Logger log = LoggerFactory.getLogger(MainApp.class);
    private static AwsEc2Service ec2Service = AwsEc2Service.getService();

    public static void main(String[] args) {

        DiscordApi api = new DiscordApiBuilder()
                .setToken(ConfigUtil.getDiscordToken())
                .login()
                .join();

        List<CommandWrapper> listenerList = new ArrayList();
        listenerList.add(new StartServerCommand(api, "start"));
        listenerList.add(new StopServerCommand(api, "stop"));
        listenerList.add(new SetInstanceIdCommand(api, "track"));
        listenerList.add(new RebootCommand(api, "reboot"));
        listenerList.add(new DetailsCommand(api, "details"));
        listenerList.add(new InstanceIpCommand(api, "serverIp"));
        listenerList.add(new SetAwsRoleId(api, "setAwsRole"));
        listenerList.add(new SetAwsRegion(api, "setRegion"));
        listenerList.add(new GetAwsRegion(api, "region"));
        listenerList.add(new BillingCommand(api, "bill"));
        listenerList.add(new SetDebug(api, "debug"));
        listenerList.add(new Monitor(api, "monitor"));
        listenerList.add(new HelpCommand(api, "help", listenerList));
        listenerList.add(new SetInstanceAlias(api, "alias"));

        listenerList.forEach(listener -> api.addMessageCreateListener(listener));
        Runnable setStatus = () -> ec2Service.updateBotStatus(api);
        ScheduledExecutorService scheduler = ExecutorProvider.getSingleScheduledExecutor();

        scheduler.scheduleAtFixedRate(setStatus, 0, 3, TimeUnit.MINUTES );

        log.info("Bot is now listening");
    }
}
