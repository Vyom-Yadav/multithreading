package concurrencyProblems;

import java.util.concurrent.atomic.AtomicInteger;

public class CounterProblem {

    public static void main(String[] args) throws InterruptedException {

        InventoryCounter inventoryCounter = new InventoryCounter();
        IncrementThread incrementThread = new IncrementThread(inventoryCounter);
        DecrementThread decrementThread = new DecrementThread(inventoryCounter);

        incrementThread.start();
        decrementThread.start();

        incrementThread.join();
        decrementThread.join();

        System.out.println(inventoryCounter.getItem());

    }

    public static class IncrementThread extends Thread {

        private InventoryCounter inventoryCounter;

        public IncrementThread(InventoryCounter inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
        }

        @Override
        public void run() {
            for (int i = 0; i < 1000; i++) {
                inventoryCounter.increment();
            }
        }
    }

    public static class DecrementThread extends Thread {

        private InventoryCounter inventoryCounter;

        public DecrementThread(InventoryCounter inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
        }

        @Override
        public void run() {
            for (int i = 0; i < 1000; i++) {
                inventoryCounter.decrement();
            }
        }
    }

   public static class InventoryCounter {

//       private int item;
        private final AtomicInteger items = new AtomicInteger(0);
       /*
       public synchronized void increment() {
           item++; // non atomic operation
       }

       public synchronized void decrement() {
           item--; // non atomic operation
       }
       */

//       public void increment() {
//           synchronized (this) {
//               item++; // non atomic operation
//           }
//       }
//
//       public void decrement() {
//           synchronized (this) {
//               item--; // non atomic operation
//           }
//       }

       public void increment() {
            items.incrementAndGet();
       }

       public void decrement() {
           items.decrementAndGet();
       }

       public int getItem() {
           return items.get();
       }

   }

}
