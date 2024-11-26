package org.example;

// SIMD
import java.nio.DoubleBuffer;
import java.util.stream.IntStream;

public class VectorizedMatrixMultiplication {

    public double[][] multiply(double[][] a, double[][] b) {
        int n = a.length;
        double[][] c = new double[n][n];

        DoubleBuffer bufferA = DoubleBuffer.wrap(flattenMatrix(a));
        DoubleBuffer bufferB = DoubleBuffer.wrap(flattenMatrix(transposeMatrix(b)));
        double[] result = new double[n * n];

        IntStream.range(0, n).parallel().forEach(row -> {
            for (int col = 0; col < n; col++) {
                double sum = 0;
                for (int k = 0; k < n; k++) {
                    sum += bufferA.get(row * n + k) * bufferB.get(col * n + k);
                }
                result[row * n + col] = sum;
            }
        });

        return reshapeMatrix(result, n);
    }

    private double[][] transposeMatrix(double[][] matrix) {
        int n = matrix.length;
        double[][] transposed = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                transposed[j][i] = matrix[i][j];
            }
        }
        return transposed;
    }

    private double[] flattenMatrix(double[][] matrix) {
        int n = matrix.length;
        double[] flat = new double[n * n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(matrix[i], 0, flat, i * n, n);
        }
        return flat;
    }

    private double[][] reshapeMatrix(double[] flat, int n) {
        double[][] matrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(flat, i * n, matrix[i], 0, n);
        }
        return matrix;
    }
}