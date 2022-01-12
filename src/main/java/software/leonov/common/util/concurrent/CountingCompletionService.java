package software.leonov.common.util.concurrent;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An {@link ExecutorCompletionService} which keeps count of the number of tasks submitted and the number of tasks
 * retrieved.
 * <p>
 * The {@link #remaining()} method returns the number of remaining tasks that have completed or will complete in the
 * future. Additional {@link #shutdownFast()} and {@link #shutdownNow()} methods are provided which delegate to the
 * supplied {@code ExecutorService} instance.
 * <p>
 * As long as the supplied {@code ExecutorService} is not modified externally, all methods in this class will guarantee
 * that the task counter is incremented and decremented appropriately. For example calling {@code shutdownNow()} will
 * subtract the tasks which never commenced execution from the task counter before returning.
 * 
 * @author Zhenya Leonov
 */
final public class CountingCompletionService<V> extends ExecutorCompletionService<V> {

    private final AtomicLong remaining = new AtomicLong();
    private final ExecutorService exec;

    /**
     * Creates a new {@code CountingCompletionService} using the supplied {@code ExecutorService} for task execution and a
     * {@link LinkedBlockingQueue} as its completion queue.
     *
     * @param exec the executor service to use
     * @throws NullPointerException if executor is {@code null}
     */
    public CountingCompletionService(final ExecutorService exec) {
        super(exec);
        this.exec = exec;
    }

    /**
     * Creates a new {@code CountingCompletionService} using the supplied {@code ExecutorService} for task execution and the
     * supplied queue as its completion queue.
     *
     * @param exec  the executor service to use
     * @param queue an unbounded queue to use as the completion queue
     * @throws NullPointerException if executor or queue are {@code null}
     */
    public CountingCompletionService(final ExecutorService exec, final BlockingQueue<Future<V>> queue) {
        super(exec, queue);
        this.exec = exec;
    }

    /**
     * @throws RejectedExecutionException {@inheritDoc}
     * @throws NullPointerException       {@inheritDoc}
     */
    @Override
    public Future<V> submit(final Callable<V> task) {
        final Future<V> future = super.submit(task);
        remaining.incrementAndGet();
        return future;
    }

    /**
     * Submits a {@code Runnable} task for execution and returns a Future representing that task. The Future's {@code get}
     * method will return {@code null} upon <i>successful</i> completion.
     *
     * @param task the task to submit
     * @return a Future representing pending completion of the task
     * @throws RejectedExecutionException if the task cannot be scheduled for execution
     * @throws NullPointerException       if the task is {@code null}
     */
    public Future<?> submit(final Runnable task) {
        return submit(task, null);
    }

    /**
     * @throws RejectedExecutionException {@inheritDoc}
     * @throws NullPointerException       {@inheritDoc}
     */
    @Override
    public Future<V> submit(final Runnable task, final V result) {
        if (task == null)
            throw new NullPointerException("task == null");
        final Future<V> future = super.submit(task, result);
        remaining.incrementAndGet();
        return future;
    }

    @Override
    public Future<V> take() throws InterruptedException {
        final Future<V> future = super.take();
        remaining.decrementAndGet();
        return future;
    }

    @Override
    public Future<V> poll() {
        final Future<V> future = super.poll();
        if (future != null)
            remaining.decrementAndGet();
        return future;
    }

    @Override
    public Future<V> poll(final long timeout, final TimeUnit unit) throws InterruptedException {
        if (unit == null)
            throw new NullPointerException("unit == null");
        final Future<V> future = super.poll(timeout, unit);
        if (future != null)
            remaining.decrementAndGet();
        return future;
    }

    /**
     * Returns the number of tasks submitted but not yet retrieved via {@link #poll()}, {@link #poll(long, TimeUnit)}, or
     * {@link #take()}.
     * 
     * @return the number of tasks submitted but not yet retrieved
     */
    public long remaining() {
        return remaining.get();
    }

    /**
     * Attempts to stop all actively executing tasks, halts the processing of waiting tasks, and returns a list of the tasks
     * that were awaiting execution.
     * <p>
     * Delegates to {@link ExecutorService#shutdownNow() shutdownNow()} on the supplied {@code ExecutorService}. The tasks
     * which never commenced execution will be subtracted from the from the {@link #remaining()} task counter.
     *
     * @return list of tasks that never commenced execution
     * @throws SecurityException if a security manager exists and shutting down this ExecutorService may manipulate threads
     *                           that the caller is not permitted to modify because it does not hold
     *                           {@link java.lang.RuntimePermission}{@code ("modifyThread")}, or the security manager's
     *                           {@code checkAccess} method denies access.
     */
    public List<Runnable> shutdownNow() {
        final List<Runnable> tasks = exec.shutdownNow();
        remaining.addAndGet(-tasks.size());
        return tasks;
    }

    /**
     * Stops the processing of pending tasks but does not attempt to stop actively executing tasks.
     * <p>
     * Delegates to {@link ExecutorServices#shutdownFast(ThreadPoolExecutor)} if the supplied {@code ExecutorService} is an
     * {@code instanceof} of either {@code ThreadPoolExecutor}, or else throws an {@code UnsupportedOperationException}. The
     * tasks which never commenced execution will be subtracted from the from the {@link #remaining()} task counter.
     * 
     * @return list of tasks that never commenced execution
     * @throws UnsupportedOperationException if the {@code ExecutorService} supplied at creation is not an
     *                                       {@code instanceof} or {@code ThreadPoolExecutor}
     */
    public List<Runnable> shutdownFast() {
        final List<Runnable> tasks;
        if (exec instanceof ThreadPoolExecutor)
            tasks = ExecutorServices.shutdownFast((ThreadPoolExecutor) exec);
        else
            throw new UnsupportedOperationException();

        remaining.addAndGet(-tasks.size());
        return tasks;
    }

}