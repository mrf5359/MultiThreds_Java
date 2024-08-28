public class Main {
    private static volatile boolean isRunning = true;

    public static void main(String[] args) {
        Thread thread1 = new Thread(new MyRunnable(), "Поток-1");
        Thread thread2 = new Thread(new MyRunnable(), "Поток-2");
        Thread thread3 = new Thread(new MyRunnable(), "Поток-3");

        thread1.setPriority(Thread.MIN_PRIORITY);
        thread2.setPriority(Thread.NORM_PRIORITY);
        thread3.setPriority(Thread.MAX_PRIORITY);

        thread1.start();
        thread2.start();
        thread3.start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Stop threads
        isRunning = false;
    }

    static class MyRunnable implements Runnable {
        private int Value = 0;

        @Override
        public void run() {
            while (isRunning) {
                synchronized (Main.class) {
                    Value++;
                    System.out.println(Thread.currentThread().getName() + ": " + Value);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
