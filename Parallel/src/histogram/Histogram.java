package histogram;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Histogram {

	static int n = 20000; // dimension of the image
	static int max; // maximum pixel intensity
	static int image[][] = new int[n][n]; // image array
	static AtomicInteger hist[]; // histogram

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		max = 10;
		generate(max);
		System.out.println(String.format("Image array generating done: %,3d rows, cols, and max = %d", n, max));
//		IntStream.range(0, n).mapToObj(i -> image[i]).forEach(a -> System.out.println(Arrays.toString(a)));

		long start, seqTime, parTime;
		start = System.currentTimeMillis();
		runHistogramSequential(image, max);
		seqTime = System.currentTimeMillis() - start;
		System.out.println(String.format("Sequential time for AtomicInteger with max = %d: %,3d ms", max, seqTime));

		start = System.currentTimeMillis();
		runHistogramParallelWithAtomicInteger(image, max);
		parTime = System.currentTimeMillis() - start;
		System.out.println(String.format("Parallel time for AtomicInteger with max = %d: %,3d ms", max, parTime));
		System.out.println(String.format("Speedup: %.6f\n", (double) seqTime / parTime));

		max = 100;
		generate(max);
		System.out.println(String.format("Image array generating done: %,3d rows, cols, and max = %d", n, max));
		start = System.currentTimeMillis();
		runHistogramSequential(image, max);
		seqTime = System.currentTimeMillis() - start;
		System.out.println(String.format("Sequential time for AtomicInteger with max = %d: %,3d ms", max, seqTime));

		start = System.currentTimeMillis();
		runHistogramParallelWithAtomicInteger(image, max);
		parTime = System.currentTimeMillis() - start;
		System.out.println(String.format("Parallel time for AtomicInteger with max = %d: %,3d ms", max, parTime));
		System.out.println(String.format("Speedup: %.6f\n", (double) seqTime / parTime));

		start = System.currentTimeMillis();
		runHistogramParallelWithInteger(image, max);
		parTime = System.currentTimeMillis() - start;
		System.out.println(String.format("Parallel time for ordinary Integer with max = %d (WRONG values): %,3d ms", max, parTime));
		System.out.println(String.format("Speedup: %.6f", (double) seqTime / parTime));

		start = System.currentTimeMillis();
		runHistogramParallelWithIntegerLocal(image, max);
		parTime = System.currentTimeMillis() - start;
		System.out.println(
				String.format("Parallel time for local ordinary Integer with max = %d: %,3d ms", max, parTime));
		System.out.println(String.format("Speedup: %.6f", (double) seqTime / parTime));

	}

	public static void generate(int max) {
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				image[i][j] = (i + j) % max;
	}

	public static void runHistogramSequential(int[][] image, int max) {
		hist = new AtomicInteger[max];
		for (int i = 0; i < max; i++)
			hist[i] = new AtomicInteger();

		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				hist[image[i][j]].getAndIncrement();

		for (int i = 0; i < max; i++)
			if (hist[i].get() != n * n / max)
				System.err.println("Error");

	}

	public static void runHistogramParallelWithAtomicInteger(int[][] image, int max)
			throws InterruptedException, ExecutionException {
		hist = new AtomicInteger[max];
		for (int i = 0; i < max; i++)
			hist[i] = new AtomicInteger();
		int numThreads = Runtime.getRuntime().availableProcessors();
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
		List<Future<Boolean>> listFutures = new ArrayList<>();
		int step = n / numThreads;
		int remainder = n % numThreads;
		for (int t = 0; t < numThreads; t++) {
			final int startIndex = t * step;
			final int endIndex = t * step + step + ((t == numThreads - 1) ? remainder : 0);
			Callable<Boolean> c = () -> {
				for (int i = startIndex; i < endIndex; i++)
					for (int j = 0; j < n; j++)
						hist[image[i][j]].incrementAndGet();
				return true;
			};
			Future<Boolean> future = executor.submit(c);
			listFutures.add(future);
		}
		
		executor.shutdown();
		executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);

		for (int i = 0; i < max; i++)
			if (hist[i].get() != n * n / max)
				System.err.println("Error");
	}

	public static void runHistogramParallelWithInteger(int[][] image, int max) throws InterruptedException {
		int[] hist = new int[max];
		for (int i = 0; i < max; i++)
			hist[i] = 0;
		int numThreads = Runtime.getRuntime().availableProcessors();
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
		int step = n / numThreads;
		int remainder = n % numThreads;
		for (int t = 0; t < numThreads; t++) {
			final int startIndex = t * step;
			final int endIndex = t * step + step + ((t == numThreads - 1) ? remainder : 0);
			final Integer[] localHist = new Integer[max];
			for (int i = 0; i < max; i++)
				localHist[i] = 0;
			Runnable r = () -> {
				for (int i = startIndex; i < endIndex; i++)
					for (int j = 0; j < n; j++)
						hist[image[i][j]] = hist[image[i][j]] + 1;
			};
			executor.submit(r);
		}
		
		executor.shutdown();
		executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
	}

	public static void runHistogramParallelWithIntegerLocal(int[][] image, int max) throws InterruptedException {
		int numThreads = Runtime.getRuntime().availableProcessors();
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
		int step = n / numThreads;
		int remainder = n % numThreads;
		List<Integer[]> listInts = new ArrayList<>();
		for (int t = 0; t < numThreads; t++) {
			final int startIndex = t * step;
			final int endIndex = t * step + step + ((t == numThreads - 1) ? remainder : 0);
			final Integer[] localHist = new Integer[max];
			for (int i = 0; i < max; i++)
				localHist[i] = 0;
			Runnable r = () -> {
				for (int i = startIndex; i < endIndex; i++)
					for (int j = 0; j < n; j++)
						localHist[image[i][j]] = localHist[image[i][j]] + 1;
			};
			listInts.add(localHist);
			executor.submit(r);
		}
		
		executor.shutdown();
		executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);

		int[] hist = new int[max];
		for (int i = 0; i < max; i++)
			hist[i] = 0;
		
		for (Integer[] ints : listInts)
			for (int i = 0; i < max; i++)
				hist[i] += ints[i];

		for (int i = 0; i < max; i++)
			if (hist[i] != n * n / max)
				System.err.println("Error");
	}
}