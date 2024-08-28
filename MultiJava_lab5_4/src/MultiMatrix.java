import java.util.concurrent.RecursiveTask;

public class MultiMatrix extends RecursiveTask<double[][]> {
    private final double[][] matrix1;
    private final double[][] matrix2;
    private final int rowStart;
    private final int rowEnd;
    private final int colStart;
    private final int colEnd;
    private final int threshold;

    public MultiMatrix(double[][] matrix1, double[][] matrix2, int rowStart, int rowEnd, int colStart, int colEnd, int threshold) {
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;
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

        MultiMatrix topLeft = new MultiMatrix(matrix1, matrix2, rowStart, rowStart + split, colStart, colStart + split, threshold);
        MultiMatrix topRight = new MultiMatrix(matrix1, matrix2, rowStart, rowStart + split, colStart + split, colEnd, threshold);
        MultiMatrix bottomLeft = new MultiMatrix(matrix1, matrix2, rowStart + split, rowEnd, colStart, colStart + split, threshold);
        MultiMatrix bottomRight = new MultiMatrix(matrix1, matrix2, rowStart + split, rowEnd, colStart + split, colEnd, threshold);

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
                    result[i][j] += matrix1[i + rowStart][k] * matrix2[k][j + colStart];
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
            throw new IllegalArgumentException("Matrices cannot be multiplied");
        }

        MultiMatrix multiplier = new MultiMatrix(matrix1, matrix2, 0, matrix1.length, 0, matrix2[0].length, threshold);
        return multiplier.compute();
    }

    public static void main(String[] args) {
        // Приклад використання
        double[][] matrix1 = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        double[][] matrix2 = {{9, 8, 7}, {6, 5, 4}, {3, 2, 1}};
        int threshold = 1000; // Порогове значення

        double[][] result = MultiMatrix.multiply(matrix1, matrix2, threshold);

        // Виведення результату
        for (double[] row : result) {
            for (double cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }
}
