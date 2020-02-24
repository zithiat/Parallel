package bestmatching.com.javferna.packtpub.mastering.bestMatching.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import bestmatching.com.javferna.packtpub.mastering.bestMatching.common.BestMatchingData;
import bestmatching.com.javferna.packtpub.mastering.bestMatching.distance.LevenshteinDistance;

public class BestMatchingBasicTask implements Callable<BestMatchingData> {

	private int startIndex;

	private int endIndex;

	private List<String> dictionary;

	private String word;

	public BestMatchingBasicTask(int startIndex, int endIndex, List<String> dictionary, String word) {
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.dictionary = dictionary;
		this.word = word;
	}

	@Override
	public BestMatchingData call() throws Exception {
		List<String> results = new ArrayList<String>();
		int minDistance = Integer.MAX_VALUE;
		int distance;
		for (int i = startIndex; i < endIndex; i++) {
			distance = LevenshteinDistance.calculate(word, dictionary.get(i));
			if (distance < minDistance) {
				results.clear();
				minDistance = distance;
				results.add(dictionary.get(i));
			} else if (distance == minDistance) {
				results.add(dictionary.get(i));
			}
		}

		BestMatchingData result = new BestMatchingData();
		result.setWords(results);
		result.setDistance(minDistance);
		return result;
	}

}
