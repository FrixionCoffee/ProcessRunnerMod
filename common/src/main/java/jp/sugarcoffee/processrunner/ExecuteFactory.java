package jp.sugarcoffee.processrunner;

import org.apache.commons.lang3.StringUtils;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class ExecuteFactory {
    private final ConfigRecord configRecord;

    private static class ThreadProcessObserver implements Execute{
        public final Process process;

        public ThreadProcessObserver(Process process) {
            this.process = process;
        }

        @Override
        public void run() {
            new Thread(() -> {
                try {
                    process.waitFor();
                    if (process.isAlive()) {
                        throw new AssertionError();
                    }

                    ProcessStreamManager.processClose(process);
                    new ModLogManager().pushOptionalInfo("not wait for thread fin");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

            }).start();

        }

    }

    private static class BaseExecute implements Execute {
        public final ConfigRecord configRecord;

        public BaseExecute(ConfigRecord configRecord) {
            this.configRecord = configRecord;
        }

        @Override
        public void run() {

            initLog();
            LambdaExceptionWrapper.voidRun(this::runProcess, RuntimeException::new);
        }

        private void runProcess() throws InterruptedException, IOException {
            final ProcessBuilder builder = new ProcessBuilder(createCommandList());
                final Process process = builder
                        .directory(configRecord.processDir.toFile())
                        .redirectErrorStream(true)
                        .redirectOutput(configRecord.logPath.toFile())
                        .start();

                new ModLogManager().pushOptionalInfo("process thread start.");

                if (configRecord.processWaitFor) {
                    after(process);
                    new ModLogManager().pushOptionalInfo("waitFor thread fin.");
                }else {
                    final ThreadProcessObserver observer = new ThreadProcessObserver(process);
                    observer.run();
                }

        }

        private void initLog() {
            final LogFileManager logFileManager = new LogFileManager(configRecord.logPath);
            logFileManager.optionalCreate();

        }

        private List<String> createCommandList() {
            final List<String> commandList = new ArrayList<>();
            commandList.add(normalizeAtStr(configRecord.commandFilePath));

            commandList.addAll(
                    Arrays.asList(configRecord.argument.split(" "))
            );

            return Collections.unmodifiableList(
                    commandList.stream()
                            .filter(StringUtils::isNotBlank)
                            .collect(Collectors.toList())
            );

        }

        private void after(final Process process) throws InterruptedException {
            final int exitCode = process.waitFor();

            if (process.isAlive()) {
                throw new AssertionError("request process.waitFor()");
            }

            ProcessStreamManager.processClose(process);

            if (configRecord.writeExitCode) {
                appendLog(
                        Collections.singletonList("exit Code: " + exitCode)
                );
            }

        }

        private void appendLog(List<String> logList) {
            try {
                Files.write(
                        configRecord.logPath,
                        logList,
                        StandardCharsets.UTF_8,
                        StandardOpenOption.APPEND
                );
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        private String normalizeAtStr(Path path) {
            return path.normalize().toAbsolutePath().toString();
        }
    }

    public ExecuteFactory(ConfigRecord configRecord) {
        this.configRecord = configRecord;
    }

    Execute create() {

        if (configRecord.allIgnore) {
            return createAllIgnore();
        }
        return new BaseExecute(this.configRecord);
    }

    /**
     * 何もしないExecuteの実装
     * @return void
     */
    public Execute createAllIgnore() {
        return () -> {
            assert true;
        };
    }

    private static final class ProcessStreamManager {
        public static void processClose(final Process process) {
            streamClose(process.getInputStream());
            streamClose(process.getOutputStream());
            streamClose(process.getErrorStream());
            new ModLogManager().pushOptionalInfo("closed process all stream");
        }

        private static void streamClose(final Closeable closeable) {
            if (closeable == null) {
                return;
            }

            try {
                closeable.close();

            }catch (ClosedChannelException ignore) {

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }


    private static class LogFileManager {

        public final Path logPath;

        public LogFileManager(Path logPath) {
            if (Files.isDirectory(logPath)) {
                throw new IllegalArgumentException(logPath + "is directory.");
            }
            this.logPath = logPath;
        }

        void optionalCreate() {

            if (Files.exists(logPath)) {
                return;
            }

            try {
                Files.createFile(logPath);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
