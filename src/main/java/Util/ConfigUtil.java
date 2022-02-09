package Util;

import com.amazonaws.auth.BasicAWSCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class ConfigUtil {
    static Properties prop = new Properties();
    static String fileName = getPath("bot.properties");
    private static final Logger log = LoggerFactory.getLogger(ConfigUtil.class);
    static {
        loadProperties();
        log.info("Properties loaded");
    }

    public static Properties loadProperties() {
        log.info("Properties Path " + fileName);
        try (InputStream fis = new FileInputStream(fileName)) {
            prop.load(fis);
        } catch (FileNotFoundException ex) {
            log.error("File not found, make sure there is a bot.properties file runBot folder");
        } catch (IOException ex) {
            log.error("Issue Reading file");
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

    public static BasicAWSCredentials getBasicAwsCredentials() {
        return new BasicAWSCredentials(getAwsAccessKey(),getAwsSecretKey());
    }

    public static boolean isDebug() {
        String debug = prop.getProperty("discord.debug");
        if (debug == null || debug.isEmpty() || debug.equals("false")) {
            return false;
        }
        return debug.equals("true");
    }

    public static boolean setDebug() {
        String debug = prop.getProperty("discord.debug");
        if (debug == null || debug.isEmpty() || debug.equals("false")) {
            prop.setProperty("discord.debug", "true");
            return updateConfigFile();
        }
        if (debug.equals("true")) {
            prop.setProperty("discord.debug", "false");
            return updateConfigFile();
        }
        throw new IllegalStateException("Could not set debug mode");
    }

    public static String getCheckmarkEmoji() {
        return prop.getProperty("discord.checkmarkEmoji");
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

    public static boolean setAwsRoleId(String serverId, String roleId) {
        prop.setProperty( "discord.awsRole." + serverId, roleId);
        return updateConfigFile();
    }

    /**
     * returns role for a given serverId.
     * @param serverId
     * @return
     */
    public static String getAwsDiscordRole(String serverId) {
        return prop.getProperty(String.format("discord.awsRole.%s", serverId));
    }

    public static boolean setAwsRegion(String roleId) {
            prop.setProperty("aws.region", roleId);
            return updateConfigFile();
    }

    public static boolean setAwsAlias(String instanceId, String aliasName) {
        prop.setProperty("aws.alias." + instanceId, aliasName );
        return updateConfigFile();
    }

    public static boolean hasAlias(String instanceId) {
        return prop.getProperty("aws.alias."+instanceId) != null;
    }

    public static String getInstanceAlias(String instanceId) {
        return prop.getProperty("aws.alias."+instanceId);
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
