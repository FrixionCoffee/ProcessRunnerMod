package jp.sugarcoffee.processrunner;

import java.nio.file.Path;

import static jp.sugarcoffee.processrunner.ConfigManager.KeyHolder;

/**
 * 毎回プロパティ読み取りはコストが大きそうなので、データ保管用のインスタンスでやり取りする
 */
public final class ConfigRecord {

    /**
     * コマンド引数
     */
    final String argument;

    /**
     * 実行可能なパス。ProcessBuilderはシステムpathを見に行くことは保証してないっぽいので、絶対パス推奨。(cmdなら無問題)
     */
    final Path commandFilePath;

    /**
     * ProcessBuilderのカレントディレクトリ
     */
    final Path processDir;

    /**
     * ログファイルパス
     */
    final Path logPath;

    /**
     * 外部プロセスの終了を待機するかどうか
     */
    final boolean processWaitFor;

    /**
     * 一時的に何もしない
     */
    final boolean allIgnore;

    /**
     * exitCodeもログに書き込むか
     */
    final boolean writeExitCode;

    final boolean infoLog4j;


    ConfigRecord() {

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
    @SuppressWarnings("unused")
    String devToString() {
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
