import java.util.concurrent.RecursiveTask;

public class TransposedMultiMatrix extends RecursiveTask<double[][]> {
    private final double[][] matrix1;
    private final double[][] matrix2Transposed;
    private final int rowStart;
    private final int rowEnd;
    private final int colStart;
    private final int colEnd;
    private final int threshold;

    public TransposedMultiMatrix(double[][] matrix1, double[][] matrix2, int rowStart, int rowEnd, int colStart, int colEnd, int threshold) {
        this.matrix1 = matrix1;
        this.matrix2Transposed = transpose(matrix2);
        this.rowStart = rowStart;
        this.rowEnd = rowEnd;
        this.colStart = colStart;
        this.colEnd = colEnd;
        this.threshold = threshold;
    }

    @Override
    protected double[][] compute() {
        int rows = rowEnd - rowStart;
        int cols = colEnd - colStart;

        if (rows <= threshold || cols <= threshold) {
            return multiplyDirectly();
        }

        int split = Math.max(rows, cols) / 2;

        TransposedMultiMatrix topLeft = new TransposedMultiMatrix(matrix1, matrix2Transposed, rowStart, rowStart + split, colStart, colStart + split, threshold);
        TransposedMultiMatrix topRight = new TransposedMultiMatrix(matrix1, matrix2Transposed, rowStart, rowStart + split, colStart + split, colEnd, threshold);
        TransposedMultiMatrix bottomLeft = new TransposedMultiMatrix(matrix1, matrix2Transposed, rowStart + split, rowEnd, colStart, colStart + split, threshold);
        TransposedMultiMatrix bottomRight = new TransposedMultiMatrix(matrix1, matrix2Transposed, rowStart + split, rowEnd, colStart + split, colEnd, threshold);

        topLeft.fork();
        topRight.fork();
        bottomLeft.fork();
        double[][] bottomRightResult = bottomRight.compute();
        double[][] bottomLeftResult = bottomLeft.join();
        double[][] topRightResult = topRight.join();
        double[][] topLeftResult = topLeft.join();

        return combineResults(topLeftResult, topRightResult, bottomLeftResult, bottomRightResult);
    }

    private double[][] multiplyDirectly() {
        int rows = rowEnd - rowStart;
        int cols = colEnd - colStart;
        double[][] result = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                for (int k = 0; k < matrix1[0].length; k++) {
                    result[i][j] += matrix1[i + rowStart][k] * matrix2Transposed[j + colStart][k];
                }
            }
        }

        return result;
    }

    private double[][] combineResults(double[][] topLeft, double[][] topRight, double[][] bottomLeft, double[][] bottomRight) {
        int totalRows = topLeft.length + bottomLeft.length;
        int totalCols = topLeft[0].length + topRight[0].length;
        double[][] result = new double[totalRows][totalCols];

        for (int i = 0; i < topLeft.length; i++) {
            System.arraycopy(topLeft[i], 0, result[i], 0, topLeft[i].length);
            System.arraycopy(topRight[i], 0, result[i], topLeft[i].length, topRight[i].length);
        }

        for (int i = 0; i < bottomLeft.length; i++) {
            System.arraycopy(bottomLeft[i], 0, result[i + topLeft.length], 0, bottomLeft[i].length);
            System.arraycopy(bottomRight[i], 0, result[i + topLeft.length], bottomLeft[i].length, bottomRight[i].length);
        }

        return result;
    }

    public static double[][] multiply(double[][] matrix1, double[][] matrix2, int threshold) {
        if (matrix1[0].length != matrix2.length) {
            throw new IllegalArgumentException("Матрицы нельзя умножить");
        }

        TransposedMultiMatrix multiplier = new TransposedMultiMatrix(matrix1, matrix2, 0, matrix1.length, 0, matrix2.length, threshold);
        return multiplier.compute();
    }

    private static double[][] transpose(double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[][] transposed = new double[cols][rows];
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                transposed[i][j] = matrix[j][i];
            }
        }
        return transposed;
    }

    public static void main(String[] args) {
        int size = 1000;
        double[][] matrix1 = generateMatrix(size);
        double[][] matrix2 = generateMatrix(size);
        int threshold = 1000; // Порогове значення

        long startTime = System.currentTimeMillis();
        double[][] result = TransposedMultiMatrix.multiply(matrix1, matrix2, threshold);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("Умножение заняло " + duration + " milliseconds.");

        // Виведення перших 10 елементів результату
        for (int i = 0; i < Math.min(10, result.length); i++) {
            for (int j = 0; j < Math.min(10, result[i].length); j++) {
                System.out.print(result[i][j] + " ");
            }
            System.out.println();
        }
    }

    private static double[][] generateMatrix(int size) {
        double[][] matrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = Math.random();
            }
        }
        return matrix;
    }
}
