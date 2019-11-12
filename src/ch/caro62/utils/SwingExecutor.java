package ch.caro62.utils;

import javax.swing.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SwingExecutor {

    private final static ScheduledExecutorService SCHEDULED_THREAD_POOL_EXECUTOR =
            Executors.newScheduledThreadPool(1, new SwingThreadFactory());


    private SwingExecutor() {

    }

    public static ScheduledFuture<?> schedule(Runnable command,
                                              long delay,
                                              TimeUnit unit) {
        return SCHEDULED_THREAD_POOL_EXECUTOR.schedule(() -> {
            SwingUtilities.invokeLater(command);
        }, delay, unit);
    }

    public <V> ScheduledFuture<V> schedule(Callable<V> callable,
                                           long delay,
                                           TimeUnit unit) {
        return SCHEDULED_THREAD_POOL_EXECUTOR.schedule(callable, delay, unit);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
                                                  long initialDelay,
                                                  long period,
                                                  TimeUnit unit) {
        return SCHEDULED_THREAD_POOL_EXECUTOR.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command,
                                                     long initialDelay,
                                                     long delay,
                                                     TimeUnit unit) {
        return SCHEDULED_THREAD_POOL_EXECUTOR.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }

    static class SwingThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        SwingThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "swing-pool-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

}
