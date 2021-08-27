package jp.sugarcoffee.processrunner.forge;

import jp.sugarcoffee.processrunner.ProcessRunnerExpectPlatform;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

@SuppressWarnings("unused")
public class ProcessRunnerExpectPlatformImpl {
    /**
     * This is our actual method to {@link ProcessRunnerExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static Path getGameDirectory() {
        return getConfigDirectory().getParent();
    }
}
