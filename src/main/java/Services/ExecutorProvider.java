package Services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class ExecutorProvider {

    private static ScheduledExecutorService singleScheduledExecutor;
    private static ExecutorService executorService;

    private ExecutorProvider() {
    }

    public static synchronized ScheduledExecutorService getSingleScheduledExecutor() {
        if (singleScheduledExecutor == null ) {
            singleScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        }
        return singleScheduledExecutor;
    }

    public static synchronized ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(5);
        }
        return executorService;
    }
}
