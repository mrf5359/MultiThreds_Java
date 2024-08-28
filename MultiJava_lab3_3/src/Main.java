import java.util.concurrent.Semaphore;

class Shared {
    static int count = 0;
}
class IncrementThread implements Runnable {
    String name;
    Semaphore semp;
    IncrementThread(Semaphore semp, String name) {
        this.semp = semp;
        this.name = name;
    }
    @Override
    public void run() {
        App.Start(semp, name, '+');
    }
}
class DecrementThread implements Runnable {
    String name;
    Semaphore semp;
    DecrementThread(Semaphore semaphore, String name) {
        this.semp = semaphore;
        this.name = name;
    }
    @Override
    public void run() {
        App.Start(semp, name, '-');
    }
}
class SemaphoreApp {
    public static void main(String args[]) {
        Semaphore semp = new Semaphore(1);
        IncrementThread incrementThread = new IncrementThread(semp, "A");
        new Thread(incrementThread).start();
        DecrementThread decrementThread = new DecrementThread(semp, "B");
        new Thread(decrementThread).start();
    }
}
class App {
    public static void Start(Semaphore semaphore, String name, char op) {
        System.out.println("Запуск потока " + name);
        try {
            System.out.println("Поток " + name + " ожидает разрешение");
            semaphore.acquire();
            System.out.println("Поток " + name + " получает разрешение");
            if (op == '-') {
                for (int i = 5; i > 0; i--, Shared.count--) {
                    System.out.println(name + ": " +
                            Shared.count);
                    Thread.sleep(300);
                }
            }
            else if (op == '+'){
                for (int i = 0; i < 5; i++, Shared.count++) {
                    System.out.println(name + ": " +
                            Shared.count);
                    Thread.sleep(300);
                }
            }
            else{
                System.out.println("Ошибка");
            }
        } catch (InterruptedException ex) {
            System.out.println("Ошибка: " + ex.getMessage());
        }
        System.out.println("Поток " + name + " освобождает разрешение");
        semaphore.release();
    }
}