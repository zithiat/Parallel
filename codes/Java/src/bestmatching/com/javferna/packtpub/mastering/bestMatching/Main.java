package bestmatching.com.javferna.packtpub.mastering.bestMatching;

import bestmatching.com.javferna.packtpub.mastering.bestMatching.concurrent.BestMatchingConcurrentAdvancedMain;
import bestmatching.com.javferna.packtpub.mastering.bestMatching.concurrent.BestMatchingConcurrentMain;
import bestmatching.com.javferna.packtpub.mastering.bestMatching.serial.BestMatchingSerialMain;

public class Main {

	public static void main(String[] args) {
		BestMatchingSerialMain.main(new String[]{"stitter"});
		System.out.println();
		BestMatchingConcurrentMain.main(new String[]{"stitter"});
		System.out.println(String.format("Speedup: %.6f\n", BestMatchingSerialMain.serialTime / BestMatchingConcurrentMain.basicConcTime));
		BestMatchingConcurrentAdvancedMain.main(new String[]{"stitter"});
		System.out.println(String.format("Speedup: %.6f\n", BestMatchingSerialMain.serialTime / BestMatchingConcurrentAdvancedMain.advancedConcTime));
	}
}
