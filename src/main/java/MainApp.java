import Listeners.StartServerCommand;
import Listeners.StopServerCommand;
import Util.ConfigUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;


public class MainApp {
    public static void main(String args[]) {

        DiscordApi api = new DiscordApiBuilder()
                .setToken(ConfigUtil.getDiscordToken())
                .login()
                .join();
        api.addMessageCreateListener(new StartServerCommand(api));
        api.addMessageCreateListener(new StopServerCommand(api));
        System.out.println("Bot now listening");
    }

}
