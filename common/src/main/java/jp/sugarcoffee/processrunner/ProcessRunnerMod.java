package jp.sugarcoffee.processrunner;

public class ProcessRunnerMod {
    public static final String MOD_ID = "processrunnermod";

    public static void init() {
        final AdminManager manager = new AdminManager();
        manager.consumeCheck();

        final ConfigManager configManager = new ConfigManager();
        final ModState state = configManager.init();

        if (state == ModState.PROCESS_DO_NOT_RUN) {
            new ModLogManager().pushOptionalInfo("processDoNotRun.");
            return;
        }

        final ConfigRecord configRecord = new ConfigRecord();
        new ModLogManager().pushOptionalInfo(configRecord.toString());

        final Execute execute = new ExecuteFactory(configRecord).create();
        execute.run();
    }

}
