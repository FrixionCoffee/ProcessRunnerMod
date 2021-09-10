package jp.sugarcoffee.processrunner;

import java.util.Arrays;
import java.util.List;

/**
 * staticフィールドのオブジェクト参照を切ってGCの対象にする処理を行うクラス
 * 通常のMODと異なりこのMODはリソースを持ち続ける必要性がないので正常に外部プロセス起動後は削除可能な要素は全て消す
 *
 * 最初からインスタンス変数だけにしておけばよかった...
 */
public final class ResourceKiller {

    void kill() {
        final List<StaticResourceCleanable> cleanableList = Arrays.asList(
                new ConfigManager(),
                new ModLogManager()
        );

        for (StaticResourceCleanable cleanable: cleanableList
             ) {
            cleanable.clean();
        }
    }

}
