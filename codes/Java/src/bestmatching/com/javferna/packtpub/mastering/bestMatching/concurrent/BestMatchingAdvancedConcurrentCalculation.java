package bestmatching.com.javferna.packtpub.mastering.bestMatching.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import bestmatching.com.javferna.packtpub.mastering.bestMatching.common.BestMatchingData;
import bestmatching.com.javferna.packtpub.mastering.bestMatching.distance.LevenshteinDistance;

public class BestMatchingAdvancedConcurrentCalculation {

	public static BestMatchingData getBestMatchingWords(String word, List<String> dictionary)
			throws InterruptedException, ExecutionException {

		int numCores = Runtime.getRuntime().availableProcessors();
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numCores);

		int size = dictionary.size();
		int step = size / numCores;
		int remainder = size % numCores;
//		int startIndex, endIndex;
		List<Future<BestMatchingData>> results;
		List<Callable<BestMatchingData>> tasks = new ArrayList<>();

		for (int i = 0; i < numCores; i++) {
//			startIndex = i * step;
//			if (i == numCores - 1) {
//				endIndex = dictionary.size();
//			} else {
//				endIndex = (i + 1) * step;
//			}
//			BestMatchingBasicTask task = new BestMatchingBasicTask(startIndex, endIndex, dictionary, word);
//			tasks.add(task);
			final int startIndex = i * step;
			final int endIndex = (i * step) + step + ((i == numCores - 1) ? remainder : 0);
			Callable<BestMatchingData> bmd = () -> {
				List<String> res = new ArrayList<String>();
				int minDistance = Integer.MAX_VALUE;
				int distance;
				for (int j = startIndex; j < endIndex; j++) {
					distance = LevenshteinDistance.calculate(word, dictionary.get(j));
					if (distance < minDistance) {
						res.clear();
						minDistance = distance;
						res.add(dictionary.get(j));
					} else if (distance == minDistance) {
						res.add(dictionary.get(j));
					}
				}
				BestMatchingData result = new BestMatchingData();
				result.setWords(res);
				result.setDistance(minDistance);
				return result;
			};
			tasks.add(bmd);
		}

		results = executor.invokeAll(tasks);
		executor.shutdown();

		List<String> words = new ArrayList<String>();
		int minDistance = Integer.MAX_VALUE;
		for (Future<BestMatchingData> future : results) {
			BestMatchingData data = future.get();
			if (data.getDistance() < minDistance) {
				words.clear();
				minDistance = data.getDistance();
				words.addAll(data.getWords());
			} else if (data.getDistance() == minDistance) {
				words.addAll(data.getWords());
			}
		}

		BestMatchingData result = new BestMatchingData();
		result.setDistance(minDistance);
		result.setWords(words);
		return result;

	}

}
