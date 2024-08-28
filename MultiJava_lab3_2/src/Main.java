import java.util.Scanner;
import java.util.concurrent.Phaser;

class SlaveThread implements Runnable {
    Phaser phaser;
    String name;
    int totalPhases;

    SlaveThread(Phaser phaser, String name, int totalPhases) {
        this.phaser = phaser;
        this.name = name;
        this.totalPhases = totalPhases;
        this.phaser.register();
    }

    @Override
    public void run() {
        for (int i = 0; i < totalPhases; i++) {
            System.out.println("Поток " + name + " начинает фазу " + (i));
            phaser.arriveAndAwaitAdvance();
            try {
                Thread.sleep(300);
            } catch (InterruptedException ex) {
                System.out.println("Ошибка: " + ex.getMessage());
            }
        }
        phaser.arriveAndDeregister();
    }
}

public class Main {
    public static void main(String args[]) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите количество фаз: ");
        int totalPhases = scanner.nextInt();
        scanner.close();

        Phaser phaser = new Phaser(1);

        System.out.println("Запуск потоков");
        new Thread(new SlaveThread(phaser, "A", totalPhases)).start();
        new Thread(new SlaveThread(phaser, "B", totalPhases)).start();
        new Thread(new SlaveThread(phaser, "C", totalPhases)).start();

        for (int phase = 0; phase < totalPhases; phase++) {
            phaser.arriveAndAwaitAdvance();
            System.out.println("Фазу " + (phase) + " завершено");
        }
        phaser.arriveAndDeregister();
        Thread.sleep(100);
        if (phaser.isTerminated()) {
            System.out.println("Синхронизатор фаз завершен");
        }
    }
}