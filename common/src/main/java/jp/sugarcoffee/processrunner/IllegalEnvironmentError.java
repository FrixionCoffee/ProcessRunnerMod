package jp.sugarcoffee.processrunner;

/**
 * 予期しない環境を示すエラー。try等によってプログラムが回復を図るべきではないエラー。
 * (実際のところ管理者権限での起動を感知したとき・OSがWindows以外のみ投げられる)
 *
 * このMODはこのエラーをcatchすることはありません。
 */
public final class IllegalEnvironmentError extends Error {

    public IllegalEnvironmentError(String message) {
        super(message);
    }
}
