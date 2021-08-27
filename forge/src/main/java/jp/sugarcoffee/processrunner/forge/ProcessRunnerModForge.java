package jp.sugarcoffee.processrunner.forge;

import jp.sugarcoffee.processrunner.ProcessRunnerMod;
import me.shedaniel.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ProcessRunnerMod.MOD_ID)
public class ProcessRunnerModForge {
    public ProcessRunnerModForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(ProcessRunnerMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        ProcessRunnerMod.init();
    }
}
