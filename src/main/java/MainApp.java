import Listeners.DetailsCommand;
import Listeners.RebootCommand;
import Listeners.SetInstanceIdCommand;
import Listeners.StartServerCommand;
import Listeners.StopServerCommand;
import Util.ConfigUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.util.logging.Logger;

public class MainApp {
    private static Logger log = Logger.getLogger(MainApp.class.getName());

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

        log.info("Bot now listening");
    }

}
