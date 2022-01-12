package software.leonov.common.util.concurrent;

/**
 * An unchecked version of {@link InterruptedException}. This class is intended to be used in methods whose signatures
 * prevent them from throwing {@code InterruptedException}s because of a super class or interface.
 * <p>
 * It is highly encouraged to set the {@link Thread#currentThread() current thread's} {@link Thread#interrupt()
 * interrupt} flag before throwing this exception. Doing so will preserve the interruption status of the thread
 * <p>
 * Typical use case:
 * 
 * <pre>
 * public void methodWhichCannotThrowInterruptedException() {
 *    try {
 *       methodWhichThrowsInterruptedException();
 *    } catch (final InterruptedException e) {
 *       Thread.currentThread().interrupt();
 *       throw new RuntimeInterruptedException(e);
 *    }
 * }
 * </pre>
 * 
 * Furthermore, if your code is the consumer of this method you may wish to reset the thread's interrupted flag and
 * propagate the original {@code InterruptedException}:
 * <p>
 * <pre>
 * public void myMethod() throws InterruptedException {
 *    try {
 *       methodWhichCannotThrowInterruptedException();
 *    } catch (final RuntimeInterruptedException e) {
 *       Thread.interrupted()
 *       throw e.getCause();
 *    }
 * }
 * </pre>
 */
public class RuntimeInterruptedException extends RuntimeException {

    /**
     * Default serial version ID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new {@code RuntimeInterruptedException}.
     */
    public RuntimeInterruptedException() {
        super();
    }

    /**
     * Creates a new {@code RuntimeInterruptedException} with the specified detail message. The cause is not initialized,
     * and may subsequently be initialized by a call to {@link #initCause}.
     *
     * @param message the detail message which can later be retrieved via {@link #getMessage()}
     */
    public RuntimeInterruptedException(final String message) {
        super(message);
    }

    /**
     * Creates a new {@code RuntimeInterruptedException} and sets the cause to the original {@code InterruptedException}.
     *
     * @param cause the original {@code InterruptedException} or {@code null}
     */
    public RuntimeInterruptedException(final InterruptedException cause) {
        super(cause);
    }

    /**
     * Creates a new {@code RuntimeInterruptedException} with the specified cause (the original
     * {@code InterruptedException}) and a detail message.
     *
     * @param message the detail message which can later be retrieved via {@link #getMessage()}
     * @param cause   the original {@code InterruptedException} or {@code null}
     */
    public RuntimeInterruptedException(final String message, final InterruptedException cause) {
        super(message, cause);
    }

    /**
     * Returns the original {@code InterruptedException} or {@code null}.
     * 
     * @return the original {@code InterruptedException}
     */
    @Override
    public InterruptedException getCause() {
        return (InterruptedException) super.getCause();
    }

}