import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
public class Main {
    private AtomicInteger result;

    public Main() {
        this.result = new AtomicInteger(0);// Исходное значение результата
    }

    public int add(int number) {
        return result.addAndGet(number);
    }

    public int subtract(int number) {
        return result.addAndGet(-number);
    }

    public int multiply(int number) {
        int curRes;
        int newRes;
        do {
            curRes = result.get();
            newRes = curRes * number;
        } while (!result.compareAndSet(curRes, newRes)); //сравнивает curRes с result и если они совпадают установить newRes для result.
        return newRes;
    }

    public int divide(int number) {
        int curRes;
        int newRes;
        do {
            curRes = result.get();
            if (number == 0)
                throw new IllegalArgumentException("Деление на ноль невозможно");
            newRes = curRes / number;
        } while (!result.compareAndSet(curRes, newRes)); // ^
        return newRes;
    }

    public int getResult() {
        return result.get();
    }

    public static void main(String[] args) {
        Main calculator = new Main();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Калькулятор");
        while (true) {
            System.out.println("Текущий результат: " + calculator.getResult());
            System.out.println("Введите операцию (+, -, *, /) или 'q' для выхода: ");
            String operation = scanner.nextLine();

            if (operation.equals("q")) {
                System.out.println("Завершение работы...");
                break;
            }

            System.out.println("Введите число: ");
            int number = scanner.nextInt();
            scanner.nextLine();

            switch (operation) {
                case "+":
                    calculator.add(number);
                    break;
                case "-":
                    calculator.subtract(number);
                    break;
                case "*":
                    calculator.multiply(number);
                    break;
                case "/":
                    try {
                        calculator.divide(number);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                    break;
                default:
                    System.out.println("Недопустимая операция!");
                    break;
            }
        }

        scanner.close();
    }
}