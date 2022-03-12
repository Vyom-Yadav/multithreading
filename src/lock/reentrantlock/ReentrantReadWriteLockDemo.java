package lock.reentrantlock;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReentrantReadWriteLockDemo {

    public static final int HIGHEST_PRICE = 1000;
    private static final Random RANDOM = new Random();

    public static void main(String[] args) throws InterruptedException {
        InventoryDatabase inventoryDatabase = new InventoryDatabase();
        for (int i = 0; i < 100000; i++) {
            inventoryDatabase.addItem(RANDOM.nextInt(HIGHEST_PRICE));
        }

        Thread writer = new Thread(() -> {
            while (true) {
                inventoryDatabase.addItem(RANDOM.nextInt(HIGHEST_PRICE));
                inventoryDatabase.removeItem(RANDOM.nextInt(HIGHEST_PRICE));
                try {
                    Thread.sleep(10);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
//                    Thread.currentThread().interrupt();
                }
            }
        });

        writer.setDaemon(true);
        writer.start();

        int numberOfThreads = 7;
        final List<Thread> readers = new ArrayList<>();
        for (int i = 0; i < numberOfThreads; i++) {
            Thread reader = new Thread(() -> {
                for (int j = 0; j < 100000; j++) {
                    int upperBoundPrice = RANDOM.nextInt(HIGHEST_PRICE);
                    int lowerBoundPrice = upperBoundPrice > 0 ? RANDOM.nextInt(upperBoundPrice) : 0;
                    inventoryDatabase.getNumberOfItemsInPriceRange(lowerBoundPrice,
                            upperBoundPrice);
                }
            });
            reader.setDaemon(true);
            readers.add(reader);
        }

        long start = System.currentTimeMillis();
        readers.forEach(Thread::start);
        for (Thread reader : readers) {
            reader.join();
        }
        long end = System.currentTimeMillis();

        System.out.println(end - start);

    }
}

class InventoryDatabase {
    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private ReentrantLock lock = new ReentrantLock();
    private final TreeMap<Integer, Integer> priceToCountMap = new TreeMap<>();

    private Lock readLock = reentrantReadWriteLock.readLock();
    private Lock writeLock = reentrantReadWriteLock.writeLock();


    public int getNumberOfItemsInPriceRange(int lowerBound, int upperBound) {
        readLock.lock();
        try {
            Integer fromKey = priceToCountMap.ceilingKey(lowerBound);

            Integer toKey = priceToCountMap.floorKey(upperBound);

            if (fromKey == null || toKey == null) {
                return 0;
            }

            NavigableMap<Integer, Integer> rangeOfPrices = priceToCountMap.subMap(fromKey, true,
                    toKey,
                    true);
            return rangeOfPrices.values()
                    .stream()
                    .mapToInt(value -> value)
                    .sum();
        }
        finally {
            readLock.unlock();
        }
    }

    public void addItem(int price) {
        writeLock.lock();
        try {
            priceToCountMap.merge(price, 1, Integer::sum);
        }
        finally {
            writeLock.unlock();
        }
    }

    public void removeItem(int price) {
        writeLock.lock();
        try {
            Integer numberOfItemsForPrice = priceToCountMap.get(price);
            if (numberOfItemsForPrice == null || numberOfItemsForPrice == 1) {
                priceToCountMap.remove(price);
            }
            else {
                priceToCountMap.put(price, numberOfItemsForPrice - 1);
            }
        }
        finally {
            writeLock.unlock();
        }
    }
}
