package exercises;

import java.util.ArrayList;
import java.util.List;

public class J5 {

	private static final int num = 10000000;
	
	public static void main(String[] args) {
		System.out.println(String.format("Number of item to push and pop to the stack: %,3d", num));
		long start = System.currentTimeMillis();
		MyParallelStack mps = new MyParallelStack();
		for (int i = 0; i < num; i++) {
			mps.push(i);
		}
		
		for (int i = 0; i < num; i++) {
			mps.pop();
		}
		long seqTime = System.currentTimeMillis() - start;
		System.out.println("Sequential time: " + seqTime + " ms\n");
		
		List<Thread> threads = new ArrayList<>();
        int numThreads = Runtime.getRuntime().availableProcessors();
        System.out.println("Number of threads: " + numThreads);
        start = System.currentTimeMillis();
        for (int i = 0; i < numThreads; i++) {
            ParallelTask task = new ParallelTask(i, num / numThreads);
            Thread thread = new Thread(task);
            thread.start();
            threads.add(thread);
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long parTime = System.currentTimeMillis() - start;
        System.out.println("Parallel time: " + parTime + " ms");
        System.out.println(String.format("Speedup: %.6f", ((double)seqTime / parTime)));
        System.out.println(String.format("Processor Utilization: %.6f", ((double)seqTime / parTime) / numThreads));
	}
}
