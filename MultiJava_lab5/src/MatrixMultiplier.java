import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;

public class MatrixMultiplier extends RecursiveTask<int[][]> {
    private int[][] matrix;
    private int scalar;
    private int threshold;

    public MatrixMultiplier(int[][] matrix, int scalar, int threshold) {
        this.matrix = matrix;
        this.scalar = scalar;
        this.threshold = threshold;
    }

    @Override
    protected int[][] compute() {
        int rows = matrix.length;
        int columns = matrix[0].length;

        if (rows * columns <= threshold) {
            // Если размер матрицы меньше порогового значения, выполнить вычисление последовательно
            return computeSequentially();
        } else {
            // Разбить матрицу на подматрицы и выполнить умножение рекурсивно
            int midpoint = rows / 2;
            MatrixMultiplier topLeft = new MatrixMultiplier(
                    subMatrix(matrix, 0, 0, midpoint, columns), scalar, threshold);
            MatrixMultiplier bottomRight = new MatrixMultiplier(
                    subMatrix(matrix, midpoint, 0, rows - midpoint, columns), scalar, threshold);

            topLeft.fork(); // Выполнить умножение верхней левой подматрицы в отдельном потоке
            int[][] bottomRightResult = bottomRight.compute(); // Вычислить результат для нижней правой подматрицы
            int[][] topLeftResult = topLeft.join(); // Получить результат для верхней левой подматрицы

            return joinMatrices(topLeftResult, bottomRightResult); // Объединить результаты
        }
    }

    private int[][] computeSequentially() {
        int rows = matrix.length;
        int columns = matrix[0].length;
        int[][] result = new int[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result[i][j] = matrix[i][j] * scalar; // Умножить каждый элемент матрицы на скаляр
            }
        }
        return result;
    }

    private int[][] subMatrix(int[][] matrix, int startRow, int startColumn, int rows, int columns) {
        int[][] result = new int[rows][columns];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(matrix[startRow + i], startColumn, result[i], 0, columns);
        }
        return result;
    }

    private int[][] joinMatrices(int[][] matrix1, int[][] matrix2) {
        int rows = matrix1.length;
        int columns = matrix1[0].length;
        int[][] result = new int[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result[i][j] = matrix1[i][j] + matrix2[i][j]; // Сложить соответствующие элементы матриц
            }
        }
        return result;
    }

    public static void main(String[] args) {
        int[][] matrix = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        int scalar = 2;
        int threshold = 3; // Пороговое значение для определения, когда выполнять вычисления последовательно

        ForkJoinPool pool = new ForkJoinPool();
        MatrixMultiplier multiplier = new MatrixMultiplier(matrix, scalar, threshold);
        int[][] result = pool.invoke(multiplier); // Выполнить вычисления с помощью Fork/Join Framework

        // Вывести результат умножения матрицы на скаляр
        for (int[] row : result) {
            for (int element : row) {
                System.out.print(element + " ");
            }
            System.out.println();
        }
    }
}
// Кароче ответ получается как-то так: {1, 2, 3}, {4, 5, 6}, {7, 8, 9}*2 -> 2 + 4 + 6 + 8 + 10 + 12 + 14 + 16 + 18 = 24 + 30 + 36