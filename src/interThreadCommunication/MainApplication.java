package interThreadCommunication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringJoiner;

public class MainApplication {

    private static final String INPUT_FILE = "./src/resources/matrices";
    private static final String OUTPUT_FILE = "./src/resources/matricesMultiplied";
    private static final int N = 10;

    public static void main(String[] args) throws IOException {
        ThreadSafeQueue threadSafeQueue = new ThreadSafeQueue();

        MatricesReaderProducer matricesReaderProducer = new MatricesReaderProducer(threadSafeQueue);
        MatricesMultiplierConsumer matricesMultiplierConsumer = new MatricesMultiplierConsumer(
                threadSafeQueue);

        matricesMultiplierConsumer.start();
        matricesReaderProducer.start();
    }

    private static class MatricesMultiplierConsumer extends Thread {
        private final ThreadSafeQueue threadSafeQueue;
        private final BufferedWriter bufferedWriter;

        public MatricesMultiplierConsumer(
                ThreadSafeQueue threadSafeQueue) throws IOException {
            this.threadSafeQueue = threadSafeQueue;
            bufferedWriter = new BufferedWriter(new FileWriter(OUTPUT_FILE));
        }

        private float[][] multiplyMatrices(MatricesPair matricesPair) {
            float[][] m1 = matricesPair.matrix1;
            float[][] m2 = matricesPair.matrix2;
            float[][] resultMatrices = new float[N][N];
            for (int r = 0; r < N; r++) {
                for (int c = 0; c < N; c++) {
                    for (int k = 0; k < N; k++) {
                        resultMatrices[r][c] += m1[r][k] * m2[k][c];
                    }
                }
            }
            return resultMatrices;
        }

        @Override
        public void run() {
            while (true) {
                MatricesPair matricesPair = threadSafeQueue.remove();
                if (matricesPair == null) {
                    System.out.println("No more matrices to consume, consumer is terminating");
                    break;
                }
                float[][] result = multiplyMatrices(matricesPair);
                try {
                    saveMatrixToFile(bufferedWriter, result);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                bufferedWriter.flush();
                bufferedWriter.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void saveMatrixToFile(BufferedWriter bufferedWriter,
                float[][] result) throws IOException {
            for (int r = 0; r < N; r++) {
                StringJoiner stringJoiner = new StringJoiner(",");
                for (int c = 0; c < N; c++) {
                    stringJoiner.add(String.format("%.2f", result[r][c]));
                }
                bufferedWriter.write(stringJoiner.toString());
                bufferedWriter.write("\n");
            }
            bufferedWriter.write("\n");
        }
    }

    private static class MatricesReaderProducer extends Thread {
        private final BufferedReader bufferedReader;
        private final ThreadSafeQueue threadSafeQueue;

        public MatricesReaderProducer(
                ThreadSafeQueue threadSafeQueue) throws FileNotFoundException {
            this.threadSafeQueue = threadSafeQueue;
            bufferedReader = new BufferedReader(new FileReader(INPUT_FILE));
        }

        private float[][] readMatrix() throws IOException {
            float[][] matrix = new float[N][N];
            for (int i = 0; i < N; i++) {
                String line = bufferedReader.readLine();
                if (line != null) {
                    String[] nums = line.split(", ");
                    for (int i1 = 0; i1 < N; i1++) {
                        float obj = Float.parseFloat(nums[i1]);
                        matrix[i][i1] = obj;
                    }
                }
                else {
                    bufferedReader.readLine();
                    return null;
                }
            }
            bufferedReader.readLine();
            return matrix;
        }

        @Override
        public void run() {
            while (true) {
                float[][] matrix1 = null;
                float[][] matrix2 = null;

                try {
                    matrix1 = readMatrix();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    matrix2 = readMatrix();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                if (matrix1 == null || matrix2 == null) {
                    threadSafeQueue.terminate();
                    System.out.println("No More matrices to read, producer thread is terminating");
                    return;
                }

                MatricesPair matricesPair = new MatricesPair();
                matricesPair.matrix1 = matrix1;
                matricesPair.matrix2 = matrix2;

                threadSafeQueue.add(matricesPair);

            }
        }
    }

    private static class ThreadSafeQueue {
        private final Queue<MatricesPair> queue = new LinkedList<>();
        private boolean isEmpty = true;
        private boolean isTerminated = false;
        private static final int CAPACITY = 5;

        public synchronized void add(MatricesPair matricesPair) {
            while (queue.size() == CAPACITY) {
                try {
                    wait();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queue.add(matricesPair);
            isEmpty = false;
            notify();
        }

        public synchronized MatricesPair remove() {
            MatricesPair matricesPair;
            while (isEmpty && !isTerminated) {
                try {
                    wait();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (queue.size() == 1) {
                isEmpty = true;
            }

            if (queue.isEmpty() && isTerminated) {
                return null;
            }
            System.out.println("queue size " + queue.size());
            matricesPair  = queue.remove();
            if (queue.size() == CAPACITY - 1) {
                notifyAll();
            }
            return matricesPair;
        }

        public synchronized void terminate() {
            isTerminated = true;
            notifyAll();
        }
    }

    private static class MatricesPair {
        float[][] matrix1;
        float[][] matrix2;
    }
}

