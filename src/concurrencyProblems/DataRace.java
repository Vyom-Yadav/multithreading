package concurrencyProblems;

public class DataRace {

    volatile int x = 0;
    volatile int y = 0;

    public void increment() {
        x++;
        y++;
    }

    public void check() {
        if (y > x) {
            System.out.println("BAD!!!");
        }
    }

    public static class incrementThread extends Thread {

        private final DataRace dataRace;

        @Override
        public void run() {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                dataRace.increment();
            }
        }

        public incrementThread(DataRace dataRace) {
            this.dataRace = dataRace;
        }
    }

    public static class checkThread extends Thread {

        private final DataRace dataRace;

        @Override
        public void run() {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                dataRace.check();
            }
        }

        public checkThread(DataRace dataRace) {
            this.dataRace = dataRace;
        }
    }

    public static void main(String[] args) {
        DataRace obj = new DataRace();
        incrementThread incrementThread = new incrementThread(obj);
        checkThread checkThread = new checkThread(obj);

        incrementThread.start();
        checkThread.start();
    }

}
