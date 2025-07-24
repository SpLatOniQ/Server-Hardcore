package splat.serverhc.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ServerHCConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHCConfig.class);
    private static final File CONFIG_FILE = new File("config/server-hardcore.properties");
    private static final Properties PROPERTIES = new Properties();

    public static boolean ANONYMOUS_DEATHS;
    public static String CUSTOM_KICK_MESSAGE;
    public static boolean NEW_TERMINAL_ON_RESTART;
    public static String START_BATCH_PATH;

    private ServerHCConfig() {}

    public static void loadConfig() {
        try {
            if (!CONFIG_FILE.exists()) {
                CONFIG_FILE.getParentFile().mkdirs();
                setDefaults();
                saveConfig();
            } else {
                try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
                    PROPERTIES.load(fis);
                }
                loadValues();
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load the config", e);
            setDefaults();
        }
    }

    private static void loadValues() {
        ANONYMOUS_DEATHS = Boolean.parseBoolean(PROPERTIES.getProperty("anonymousDeaths", "false"));
        CUSTOM_KICK_MESSAGE = PROPERTIES.getProperty("customKickMessage", "");
        NEW_TERMINAL_ON_RESTART = Boolean.parseBoolean(PROPERTIES.getProperty("newTerminalOnRestart", "false"));
        START_BATCH_PATH = PROPERTIES.getProperty("startFilePath", "start.bat");
    }

    private static void setDefaults() {
        PROPERTIES.setProperty("anonymousDeaths", "false");
        PROPERTIES.setProperty("customKickMessage", " has died! The world is being reset...");
        PROPERTIES.setProperty("newTerminalOnRestart", "true");
        PROPERTIES.setProperty("startFilePath", "start.bat");
        loadValues();
    }

    public static void saveConfig() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            PROPERTIES.store(fos, "ServerHC Config");
        } catch (IOException e) {
            LOGGER.error("Failed to save config", e);
        }
    }
}