package jacobirelaxation;

import java.util.Random;

public class JacobiRelaxation {

	public static int n = 10000;
	public static float tolerance = 0.1f;
	public static boolean done = false;

	public static void main(String[] args) {
		float A[][] = generate(n);
		float B[][] = new float[n][n];
		long start, end;
		
		System.out.println(String.format("Number of row and length of the array: %,3d", n));
		start = System.currentTimeMillis();
		SequentialScanArray ssa = new SequentialScanArray(A, B);
		ssa.scan();
		end = System.currentTimeMillis();
		long seqTime = end - start;
		System.out.println(String.format("Sequential time: %,3d ms", seqTime));
		
		A = generate(n);
		B = new float[n][n];
		start = System.currentTimeMillis();
		ParallelScanArray psa = new ParallelScanArray(A, B);
		psa.scan();
		end = System.currentTimeMillis();
		long parTime = end - start;
        System.out.println(String.format("Parallel time: %,3d ms", parTime));
        System.out.println(String.format("Speedup: %.6f", (double) seqTime / parTime));
        System.out.println(String.format("Processor utilization: %.6f", (double) (seqTime / parTime) / Runtime.getRuntime().availableProcessors()));
	}
	
	public static float[][] generate(int n) {
		float[][] arr = new float[n][n];
		Random r = new Random();
		// Only generate the borders of the matrix
		for (int i = 0; i < n; i++) {
			arr[i][0] = r.nextFloat();
			for (int j = 0; j < n; j++) {
				arr[0][j] = r.nextFloat();
				arr[n - 1][j] = r.nextFloat();
			}
			arr[i][n - 1] = r.nextFloat();
		}
		return arr;
	}
}
