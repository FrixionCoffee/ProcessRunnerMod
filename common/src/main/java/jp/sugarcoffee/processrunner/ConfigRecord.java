package jp.sugarcoffee.processrunner;

import java.nio.file.Path;

import static jp.sugarcoffee.processrunner.ConfigManager.KeyHolder;

/**
 * 毎回プロパティ読み取りはコストが大きそうなので、データ保管用のインスタンスでやり取りする
 */
public class ConfigRecord {

    /**
     * コマンド引数
     */
    public final String argument;

    /**
     * 実行可能なパス。ProcessBuilderはシステムpathを見に行くことは保証してないっぽいので、絶対パス推奨。(cmdなら無問題)
     */
    public final Path commandFilePath;

    /**
     * ProcessBuilderのカレントディレクトリ
     */
    public final Path processDir;

    /**
     * ログファイルパス
     */
    public final Path logPath;

    /**
     * 外部プロセスの終了を待機するかどうか
     */
    public final boolean processWaitFor;

    /**
     * 一時的に何もしない
     */
    public final boolean allIgnore;

    /**
     * exitCodeもログに書き込むか
     */
    public final boolean writeExitCode;

    public final boolean infoLog4j;


    public ConfigRecord() {

        argument = KeyHolder.ARGUMENT.getValue();

        commandFilePath = KeyHolder.COMMAND_FILE_PATH.getValue();

        processDir = KeyHolder.PROCESS_BUILDER_DIR.getValue();
        logPath = KeyHolder.LOG_EXPORT_FILE_PATH.getValue();

        processWaitFor = KeyHolder.PROCESS_WAIT_FOR_MODE.getValue();
        allIgnore = KeyHolder.TEMPORARY_ALL_IGNORE_MODE.getValue();
        writeExitCode = KeyHolder.WRITE_EXIT_CODE_MODE.getValue();

        infoLog4j = KeyHolder.PRINT_LOG4J_INFO_MODE.getValue();
    }

    /**
     *
     * @return デバッグ用の読み取り値
     */
    @Override
    public String toString() {
        return "ConfigRecord{" +
                "argument='" + argument + '\'' +
                ", commandFilePath=" + commandFilePath +
                ", processDir=" + processDir +
                ", logPath=" + logPath +
                ", processWaitFor=" + processWaitFor +
                ", allIgnore=" + allIgnore +
                ", writeExitCode=" + writeExitCode +
                '}';
    }
}
