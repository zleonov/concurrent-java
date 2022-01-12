package software.leonov.common.util.concurrent;

import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class CountingCompletionServiceTest {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        FluentThreadPoolExecutor exec = FluentThreadPoolExecutor.create(1);
        CountingCompletionService<BigInteger> ccs = new CountingCompletionService<>(exec);

        ccs.submit(() -> {
            System.out.println(Thread.currentThread().getName() + ": (Task 1) calculating next propable prime");
            BigInteger i = new BigInteger(5000, 1, new Random());
            if (true)
                throw new RuntimeException("TestException");
            System.out.println(Thread.currentThread().getName() + ": finished (Task 1) ");
            return i;
        });

        ccs.submit(() -> {
            System.out.println(Thread.currentThread().getName() + ": (Task 2) calculating next propable prime");
            BigInteger i = new BigInteger(5000, 1, new Random());
            System.out.println(Thread.currentThread().getName() + ": finished (Task 2) ");
            return i;
        });

        ccs.submit(() -> {
            System.out.println(Thread.currentThread().getName() + ": (Task 3) calculating next propable prime");
            BigInteger i = new BigInteger(5000, 1, new Random());
            System.out.println(Thread.currentThread().getName() + ": finished (Task 3)");
            return i;
        });

//        Thread t = Thread.currentThread();
//        new Thread(() -> {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            t.interrupt();
//        }).start();

        // Future<BigInteger> f = null;

        while (ccs.remaining() > 0) {
            Future<BigInteger> f = ccs.take();
            f.get();

        }

        System.out.println("Shutting down");
        exec.shutdownNow();

    }

}
