package org.example;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ParallelMatrixMultiplication {

    public double[][] multiply(double[][] a, double[][] b) {
        int n = a.length;
        double[][] c = new double[n][n];

        double[][] bTransposed = transposeMatrix(b);

        ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors() * 2);
        pool.invoke(new MultiplyTask(a, bTransposed, c, 0, n));

        return c;
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

    static class MultiplyTask extends RecursiveAction {
        private static final int BASE_THRESHOLD = 64;
        private final double[][] a, bTransposed, c;
        private final int startRow, endRow;

        public MultiplyTask(double[][] a, double[][] bTransposed, double[][] c, int startRow, int endRow) {
            this.a = a;
            this.bTransposed = bTransposed;
            this.c = c;
            this.startRow = startRow;
            this.endRow = endRow;
        }

        @Override
        protected void compute() {
            int n = a.length;
            int threshold = Math.max(BASE_THRESHOLD, n / Runtime.getRuntime().availableProcessors());

            if (endRow - startRow <= threshold) {
                multiplyDirect();
            } else {
                int mid = (startRow + endRow) / 2;
                MultiplyTask top = new MultiplyTask(a, bTransposed, c, startRow, mid);
                MultiplyTask bottom = new MultiplyTask(a, bTransposed, c, mid, endRow);
                invokeAll(top, bottom);
            }
        }

        private void multiplyDirect() {
            int blockSize = 32;
            int n = a.length;

            for (int i = startRow; i < endRow; i += blockSize) {
                for (int j = 0; j < n; j += blockSize) {
                    for (int k = 0; k < n; k += blockSize) {
                        // Compute block (i, j) for this range
                        for (int ii = i; ii < Math.min(i + blockSize, endRow); ii++) {
                            for (int jj = j; jj < Math.min(j + blockSize, n); jj++) {
                                double sum = 0;
                                for (int kk = k; kk < Math.min(k + blockSize, n); kk++) {
                                    sum += a[ii][kk] * bTransposed[jj][kk];
                                }
                                c[ii][jj] += sum;
                            }
                        }
                    }
                }
            }
        }
    }
}
