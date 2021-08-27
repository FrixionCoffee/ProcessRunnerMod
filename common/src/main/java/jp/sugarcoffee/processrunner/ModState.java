package jp.sugarcoffee.processrunner;

/**
 * 外部プログラムを起動するかどうかを定義する
 *
 * 初回起動時は設定ファイルが生成されたばかりなのでPROCESS_DO_NOT_RUN
 * 以降PROCESS_RUN
 */
public enum ModState {
    PROCESS_RUN, PROCESS_DO_NOT_RUN
}
