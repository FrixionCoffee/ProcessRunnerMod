package jp.sugarcoffee.processrunner;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * Windowsのネイティブコードを呼び出すクラス
 * java17以降ならシールドクラスにしてJNA関連以外実装できなくしてもいいかも
 */
interface Shell32Dll extends Library {
    Shell32Dll INSTANCE = Native.loadLibrary("Shell32", Shell32Dll.class);

    /**
     *
     * @return 実行プロセスが管理者権限で起動しているか
     */
    boolean IsUserAnAdmin();
}
