package software.leonov.common.util.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;

import com.google.common.util.concurrent.FluentFuture;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * An {@link PausableThreadPoolExecutor} which returns {@link FluentFuture}s from its {@code submit} methods.
 * <p>
 * Requires the optional <a href="https://github.com/google/guava" target="_blank">Guava</a> dependency. See the Guava
 * User Guide article on
 * <a href="https://github.com/google/guava/wiki/ListenableFutureExplained" target="_blank">ListenableFuture</a>.
 * 
 * @author Zhenya Leonov
 */
public final class FluentThreadPoolExecutor extends PausableThreadPoolExecutor implements ListeningExecutorService {

    /**
     * Creates a new {@code FluentThreadPoolExecutor} with the given fixed number of threads, unbounded work queue, default
     * thread factory, and the {@link java.util.concurrent.ThreadPoolExecutor.AbortPolicy AbortPolicy} handler.
     *
     * @param corePoolSize the number of threads to keep in the pool
     * @return a new {@code FluentThreadPoolExecutor} with the given fixed number of threads, unbounded work queue, default
     *         thread factory, and the {@link java.util.concurrent.ThreadPoolExecutor.AbortPolicy AbortPolicy} handler
     * @throws IllegalArgumentException if {@code corePoolSize} < 1
     * 
     */
    public static FluentThreadPoolExecutor create(final int corePoolSize) {
        return new FluentThreadPoolExecutor(corePoolSize, new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(), new AbortPolicy());
    }

    /**
     * Creates a new {@code FluentThreadPoolExecutor} with the given fixed number of threads, the specified work queue,
     * default thread factory, and the {@link java.util.concurrent.ThreadPoolExecutor.AbortPolicy AbortPolicy} handler.
     *
     * @param corePoolSize the number of threads to keep in the pool
     * @param queue        the queue to use for holding tasks before they are executed
     * @return a new {@code FluentThreadPoolExecutor} with the given fixed number of threads, the specified work queue,
     *         default thread factory, and the {@link java.util.concurrent.ThreadPoolExecutor.AbortPolicy AbortPolicy}
     *         handler
     * @throws IllegalArgumentException if {@code corePoolSize} < 1
     */
    public static FluentThreadPoolExecutor create(final int corePoolSize, final BlockingQueue<Runnable> queue) {
        return new FluentThreadPoolExecutor(corePoolSize, queue, Executors.defaultThreadFactory(), new AbortPolicy());
    }

    /**
     * Creates a new {@code FluentThreadPoolExecutor} with the given fixed number of threads, the specified work queue,
     * thread factory, and the {@link java.util.concurrent.ThreadPoolExecutor.AbortPolicy AbortPolicy} handler.
     *
     * @param corePoolSize the number of threads to keep in the pool
     * @param queue        the queue to use for holding tasks before they are executed
     * @param factory      the factory used to create new threads
     * @return a new {@code FluentThreadPoolExecutor} with the given fixed number of threads, the specified work queue,
     *         thread factory, and the {@link java.util.concurrent.ThreadPoolExecutor.AbortPolicy AbortPolicy} handler
     * @throws IllegalArgumentException if {@code corePoolSize} < 1
     */
    public static FluentThreadPoolExecutor create(final int corePoolSize, final BlockingQueue<Runnable> queue, final ThreadFactory factory) {
        return new FluentThreadPoolExecutor(corePoolSize, queue, factory, new AbortPolicy());
    }

    /**
     * Creates a new {@code FluentThreadPoolExecutor} with the given fixed number of threads, the specified work queue,
     * thread factory, and {@code RejectedExecutionHandler}.
     *
     * @param corePoolSize the number of threads to keep in the pool
     * @param queue        the queue to use for holding tasks before they are executed
     * @param factory      the factory used to create new threads
     * @param handler      the handler to use when execution is blocked because the thread bounds and queue capacities are
     *                     reached
     * @return a new {@code FluentThreadPoolExecutor} with the given fixed number of threads, the specified work queue,
     *         thread factory, and {@code RejectedExecutionHandler}
     * @throws IllegalArgumentException if {@code corePoolSize} < 1
     */
    public static FluentThreadPoolExecutor create(final int corePoolSize, final BlockingQueue<Runnable> queue, final ThreadFactory factory, final RejectedExecutionHandler handler) {
        return new FluentThreadPoolExecutor(corePoolSize, queue, factory, handler);
    }

    private FluentThreadPoolExecutor(final int corePoolSize, final BlockingQueue<Runnable> queue, final ThreadFactory factory, final RejectedExecutionHandler handler) {
        super(corePoolSize, queue, factory, handler);
    }

    @Override
    protected final <T> RunnableFuture<T> newTaskFor(final Runnable runnable, final T value) {
        return ListenableFutureTask.create(runnable, value);
    }

    @Override
    protected final <T> RunnableFuture<T> newTaskFor(final Callable<T> callable) {
        return ListenableFutureTask.create(callable);
    }

    @Override
    public FluentFuture<?> submit(final Runnable task) {
        return FluentFuture.from((ListenableFuture<?>) super.submit(task));
    }

    @Override
    public <T> FluentFuture<T> submit(final Runnable task, final T result) {
        return FluentFuture.from((ListenableFuture<T>) super.submit(task, result));
    }

    @Override
    public <T> FluentFuture<T> submit(final Callable<T> task) {
        return FluentFuture.from((ListenableFuture<T>) super.submit(task));
    }

}