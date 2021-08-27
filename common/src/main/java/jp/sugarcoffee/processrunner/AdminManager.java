package jp.sugarcoffee.processrunner;

/**
 * 管理者権限関連のクラス
 * 実際のところ
 */
public class AdminManager {

    static class AdminCheckerFactory {

        static AdminChecker create() {
            String osName = System.getProperty("os.name");

            if (osName.matches(".*Windows.*") | osName.matches(".*windows.*")) {
                return new WindowsAdminChecker();
            }
            throw new IllegalEnvironmentError("Windows Only.(now)");
        }

    }

    /**
     * 管理者権限で起動されていないことを確認する。
     * @throws IllegalEnvironmentError 実行プロセスが管理者権限であるとき
     */
    public void consumeCheck() throws IllegalEnvironmentError {
        AdminCheckerFactory
                .create()
                .voidCheck();
    }

    interface AdminChecker {
        default void voidCheck() {
            throw new UnsupportedOperationException();
        }
    }

    @SuppressWarnings("unused")
    static class LinuxAdminChecker implements AdminChecker {
        // getUid()辺りでできそう
    }

    static class WindowsAdminChecker implements AdminChecker {

        @Override
        public void voidCheck() {
            if (Shell32Dll.INSTANCE.IsUserAnAdmin()) {
                throw new IllegalEnvironmentError(
                        "It has been detected that it is operating with administrator privileges. " +
                                "This mod will not work with administrator privileges."
                );
            }
        }
    }

}
