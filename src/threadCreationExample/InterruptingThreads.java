package threadCreationExample;

import java.math.BigInteger;

/**
 * Use case for Daemon threads here will be when worker thread is not
 * under our control, and we don't want it to block our application
 * from terminating. Example, exit the main program even if
 * the LongComputation thread is running, we can set it to daemon.
 * Program will exit even if the thread is currently running.
 */
public class InterruptingThreads {

    public static void main(String[] args) {
        Thread thread = new Thread(new BlockingTask());

        thread.start();
        thread.interrupt();

        Thread thread2 = new Thread(new LongComputation(new BigInteger("24654646546"),
                new BigInteger("10465546")));

        thread2.setDaemon(true);
        thread2.start();
//        thread2.interrupt();
    }

    public static class BlockingTask implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(5000000);
            }
            catch (InterruptedException e) {
                System.out.println("Exiting blocking thread");
            }
        }
    }

    public static class LongComputation implements Runnable {

        private final BigInteger base;
        private final BigInteger power;

        public LongComputation(BigInteger base, BigInteger power) {
            this.base = base;
            this.power = power;
        }

        @Override
        public void run() {
            System.out.println(base + "^" + power + " = " + pow(base, power));
        }

        private BigInteger pow(BigInteger base, BigInteger power) {
            BigInteger result = BigInteger.ONE;
            for (BigInteger i = BigInteger.ZERO; i.compareTo(power) != 0;
                 i = i.add(BigInteger.ONE)) {
                /*if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Prematurely interrupted computation");
                    return BigInteger.ZERO;
                }*/
                result = result.multiply(base);
            }
            return result;
        }
    }

}
