import java.util.LinkedList;
import java.util.Queue;

class Main {
    private static final int CAPACITY = 5;
    private static Queue<Integer> warehouse = new LinkedList<>();

    static class Producer extends Thread {
        @Override
        public void run() {
            int product = 0;
            while (true) {
                try {
                    produce(product++);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void produce(int item) throws InterruptedException {
            synchronized (warehouse) {
                while (warehouse.size() == CAPACITY) {
                    System.out.println("Заполнено, подождите");
                    warehouse.wait();
                }
                System.out.println("Изготовлен продукт: " + item);
                warehouse.add(item);
                warehouse.notifyAll();
                Thread.sleep(1000);
            }
        }
    }

    static class Consumer extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    consume();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void consume() throws InterruptedException {
            synchronized (warehouse) {
                while (warehouse.size() < CAPACITY) {
                    System.out.println("Пусто, подождите");
                    warehouse.wait();
                }
                while (!warehouse.isEmpty()) {
                    System.out.println("Потреблен продукт: " + warehouse.poll());
                }
                warehouse.notifyAll();
                Thread.sleep(2000);
            }
        }
    }

    public static void main(String[] args) {
        Producer producer = new Producer();
        Consumer consumer = new Consumer();

        producer.start();
        consumer.start();
    }
}