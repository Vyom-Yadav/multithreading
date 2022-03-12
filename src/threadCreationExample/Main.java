package threadCreationExample;

public class Main {

    public static void main(String[] args) {

//        Thread thread = new Thread(() -> {
//            System.out.println("We are in thread: " + Thread.currentThread().getName());
//            System.out.println("Priority: " + Thread.currentThread().getPriority());
//        });
        Thread thread = new NewThread();
        thread.setName("Worker Thread");
        thread.setPriority(Thread.MAX_PRIORITY);

        System.out.println("Current thread: " + Thread.currentThread().getName());
        thread.start();
        System.out.println("Current thread: " + Thread.currentThread().getName());

        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static class NewThread extends Thread {
        @Override
        public void run() {
            System.out.println("In new Thread " + Thread.currentThread().getName());
        }
    }


}
