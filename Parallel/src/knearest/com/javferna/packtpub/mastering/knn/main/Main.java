package knearest.com.javferna.packtpub.mastering.knn.main;

public class Main {

	public static void main(String[] args) {
		SerialMain.main(new String[]{"10"});
		for (int i = 1; i <= Runtime.getRuntime().availableProcessors(); i++) {
			ParallelGrouplMain.main(new String[] { "10" , i + ""});
			System.out.println(String.format("Speedup: %.3f", SerialMain.serialTime / ParallelGrouplMain.parTime));
		}
	}
}
