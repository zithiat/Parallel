package exercises;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyTestCallable {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		int numCores = Runtime.getRuntime().availableProcessors();
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numCores);
		
		List<Future<String>> results = new ArrayList<Future<String>>();
		for (int i = 0; i < numCores; i++) {
			final int tid = i;
			Callable<String> callableObj = () -> {
				return "Thread:" + tid;
			};
			Future<String> future = executor.submit(callableObj);
			results.add(future);
		}
		executor.shutdown();
		executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
		
		for (Future<String> f : results) {
			System.out.println(f.get());
		}
	}
}
