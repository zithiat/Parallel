package matrixmultiplier.com.javferna.packtpub.mastering.matrixmultiplier.main;

import java.util.Date;

import matrixmultiplier.com.javferna.packtpub.mastering.matrixmultiplier.parallel.group.ParallelGroupMultiplier;
import matrixmultiplier.com.javferna.packtpub.mastering.matrixmultiplier.util.MatrixGenerator;

public class ParallelGroupMain {

    public static void main(String[] args) {
        int num = 3000;
        double matrix1[][] = MatrixGenerator.generate(num, num);
        double matrix2[][] = MatrixGenerator.generate(num, num);

        double resultParallelGroup[][] = new double[matrix1.length][matrix2[0].length];
        Date start, end;
        start = new Date();
        ParallelGroupMultiplier.multiply(matrix1, matrix2, resultParallelGroup);
        end = new Date();
        System.out.printf("Parallel Group: %d%n", end.getTime() - start.getTime());
    }
}
