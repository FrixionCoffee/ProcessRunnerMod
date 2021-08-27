package jp.sugarcoffee.processrunner;

/**
 * Runnableでもいい気がするけど別スレッドで動作させることは保証しないので、このクラスを使う。
 */
@FunctionalInterface
interface Execute {

    void run();

}
