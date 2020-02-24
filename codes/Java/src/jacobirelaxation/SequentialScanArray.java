package jacobirelaxation;

public class SequentialScanArray {
	private float A[][];
	private float B[][];

	public SequentialScanArray(float[][] a, float[][] b) {
		A = a;
		B = b;
	}

	public void scan() {
		boolean done = false;
		while (!done) {
			for (int i = 1; i < JacobiRelaxation.n - 1; i++) {
				for (int j = 1; j < JacobiRelaxation.n - 1; j++) {
					/* Compute average of four neighbors */
					B[i][j] = (float) (0.25 * (A[i - 1][j] + A[i + 1][j] + A[i][j - 1] + A[i][j + 1]));
				}
			}
			done = true;
			for (int i = 1; i <= JacobiRelaxation.n - 2; i++) {
				for (int j = 1; j <= JacobiRelaxation.n - 2; j++) {
					if (Math.abs(B[i][j] - A[i][j]) > JacobiRelaxation.tolerance) 
						done = false;
					A[i][j] = B[i][j];
				}
			}
		}
	}
}
