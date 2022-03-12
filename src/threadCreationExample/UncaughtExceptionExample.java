package threadCreationExample;

public class UncaughtExceptionExample {

    public static void main(String[] args) {

        Thread thread = new Thread(() -> {
            System.out.println("Inside new thread");
            throw new RuntimeException();
        });

        thread.setUncaughtExceptionHandler((Thread t, Throwable e) ->
                System.out.println(
                        "An Intentional uncaught exception: " + "\"" + e.getMessage() + "\"" + " "
                                + "on thread " + t.getName()));

        thread.start();
    }

}
