package Util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConfigUtil {

    static final String fileName = "src/main/resources/bot.config";
    static Properties prop = new Properties();

    static {
        loadProperties();
        System.out.println("Properties Loaded");
    }

    public static Properties loadProperties() {
        try {
            FileInputStream fis = new FileInputStream(fileName);
            prop.load(fis);
        } catch (FileNotFoundException ex) {
            System.out.println("File not found, make sure there is a bot.config file in src/main/resources");
        } catch (IOException ex) {
            System.out.println("Issue reading config file.");
        }

        return prop;
    }

    public static String getDiscordToken() {
        return prop.getProperty("discord.token");
    }

    public static String getPrefix() {
        return prop.getProperty("discord.prefix");
    }

    public static String getAwsAccessKey() { return prop.getProperty("aws.accessKey"); }

    public static String getAwsSecretKey() { return prop.getProperty("aws.secretKey"); }

    public static String getAwsRegion() { return prop.getProperty("aws.region"); }

    public static String getStartLambdaName() { return prop.getProperty("aws.startLambdaName"); }

    public static String getStopLambdaName() { return prop.getProperty("aws.stopLambdaName"); }


}
