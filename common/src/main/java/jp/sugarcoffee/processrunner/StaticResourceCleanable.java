package jp.sugarcoffee.processrunner;

/**
 * クラスがアンロードされる前に全staticフィールドを解放可能であるインターフェース
 * 二度と実行しないことが実装上分かっている場合等に使用します。
 *
 * このインターフェースを実装している場合はそのままnew インスタンス化して実行しても問題ありません
 *
 * Process Runner Modは1度しかプロセス実行を行わないため、処理が終わったらstaticフィールドの参照を=nullで切ります。
 *
 * なお、インナークラスがこのインターフェースを実装している場合は外部クラスもこのインターフェースを実装しており、
 * インナーのclean()も実行するようになっています。
 *
 * clean()後に外部からリフレクション等でstaticフィールドが呼ばれると予期しない動作が発生する可能性があります。
 */
public interface StaticResourceCleanable {

    void clean();

}
