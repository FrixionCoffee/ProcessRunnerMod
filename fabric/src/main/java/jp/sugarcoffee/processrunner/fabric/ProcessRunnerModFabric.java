package jp.sugarcoffee.processrunner.fabric;

import jp.sugarcoffee.processrunner.ProcessRunnerMod;
import net.fabricmc.api.ModInitializer;

public class ProcessRunnerModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ProcessRunnerMod.init();
    }
}
