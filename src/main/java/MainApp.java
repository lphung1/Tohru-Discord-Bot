import AwsServices.AwsEc2Service;
import Listeners.DetailsCommand;
import Listeners.GetAwsRegion;
import Listeners.InstanceIpCommand;
import Listeners.RebootCommand;
import Listeners.SetAwsRegion;
import Listeners.SetAwsRoleId;
import Listeners.SetInstanceIdCommand;
import Listeners.StartServerCommand;
import Listeners.StopServerCommand;
import Util.ConfigUtil;
import Util.RunnableCallback;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class MainApp {
    private static Logger log = Logger.getLogger(MainApp.class.getName());
    private static AwsEc2Service ec2Service = AwsEc2Service.getService();

    public static void main(String[] args) {

        DiscordApi api = new DiscordApiBuilder()
                .setToken(ConfigUtil.getDiscordToken())
                .login()
                .join();
        api.addMessageCreateListener(new StartServerCommand(api));
        api.addMessageCreateListener(new StopServerCommand(api));
        api.addMessageCreateListener(new SetInstanceIdCommand(api));
        api.addMessageCreateListener(new RebootCommand(api));
        api.addMessageCreateListener(new DetailsCommand(api));
        api.addMessageCreateListener(new InstanceIpCommand(api));
        api.addMessageCreateListener(new SetAwsRoleId(api));
        api.addMessageCreateListener(new SetAwsRegion(api));
        api.addMessageCreateListener(new GetAwsRegion(api));

        Consumer<DiscordApi> setStatus = discordApi -> ec2Service.updateBotStatus(discordApi);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(new RunnableCallback<>(setStatus, api), 0, 3, TimeUnit.MINUTES );

        log.info("Bot now listening");
    }



}
