package software.leonov.common.util.concurrent;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * Static utility methods for creating and adding to {@link FutureCallback}s to {@link ListenableFuture}s.
 * <p>
 * The intention of this class is to provide convenience methods that allow users to create and attach
 * {@code FutureCallback}s using method references or lambda expressions, reflecting the new developments in Java 8.
 * <p>
 * Requires the optional <a href="https://github.com/google/guava" target="_blank">Guava</a> dependency. See the Guava
 * User Guide article on
 * <a href="https://github.com/google/guava/wiki/ListenableFutureExplained" target="_blank">ListenableFuture</a>.
 * 
 * @author Zhenya Leonov
 */
public final class MoreFutures {

    private MoreFutures() {
    }

    /**
     * A convenience method that calls {@link Futures#addCallback(ListenableFuture, FutureCallback, Executor)
     * Futures.addCallback(future, callback, directExecutor())} and returns the specified future for method chaining.
     * <p>
     * <b>Warning</b>: Using {@link MoreExecutors#directExecutor()} may be a dangerous choice if the callback is not fast
     * and lightweight. See {@link ListenableFuture#addListener ListenableFuture.addListener} documentation for more
     * information.
     * 
     * @param future   the specified future
     * @param callback the callback to register
     * @return the specified future
     */
    public static <V> ListenableFuture<V> addCallback(final ListenableFuture<V> future, final FutureCallback<? super V> callback) {
        checkNotNull(future, "future == null");
        checkNotNull(callback, "callback == null");
        Futures.addCallback(future, callback, directExecutor());
        return future;

    }

    /**
     * Returns a {@code FutureCallback} which invokes the specified consumers on {@link FutureCallback#onSuccess(Object)
     * success} and {@link FutureCallback#onFailure(Throwable) failure} respectively.
     * 
     * @param success the consumer to invoke if the future succeeds
     * @param failure the consumer to invoke if the future fails or is cancelled
     * @return a {@code FutureCallback} which invokes the specified consumers on {@link FutureCallback#onSuccess(Object)
     *         success} and {@link FutureCallback#onFailure(Throwable) failure}
     * @return a {@code FutureCallback} which invokes the specified consumers on {@link FutureCallback#onSuccess(Object)
     *         success} and {@link FutureCallback#onFailure(Throwable) failure} respectively
     */
    public static <V> FutureCallback<? extends V> newCallback(final Consumer<? super V> success, final Consumer<Throwable> failure) {
        checkNotNull(success, "success == null");
        checkNotNull(failure, "failure == null");
        return new FutureCallback<V>() {

            @Override
            public void onFailure(final Throwable t) {
                failure.accept(t);
            }

            @Override
            public void onSuccess(final V result) {
                success.accept(result);
            }
        };
    }

    /**
     * Returns a {@code FutureCallback} which does nothing {@link FutureCallback#onSuccess(Object) on success} and invokes
     * the specified {@code consumer} if the future {@link FutureCallback#onFailure(Throwable) fails} or is cancelled.
     * <p>
     * Shorthand for creating a {@code FutureCallback} for {@code Runnable} tasks which return nothing.
     * 
     * @param failure the consumer to invoke if the {@code future} fails or is cancelled
     * @return a {@code FutureCallback} which does nothing {@link FutureCallback#onSuccess(Object) on success} and invokes
     *         the specified {@code consumer} if the future {@link FutureCallback#onFailure(Throwable) fails} or is
     *         cancelled
     */
    public static <V> FutureCallback<V> onFailure(final Consumer<Throwable> failure) {
        checkNotNull(failure, "failure == null");
        return new FutureCallback<V>() {

            @Override
            public void onFailure(final Throwable t) {
                failure.accept(t);
            }

            @Override
            public void onSuccess(final V result) {
            }
        };
    }

}