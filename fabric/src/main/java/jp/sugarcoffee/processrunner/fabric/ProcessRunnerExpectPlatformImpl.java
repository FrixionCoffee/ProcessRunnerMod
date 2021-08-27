package jp.sugarcoffee.processrunner.fabric;

import jp.sugarcoffee.processrunner.ProcessRunnerExpectPlatform;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

@SuppressWarnings("unused")
public class ProcessRunnerExpectPlatformImpl {
    /**
     * This is our actual method to {@link ProcessRunnerExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static Path getGameDirectory() {
        return getConfigDirectory().getParent();
    }
}
