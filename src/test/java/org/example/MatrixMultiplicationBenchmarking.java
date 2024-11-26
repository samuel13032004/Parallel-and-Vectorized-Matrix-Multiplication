package org.example;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class MatrixMultiplicationBenchmarking {

    @State(Scope.Thread)
    public static class Operands {

        @Param({"10", "50", "100", "200", "500", "1000", "2000","5000"})
        public int n;

        public double[][] a;
        public double[][] b;

        public boolean memoryPrintedBasic = false;
        public boolean memoryPrintedParallel = false;
        public boolean memoryPrintedVectorized = false;

        @Setup(Level.Trial)
        public void setup() {
            a = new double[n][n];
            b = new double[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    a[i][j] = Math.random() * 10;
                    b[i][j] = Math.random() * 10;
                }
            }
        }

        private void printMemoryUsage(String methodName, int matrixSize, long memoryUsedBytes, boolean printedFlag) {
            if (!printedFlag) {
                double memoryUsedMB = memoryUsedBytes / (1024.0 * 1024.0);
                System.out.println(methodName + " - Matrix size: " + matrixSize + "x" + matrixSize +
                        " | Memory used: " + String.format("%.2f", memoryUsedMB) + " MB");
            }

        }
    }

    @Benchmark
    public void basicMultiplicationBenchmark(Operands operands) {
        Runtime runtime = Runtime.getRuntime();
        System.gc();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        BasicMatrixMultiplication basic = new BasicMatrixMultiplication();
        double[][] result = basic.multiply(operands.a, operands.b);

        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = Math.abs(memoryAfter - memoryBefore);
        operands.printMemoryUsage("Basic Multiplication", operands.n, memoryUsed, operands.memoryPrintedBasic);
        operands.memoryPrintedBasic = true;
    }

   @Benchmark
   public void parallelMultiplicationBenchmark(Operands operands) {
       Runtime runtime = Runtime.getRuntime();
       System.gc();
       long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

       ParallelMatrixMultiplication parallel = new ParallelMatrixMultiplication();
       double[][] result = parallel.multiply(operands.a, operands.b);

       long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
       long memoryUsed = Math.abs(memoryAfter - memoryBefore);
       operands.printMemoryUsage("Parallel Multiplication", operands.n, memoryUsed, operands.memoryPrintedParallel);
       operands.memoryPrintedParallel = true;
   }

    @Benchmark
    public void VectorizedMatrixMultiplicationBenchmark(Operands operands) {
        Runtime runtime = Runtime.getRuntime();
        System.gc();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        VectorizedMatrixMultiplication vectorized = new VectorizedMatrixMultiplication();
        double[][] result = vectorized.multiply(operands.a, operands.b);

        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = Math.abs(memoryAfter - memoryBefore);
        operands.printMemoryUsage("Vectorized Multiplication", operands.n, memoryUsed, operands.memoryPrintedVectorized);
        operands.memoryPrintedVectorized = true;
    }
}

