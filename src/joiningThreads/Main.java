package joiningThreads;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        List<Long> inputNumbers = Arrays.asList(2L, 2342L, 23234L, 48L, 2342534L);
        List<FactorialThread> threads = inputNumbers.stream()
                .map(FactorialThread::new)
                .collect(Collectors.toList());
        threads.forEach(Thread::start);

        for (FactorialThread thread : threads) {
            try {
                thread.join(2000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < inputNumbers.size(); i++) {
            FactorialThread factorialThread = threads.get(i);
            if (factorialThread.isFinished()) {
                System.out.println("Factorial of " + inputNumbers.get(i)
                        + " is " + factorialThread.getResult());
            }
            else {
                factorialThread.interrupt();
                System.out.println("Still calculating factorial of "
                        + inputNumbers.get(i));
            }
        }

    }

    public static class FactorialThread extends Thread {

        private final long inputNumber;
        private BigInteger result;
        private boolean isFinished;

        public FactorialThread(long inputNumber) {
            this.inputNumber = inputNumber;
        }

        @Override
        public void run() {
            this.result = factorial(inputNumber);
            this.isFinished = true;
        }

        private BigInteger factorial(long inputNumber) {
            BigInteger result = BigInteger.ONE;
            for (long i = inputNumber; i > 0; i--) {
                if (this.isInterrupted()) {
                    return BigInteger.ZERO;
                }
                result = result.multiply(new BigInteger(Long.toString(i)));
            }
            return result;
        }

        public boolean isFinished() {
            return isFinished;
        }

        public BigInteger getResult() {
            return result;
        }


    }

}
