package jp.sugarcoffee.processrunner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModLogManager implements StaticResourceCleanable{
    public static Logger LOGGER = LogManager.getLogger();
    public static Boolean isPrint = ConfigManager.KeyHolder.PRINT_LOG4J_INFO_MODE.getValue();   //プリミティブだと参照切れなさそうなのでラッパーに変更

    void pushOptionalInfo(String message) {
        if (isPrint) {
            LOGGER.info(message);
        }
    }

    @Override
    public void clean() {
        LOGGER = null;
        isPrint = null;
    }
}
