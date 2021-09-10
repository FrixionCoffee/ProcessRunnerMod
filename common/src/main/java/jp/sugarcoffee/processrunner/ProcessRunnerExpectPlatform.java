package jp.sugarcoffee.processrunner;

import dev.architectury.injectables.annotations.ExpectPlatform;
import me.shedaniel.architectury.platform.Platform;

import java.nio.file.Path;

public class ProcessRunnerExpectPlatform {
    /**
     *
     */
    @ExpectPlatform
    public static Path getConfigDirectory() {
        // Just throw an error, the content should get replaced at runtime.
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Path getGameDirectory() {
        throw new AssertionError();
    }
}
