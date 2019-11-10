package ch.caro62.utils;

import java.util.concurrent.*;

/**
 * @author karo62
 */
public class TaskManager {


    private static final ScheduledExecutorService SCH = Executors.newSingleThreadScheduledExecutor();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(SCH::shutdown));
    }

    public static <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return SCH.schedule(callable, delay, unit);
    }


}
