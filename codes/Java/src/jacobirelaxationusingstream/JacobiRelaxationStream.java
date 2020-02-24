package jacobirelaxationusingstream;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.IntStream;

public class JacobiRelaxationStream {

	static int n = 10;
	static double[][] A = new double[n][n];
	static double[][] B = new double[n][n];
	static double tolerance = 0.1;
	private static boolean isDone = false;

	public static void main(String[] args) {
		long start, seqTimeFor, seqTimeStream, parTimeFor, parTimeStream;
		for (int i = 1; i <= 4; i++) {
			System.out.println("====================================\n");
			n = (int)Math.pow(10, i);
			generate();
			System.out.println(String.format("Generating ... %,3d rows, cols with tolerance = %.2f", n, tolerance));
			start = System.currentTimeMillis();
			sequentialScanWithFor();
			seqTimeFor = System.currentTimeMillis() - start;
			System.out.println(String.format("Sequential scan with FOR loop time: %,3d ms\n", seqTimeFor));
			
			System.out.println(String.format("Regenerating ... %,3d rows, cols with tolerance = %.2f", n, tolerance));
			generate();
			start = System.currentTimeMillis();
			sequentialScanWithStream();
			seqTimeStream = System.currentTimeMillis() - start;
			System.out.println(String.format("Sequential scan with IntStream time: %,3d ms\n", seqTimeStream));

			System.out.println(String.format("Regenerating ... %,3d rows, cols with tolerance = %.2f", n, tolerance));
			generate();
			start = System.currentTimeMillis();
			ParallelScanArray psa = new ParallelScanArray(A, B);
			psa.scan();
			parTimeFor = System.currentTimeMillis() - start;
			System.out.println(String.format("Parallel with FOR loop time: %,3d ms", parTimeFor));
			System.out.println(String.format("Speedup with FOR loop time (comparing with sequential - FOR): %.6f ms", (double)seqTimeFor/ parTimeFor));
			System.out.println(String.format("Speedup with FOR loop time (comparing with sequential - IntStream): %.6f ms\n", (double)seqTimeStream/ parTimeFor));
			
			System.out.println(String.format("Regenerating ... %,3d rows, cols with tolerance = %.2f", n, tolerance));
			generate();
			start = System.currentTimeMillis();
			parallelScan();
			parTimeStream = System.currentTimeMillis() - start;
			System.out.println(String.format("Parallel time with IntStream: %,3d ms", parTimeStream));
			System.out.println(String.format("Speedup with IntStream loop time (comparing with sequential - FOR): %.6f ms", (double)seqTimeFor/ parTimeStream));
			System.out.println(String.format("Speedup with IntStream loop time (comparing with sequential - IntStream): %.6f ms", (double)seqTimeStream/ parTimeStream));
			System.out.println(String.format("Speedup with IntStream loop time (comparing with parallel - FOR): %.6f ms\n", (double)parTimeFor/ parTimeStream));
		}
	}

	private static void generate() {
		A = new double[n][n];
		B = new double[n][n];
		for (int i = 1; i < n - 1; i++)
			for (int j = 1; j < n - 1; j++)
				A[i][j] = 0;
		for (int i = 0; i < n; i++) {
			A[i][0] = 10d;
			A[i][n - 1] = 10d;
			A[0][i] = 10d;
			A[n - 1][i] = 10d;
		}
	}

	private static void sequentialScanWithFor() {
		boolean done = false;
		while (!done) {
			for (int i = 1; i < n - 1; i++) {
				for (int j = 1; j < n - 1; j++) {
					B[i][j] = (double) (0.25 * (A[i - 1][j] + A[i + 1][j] + A[i][j - 1] + A[i][j + 1]));
				}
			}
			done = true;
			for (int i = 1; i <= n - 2; i++) {
				for (int j = 1; j <= n - 2; j++) {
					if (Math.abs(B[i][j] - A[i][j]) > tolerance)
						done = false;
					A[i][j] = B[i][j];
				}
			}
		}
	}

