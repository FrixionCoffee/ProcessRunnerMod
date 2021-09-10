package jp.sugarcoffee.processrunner;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.function.Function;

/**
 * 設定ファイルの管理を行う。が実際のところ殆ど内部クラスが処理しており、実質的に何もしていない
 */
public final class ConfigManager implements StaticResourceCleanable {
    static Path CONFIG_DIR = ProcessRunnerExpectPlatform.getConfigDirectory();

    /**
     * ConfigManagerがもっている唯一のメソッド。Configクラスに初期化を丸投げする。
     *
     * @return 初回起動時で外部プロセスは起動させないなら、ModState.PROCESS_DO_NOT_RUN;
     */
    ModState init() {
        return new Config().init();
    }

    @Override
    public void clean() {
        CONFIG_DIR = null;
        new KeyHolder().clean();
        new Config().clean();
    }

    /**
     * Propertiesを扱うためのクラス
     * コンストラクタはgetしたときのStringを R に変換するためのFunctionを要求する
     *
     * @param <R> Return param
     */
    static class Key<R> {
        public final String KEY_NAME;

        /**
         * プロパティファイルから読み取ったStringをRに変換する関数
         */
        public final Function<String, R> getValueTranslateFunction;

        public Key(String KEY_NAME, Function<String, R> getFunction) {
            this.KEY_NAME = KEY_NAME;
            this.getValueTranslateFunction = getFunction;
        }

        public R getValue() {
            final Properties readProperties = new Properties();
            try (final BufferedReader reader = Files.newBufferedReader(Config.CONFIG_FILE, StandardCharsets.UTF_8)) {
                readProperties.load(reader);
                String s = readProperties.getProperty(KEY_NAME);
                return getValueTranslateFunction.apply(s);

            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalStateException("properties get error. catch IOException", e);
            }
        }

        public void setInitValue(String value) {
            KeyHolder.INIT_PROPERTIES.setProperty(KEY_NAME, value);
        }

    }

    /**
     * 普通のString
     * 実装はプロパティの値をそのまま帰すだけ
     */
    static final class StringKey extends Key<String> {
        public StringKey(String KEY_NAME) {
            super(KEY_NAME, Function.identity());
            // String::toStringとほとんど等価(identityはs -> sよりも軽い可能性があるとかなんとか)
        }
    }

    /**
     * 設定値がパスを扱っているものに使う。
     */
    static final class PathKey extends Key<Path> {

        public PathKey(String KEY_NAME) {
            super(KEY_NAME, s -> Paths.get(s).toAbsolutePath());
        }

        public void setInitValue(Path initPath) {
            super.setInitValue(initPath.normalize().toAbsolutePath().toString());
        }
    }

    /**
     * 設定値がboolean型である物に使う。"true"などはセットできず例外が送出される。
     */
    static final class BooleanKey extends Key<Boolean> {
        public BooleanKey(String KEY_NAME) {
            super(KEY_NAME, Boolean::valueOf);
        }

        @Override
        public void setInitValue(String value) {
            throw new UnsupportedOperationException();
        }

        public void setInitValue(boolean value) {
            super.setInitValue(String.valueOf(value));
        }
    }

    /**
     * 各種プロパティ読み取り用のKeyクラスを保管するホルダークラス。
     * ホルダークラスだけど、最初のプロパティ生成時にstore()を呼ぶ必要がある。
     */
    static class KeyHolder implements StaticResourceCleanable {
        private static Properties INIT_PROPERTIES = new Properties();

        public static StringKey ARGUMENT = new StringKey("commandArgument");

        public static PathKey COMMAND_FILE_PATH = new PathKey("commandFilePath");
        public static PathKey PROCESS_BUILDER_DIR = new PathKey("processBuilderDirPath");
        public static PathKey LOG_EXPORT_FILE_PATH = new PathKey("logExportFilePath");

        public static BooleanKey PROCESS_WAIT_FOR_MODE = new BooleanKey("processWaitForMode");
        public static BooleanKey TEMPORARY_ALL_IGNORE_MODE = new BooleanKey("temporaryAllIgnoreMode");
        public static BooleanKey WRITE_EXIT_CODE_MODE = new BooleanKey("writeExitCodeMode");

        public static BooleanKey PRINT_LOG4J_INFO_MODE = new BooleanKey("printLog4jInfoMode");

        static void store() {
            try (final BufferedWriter writer = Files.newBufferedWriter(Config.CONFIG_FILE, StandardCharsets.UTF_8)) {
                INIT_PROPERTIES.store(writer, "ProcessRunnerMod create. Don't Use Admin Permission.");
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        @Override
        public void clean() {
            INIT_PROPERTIES = null;
            ARGUMENT = null;
            COMMAND_FILE_PATH = null;
            PROCESS_BUILDER_DIR = null;
            LOG_EXPORT_FILE_PATH = null;

            PROCESS_WAIT_FOR_MODE = null;
            TEMPORARY_ALL_IGNORE_MODE = null;
            WRITE_EXIT_CODE_MODE = null;

            PRINT_LOG4J_INFO_MODE = null;
        }
    }

    /**
     * プロパティの初期化処理を行うクラス
     */
    static class Config implements StaticResourceCleanable{
        public static Path CONFIG_FILE = CONFIG_DIR.resolve("process_runner_mod.properties").normalize().toAbsolutePath();

        @Override
        public void clean() {
            CONFIG_FILE = null;
        }

        enum ConfigFileState {
            INITIALIZED, NON_INITIALIZED
        }

        private ConfigFileState optionalCreate() {
            if (Files.exists(CONFIG_FILE)) {
                return ConfigFileState.INITIALIZED;
            }

            try {
                Files.createFile(CONFIG_FILE);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            return ConfigFileState.NON_INITIALIZED;
        }

        private void initProperties() {
            KeyHolder.COMMAND_FILE_PATH.setInitValue(Paths.get("C:/Windows/system32/cmd.exe"));
            KeyHolder.ARGUMENT.setInitValue("/c example.bat");

            final Path gameDir = ProcessRunnerExpectPlatform.getGameDirectory();
            KeyHolder.PROCESS_BUILDER_DIR.setInitValue(gameDir);

            KeyHolder.LOG_EXPORT_FILE_PATH.setInitValue(gameDir.resolve("ProcessRunnerLast.log"));

            KeyHolder.PROCESS_WAIT_FOR_MODE.setInitValue(true);
            KeyHolder.TEMPORARY_ALL_IGNORE_MODE.setInitValue(false);
            KeyHolder.WRITE_EXIT_CODE_MODE.setInitValue(true);

            KeyHolder.PRINT_LOG4J_INFO_MODE.setInitValue(true);
            KeyHolder.store();

        }

        /**
         * @return 外部プロセスを起動させるかさせないか
         */
        ModState init() {
            if (optionalCreate() == ConfigFileState.INITIALIZED) {
                return ModState.PROCESS_RUN;
            }

            initProperties();
            return ModState.PROCESS_DO_NOT_RUN;
        }
    }


}
