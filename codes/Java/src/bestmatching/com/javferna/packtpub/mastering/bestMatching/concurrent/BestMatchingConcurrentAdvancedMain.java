package bestmatching.com.javferna.packtpub.mastering.bestMatching.concurrent;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import bestmatching.com.javferna.packtpub.mastering.bestMatching.common.BestMatchingData;
import bestmatching.com.javferna.packtpub.mastering.bestMatching.data.WordsLoader;

public class BestMatchingConcurrentAdvancedMain {
	
	public static double advancedConcTime;

	public static void main(String[] args) {
		try {
			Date startTime, endTime;
			List<String> dictionary = WordsLoader.load("data/UK Advanced Cryptics Dictionary.txt");
			System.out.println("****** BestMatching Advanced Concurrent ******");
			System.out.println("Dictionary Size: " + dictionary.size());

			startTime = new Date();

			String word = "stitter";

			if (args.length == 1) {
				word = args[0];
			}

			BestMatchingData result;
			result = BestMatchingAdvancedConcurrentCalculation.getBestMatchingWords(word, dictionary);
			List<String> results = result.getWords();
			endTime = new Date();
			System.out.println("Word: " + word);
			System.out.println("Minimun distance: " + result.getDistance());
			System.out.println("List of best matching words: " + results.size());
			for (String wordOut : results) {
				System.out.println(wordOut);
			}
			advancedConcTime = endTime.getTime() - startTime.getTime();
			System.out.println("Execution Time: " + advancedConcTime);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

	}

}
