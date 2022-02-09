package Listeners;

public interface CommandDescriptions {
    String start = "Starts the instance that's currently being tracked. \nCan also pass a parameter to start a specific instance by using the instance id or alias and or time in mins \nEx. @mention start <instanceId/name> <delayInMins>";
    String stop = "Stops the tracked instance. Can pass parameter to stop a specific instance by id or alias and or time in mins. Ex. @mention stop <instanceId/name> <delayInMins>";
    String reboot = "Reboots tracked instance Ex. @mention reboot <instanceId> <delayInMins>";
    String track = "Sets instanceId to track. Updates status based on tracked instance and will be default server for the other commands.\nadd <instanceId> argument to set the instanceID to track.";
    String details = "Shows details for current tracked instance. \nadd <all> parameter to view all available instances for current set region.";
    String serverIp = "Sends user the ip details for the tracked instance";
    String setAwsRole = "Sets the discord role to use AWS commands. add <roleId> argument";
    String setRegion = "Sets the AWS region for the bot. If servers are on different regions, you will need to update the region in order to view those instances." +
                        "\n Valid regions are us-east-1 us-east-2 us-west-1 us-west-2";
    String getRegion = "Shows the current aws region";
    String help = "Help command";
    String bill = "Shows billing information";
    String alias = "Sets a nickname for the instance to easily see what server it's for.\n use <instanceId> <nickname> params or just <nickname> to update current tracked instance";
    String debug = "Sets debug mode on or off. Will print exception stack traces to channel if on";
    String monitor = "Gets CPU utilization monitoring stats. Accepts <hoursFromNow> param as integer, otherwise default to last hour";
}
