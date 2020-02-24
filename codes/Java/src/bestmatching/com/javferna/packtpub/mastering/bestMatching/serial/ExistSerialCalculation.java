package bestmatching.com.javferna.packtpub.mastering.bestMatching.serial;

import java.util.List;

import bestmatching.com.javferna.packtpub.mastering.bestMatching.distance.LevenshteinDistance;

public class ExistSerialCalculation {

	public static boolean existWord(String word, List<String> dictionary) {
		for (String str : dictionary) {
			if (LevenshteinDistance.calculate(word, str) == 0) {
				return true;
			}
		}
		return false;
	}
}
