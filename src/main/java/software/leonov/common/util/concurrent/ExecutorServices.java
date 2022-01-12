package software.leonov.common.util.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Static utility methods for {@link ExecutorService}s.
 * 
 * @author Zhenya Leonov
 */
public final class ExecutorServices {

    private ExecutorServices() {
    }

    /**
     * Halts the processing of pending tasks but does not attempt to stop actively executing tasks. All pending tasks are
     * drained (removed) from the work queue and returned when this method completes.
     * <p>
     * This method does not wait for actively executing tasks to terminate. Use
     * {@link ThreadPoolExecutor#awaitTermination(long, TimeUnit)} to do that.
     * <p>
     * This method is the middle ground between {@link ExecutorService#shutdown()} and
     * {@link ExecutorService#shutdownNow()}:
     * <ul>
     * <li>{@link ExecutorService#shutdown() shutdown()}: all actively executing tasks and pending tasks are allowed to
     * continue, but no new tasks will be accepted</li>
     * <li><b>shutdownFast()</b>: all actively executing tasks are allowed to continue, pending tasks are removed, and no
     * new tasks will be accepted</li>
     * <li>{@link ExecutorService#shutdownNow() shutdownNow()}: all actively executing tasks are <u>interrupted</u>, pending
     * tasks are removed, and no new tasks will be accepted</li>
     * </ul>
     * 
     * @param exec the specified executor service
     * @return the list of pending tasks
     */
    public static List<Runnable> shutdownFast(final ThreadPoolExecutor exec) {
        if (exec == null)
            throw new NullPointerException("exec == null");
        final List<Runnable> tasks;
        if (exec instanceof PausableThreadPoolExecutor)
            return ((PausableThreadPoolExecutor) exec).shutdownFast();
        else {
            exec.shutdown(); // should this come at the end?
            final BlockingQueue<Runnable> queue = exec.getQueue();
            /*
             * We synchronize here on the off chance that multiple threads call this method with the same executor.
             */
            synchronized (exec) {
                tasks = new ArrayList<>(queue.size());
                PausableThreadPoolExecutor.drainFully(queue, tasks);
            }
        }
        return tasks;
    }

}