	private static void sequentialScanWithStream() {
		isDone = false;
		while (!isDone) {
			IntStream.range(1, n - 1).sequential().forEach((i) -> {
				IntStream.range(1, n - 1).sequential().forEach((j) -> {
					B[i][j] = (double) (0.25 * (A[i - 1][j] + A[i + 1][j] + A[i][j - 1] + A[i][j + 1]));
					}); });
			isDone = true;
			IntStream.range(1, n - 1).boxed().sequential().forEach((i) -> {
				IntStream.range(1, n - 1).boxed().sequential().forEach((j) -> {
					if (Math.abs(B[i][j] - A[i][j]) > tolerance)
						isDone = false;
					A[i][j] = B[i][j]; }); });
		}
	}
	
	private static void parallelScan() {
		isDone = false;
		while (!isDone) {
			IntStream.range(1, n - 1).parallel().forEach((i) -> {
				IntStream.range(1, n - 1).forEach((j) -> {
					B[i][j] = (double) (0.25 * (A[i - 1][j] + A[i + 1][j] + A[i][j - 1] + A[i][j + 1])); }); });
			isDone = true;
			IntStream.range(1, n - 1).boxed().parallel().forEach((i) -> {
				IntStream.range(1, n - 1).boxed().forEach((j) -> {
					if (Math.abs(B[i][j] - A[i][j]) > tolerance)
						isDone = false;
					A[i][j] = B[i][j]; }); });
		}
	}
}

class ParallelScanArray {
	private double A[][];
	private double B[][];
	private CyclicBarrier globalBarrier, localBarrier;
	
	public ParallelScanArray(double[][] a2, double[][] b2) {
		A = a2;
		B = b2;
	}
	
	public void scan() {
		int parties = Runtime.getRuntime().availableProcessors();
		int step = JacobiRelaxationStream.n / parties;
		List<Thread> threads = new ArrayList<Thread>();
		List<ParallelScanArrayTask> tasks = new ArrayList<ParallelScanArrayTask>();
		for (int i = 0; i < parties; i++) {
			tasks.add(new ParallelScanArrayTask(A, B, (i * step) + 1, (i * step) + step));
		}
		Status s = new Status(tasks);
		this.globalBarrier = new CyclicBarrier(parties);
		this.localBarrier = new CyclicBarrier(parties, s);
		while(!s.doneAll) {
			threads.clear();
			for (int i = 0; i < parties; i++) {
				Thread t = new Thread(tasks.get(i));
				t.start();
				threads.add(t);
			}
			try {
				for (int i = 0; i < parties; i++) {
					threads.get(i).join();
				}
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private class ParallelScanArrayTask implements Runnable {
		private int startIndex, endIndex;
		private boolean done;
		private double A[][];
		private double B[][];

		public ParallelScanArrayTask(double[][] a, double[][] b, int startIndex, int endIndex) {
			this.A = a;
			this.B = b;
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}
		
		@Override
		public void run() {
			this.done = false;
			try {
                for (int i = startIndex; i < endIndex - 1; i++) {
                    for (int j = startIndex; j < endIndex - 1; j++) {
                        B[i][j] = (double)0.25 * (A[i + 1][j] + A[i - 1][j] + A[i][j - 1] + A[i][j + 1]);
                    }
                }
                this.done = true;
                globalBarrier.await();
                for (int i = startIndex; i < endIndex - 1; i++) {
                    for (int j = startIndex; j < endIndex - 1; j++) {
                        if (Math.abs(A[i][j] - B[i][j]) > JacobiRelaxationStream.tolerance) {
                            this.done = false;
                        }
                        A[i][j] = B[i][j];
                    }
                }
                localBarrier.await();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
		}
		
		public boolean getDone() {
			return this.done;
		}
	}
	
	private class Status implements Runnable {
		public boolean doneAll = false;
		private List<ParallelScanArrayTask> tasks;
		
		public Status(List<ParallelScanArrayTask> tasks) {
			this.tasks = tasks;
		}
		
		@Override
		public void run() {
			doneAll = true;
			for (ParallelScanArrayTask t : tasks) {
				doneAll = doneAll && t.getDone();
			}
		}
	}
}
