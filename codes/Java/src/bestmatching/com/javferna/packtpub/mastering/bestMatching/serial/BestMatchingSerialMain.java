package bestmatching.com.javferna.packtpub.mastering.bestMatching.serial;

import java.util.Date;
import java.util.List;

import bestmatching.com.javferna.packtpub.mastering.bestMatching.common.BestMatchingData;
import bestmatching.com.javferna.packtpub.mastering.bestMatching.data.WordsLoader;

public class BestMatchingSerialMain {
	
	public static double serialTime;

	public static void main(String[] args) {

		Date startTime, endTime;
		List<String> dictionary = WordsLoader.load("data/UK Advanced Cryptics Dictionary.txt");
		System.out.println("****** BestMatching Serial ******");
		System.out.println("Dictionary Size: " + dictionary.size());

		startTime = new Date();

		String word = "stitter";

		if (args.length == 1) {
			word = args[0];
		}
		BestMatchingData result = BestMatchingSerialCalculation.getBestMatchingWords(word, dictionary);
		List<String> results = result.getWords();
		endTime = new Date();
		System.out.println("Word: " + word);
		System.out.println("Minimun distance: " + result.getDistance());
		System.out.println("List of best matching words: " + results.size());
		results.forEach(System.out::println);
		serialTime = endTime.getTime() - startTime.getTime();
		System.out.println("Execution Time: " + serialTime);
	}

}
