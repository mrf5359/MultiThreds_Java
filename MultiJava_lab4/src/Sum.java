import java.util.concurrent.*;

class Sum implements Callable<Integer> {
    int value;
    Sum(int value) {
        this.value = value;
    }
    @Override
    public Integer call() {
        int sum = 0, i = 1;
        while (i <= value) {
            sum += i++;
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return sum;
    }
}
class Hypotenuse implements Callable<Double> {
    double sideA, sideB;
    Hypotenuse(double a, double b) {
        sideA = a;
        sideB = b;
    }
    @Override
    public Double call() {
        return Math.sqrt(Math.pow(sideA, 2) + Math.pow(sideB, 2));
    }
}
class Factorial implements Callable<Integer> {
    int value;
    Factorial(int value) {
        this.value = value;
    }
    @Override
    public Integer call() {
        int fact = 1, i = 2;
        while (i <= value) {
            fact *= i++;
        }
        return fact;
    }
}
class Fibonacci implements Callable<Integer>{
    int value;
    Fibonacci(int value) {this.value = value;}
    @Override
    public Integer call(){
        int fib1 = 0;
        int fib2 = 1;
        int fib = 0;

        if (value == 0) return fib1;
        if (value == 1) return fib2;

        for (int i = 2; i <= value; i++) {
            fib = fib1 + fib2;
            fib1 = fib2;
            fib2 = fib;
        }

        return fib;
    }
}
class CallableApp {
    public static void main(String args[]) {
        ExecutorService service = Executors.newFixedThreadPool(3);
        Future<Integer> future1;
        Future<Double> future2;
        Future<Integer> future3;
        Future<Integer> future4;
        System.out.println("Запуск вычислений");
        future1 = service.submit(new Sum(120));
        future2 = service.submit(new Hypotenuse(50, 90));
        future3 = service.submit(new Factorial(5));
        future4 = service.submit(new Fibonacci(18));
        try {

            System.out.println("Сумма чисел: " + future1.get(1000000, TimeUnit.MICROSECONDS));
            System.out.println("Гипотенуза: " + future2.get(1000000, TimeUnit.MICROSECONDS));
            System.out.println("Факториал: " + future3.get(1000000, TimeUnit.MICROSECONDS));
            System.out.println("Числа фибоначчи: " + future4.get(1000000, TimeUnit.MICROSECONDS));
        } catch (ExecutionException | InterruptedException | TimeoutException ex) {
            System.out.println("Ошибка: " + ex.getMessage());
        }
        service.shutdown();
        System.out.println("Завершение вычислений");
    }
}