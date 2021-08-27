package jp.sugarcoffee.processrunner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModLogManager {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final boolean isPrint = ConfigManager.KeyHolder.PRINT_LOG4J_INFO_MODE.getValue();

    public void pushOptionalInfo(String message) {
        if (isPrint) {
            LOGGER.info(message);
        }
    }
}
