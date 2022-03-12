package ThreadSafeDataStructure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class Main {

    public static void main(String[] args) throws InterruptedException {
//        StandardStack<Integer> stack = new StandardStack<>();
        LockFreeStack<Integer> stack = new LockFreeStack<>();
        Random random = new Random();

        for (int i = 0; i < 10000; i++) {
            stack.push(random.nextInt());
        }

        List<Thread> threads = new ArrayList<>();

        int pushingThreads = 2;
        int poppingThreads = 2;

        for (int i = 0; i < pushingThreads; i++) {
            Thread thread = new Thread(() -> {
                while (true) {
                    stack.push(random.nextInt());
                }
            });
            thread.setDaemon(true);
            threads.add(thread);
        }

        for (int i = 0; i < poppingThreads; i++) {
            Thread thread = new Thread(() -> {
                while (true) {
                    stack.pop();
                }
            });
            thread.setDaemon(true);
            threads.add(thread);
        }

        threads.forEach(Thread::start);

        Thread.sleep(1000);

        System.out.println(stack.getCounter());

    }

    public static class LockFreeStack<T> {
        private final AtomicReference<StackNode<T>> head = new AtomicReference<>();
        private final AtomicInteger count = new AtomicInteger(1);

        public void push(T value) {
            StackNode<T> newHead = new StackNode<>(value);
            count.incrementAndGet();
            if (head.get() == null) {
                head.set(newHead);
                return;
            }
            while (true) {
                StackNode<T> currentHeadNode = head.get();
                newHead.next = currentHeadNode;
                if (head.compareAndSet(currentHeadNode, newHead)) {
                    break;
                }
                else {
                    LockSupport.parkNanos(1);
                }
            }
        }

        public T pop() {
            StackNode<T> currentHead = head.get();
            StackNode<T> newHeadNode;

            count.incrementAndGet();
            while (currentHead != null) {
                newHeadNode = currentHead.next;
                if (head.compareAndSet(currentHead, newHeadNode)) {
                    break;
                }
                else {
                    LockSupport.parkNanos(1);
                    currentHead = head.get();
                }
            }

            return currentHead != null ? currentHead.value : null;
        }

        public int getCounter() {
            return count.get();
        }

    }

    public static class StandardStack<T> {
        private StackNode<T> head;
        private int counter;

        public synchronized void push(T value) {
            StackNode<T> newHead = new StackNode<>(value);
            counter++;
            if (head == null) {
                head = newHead;
                return;
            }
            newHead.next = head;
            head = newHead;
        }

        public synchronized T pop() {
            counter++;
            if (head == null) {
                return null;
            }
            T value = head.value;
            head = head.next;
            return value;
        }

        public int getCounter() {
            return counter;
        }
    }

    public static class StackNode<T> {
        public T value;
        public StackNode<T> next;

        public StackNode(T value) {
            this.value = value;
            next = null;
        }
    }

}
