import java.util.concurrent.Semaphore;

class Shared {
    static int[] buffer = new int[5];
    static int count = 0;
}

class Producer implements Runnable {
    Semaphore sem;

    Producer(Semaphore sem) {
        this.sem = sem;
    }

    @Override
    public void run() {
        System.out.println("Начало поставки");
        try {
            for (int i = 99; i > 0; i--) {
                sem.acquire();
                if (Shared.count < Shared.buffer.length) {
                    Shared.buffer[Shared.count] = 1;
                    System.out.println("Поставщик добавил элемент в буфер. Количество элементов в буфере: " + (Shared.count + 1));
                    Shared.count++;
                } else {
                    System.out.println("Поставщик не может добавить элемент, буфер полон.");
                }
                sem.release();
                Thread.sleep((long) (Math.random() * 300));
            }
        } catch (InterruptedException ex) {
            System.out.println("Ошибка: " + ex.getMessage());
        }
    }
}

class Consumer implements Runnable {
    Semaphore sem;

    Consumer(Semaphore sem) {
        this.sem = sem;
    }

    @Override
    public void run() {
        System.out.println("Начало потребления");
        try {
            for (int i = 99; i > 0; i--) {
                sem.acquire();
                if (Shared.count > 0) {
                    System.out.println("Потребитель взял элемент из буфера. Количество элементов в буфере: " + (Shared.count - 1));
                    Shared.count--;
                } else {
                    System.out.println("Потребитель не может взять элемент, пустой буфер.");
                }
                sem.release();
                Thread.sleep((long) (Math.random() * 300));
            }
        } catch (InterruptedException ex) {
            System.out.println("Ошибка: " + ex.getMessage());
        }
    }
}

class SemaphoreApp {
    public static void main(String[] args) {
        String str=new String("Java");
        int i=1;
        char j=3;
        System.out.println(str.substring(i,j));
    }
}
