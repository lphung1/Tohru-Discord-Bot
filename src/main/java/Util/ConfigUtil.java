package Util;

import com.amazonaws.auth.BasicAWSCredentials;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigUtil {
    static Properties prop = new Properties();
    static String fileName = getPath("bot.properties");
    static Logger log = Logger.getLogger(ConfigUtil.class.getName());
    static {
        loadProperties();
        log.info("Properties loaded");
    }

    public static Properties loadProperties() {
        log.info("Properties Path " + fileName);
        try (InputStream fis = new FileInputStream(fileName)) {
            prop.load(fis);
        } catch (FileNotFoundException ex) {
            log.log(Level.SEVERE, "File not found, make sure there is a bot.properties file in src/main/resources");
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Issue Reading file");
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

    public static String getEC2InstanceId() { return prop.getProperty("aws.ec2InstanceId"); }

    public static String getAwsDiscordRole() { return prop.getProperty("discord.awsRole"); }

    public static BasicAWSCredentials getBasicAwsCredentials() {
        return new BasicAWSCredentials(getAwsAccessKey(),getAwsSecretKey());
    }

    public static String getLambdaFunction(int num) {
        return prop.getProperty("aws.lambda."+num);
    }

    public static boolean setLambdaFunction(int lambdaNum, String lambdaArn) {
            prop.setProperty("aws.lambda."+lambdaNum, lambdaArn);
            return updateConfigFile();
    }

    public static boolean setEc2InstanceId(String ec2InstanceId) {
            prop.setProperty("aws.ec2InstanceId", ec2InstanceId);
            return updateConfigFile();
    }

    public static boolean setAwsRoleId(String roleId) {
            prop.setProperty("discord.awsRole", roleId);
            return updateConfigFile();
    }

    public static boolean setAwsRegion(String roleId) {
            prop.setProperty("aws.region", roleId);
            return updateConfigFile();
    }

    private static boolean updateConfigFile() {
        try (OutputStream outStream = new FileOutputStream(fileName)) {
            prop.store(outStream, "File updated");
            return true;
        } catch (IOException e) {
            log.info("Issue with writing to file.");
            return false;
        }
    }

    private static String getPath(String path) {
        try {
            return Paths.get(new File(ConfigUtil.class.getProtectionDomain()
                                    .getCodeSource()
                                    .getLocation()
                                    .toURI())
                                    .getParentFile()
                                    .getPath() + File.separator + path)
                                    .toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }


}
