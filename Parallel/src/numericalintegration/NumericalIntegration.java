package numericalintegration;

import java.util.stream.IntStream;

public class NumericalIntegration {

	public static void main(String[] args) {
		long start, regTime, parTime;
		int a = 0, b = 2, n = 2000000000;
		double w = (double)(b - a)/n;
		System.out.println(String.format("Range: %,3d", n));
		System.out.println("Non-parallel stream ===");
		start = System.currentTimeMillis();
		double result = IntStream.range(1, n).asDoubleStream().map(i -> f(a + i * w)).sum();
		double ans = w * (result + (f(a)+f(b))/2.0);
		System.out.println(String.format("Result: %.6f", ans));
		regTime = System.currentTimeMillis() - start;
		System.out.println(String.format("Elapsed time: %,3d", regTime));
		
		System.out.println("\nParallel stream ===");
		start = System.currentTimeMillis();
		result = IntStream.range(1, n).asDoubleStream().parallel().map(i -> f(a + i * w)).sum();
		ans = w * (result + (f(a)+f(b))/2.0);
		System.out.println(String.format("Result: %.6f", ans));
		parTime = System.currentTimeMillis() - start;
		System.out.println(String.format("Elapsed time: %,3d", parTime));
		System.out.println(String.format("Speedup: %.4f ms", (double)regTime / parTime));
	}
	
	public static double f(double i) {
		return Math.sqrt(4.0 - i * i);
	}
}
