package pl.edu.agh.macwozni.matrixtw;

import Jama.LUDecomposition;
import Jama.Matrix;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class Generator {

    // machine precision epsilon
    static double epsilon = 0.00001;

    public static void main(String args[]) throws FileNotFoundException {
        System.out.println(args.length);
        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
        }

        // if there are more or less arguments then matrix size and 2 file addresses
        if (args.length != 3) {
            System.err.print("wrong amount of arguments");
            System.exit(1);
        }

        int n = Integer.parseInt(args[0]);

        Matrix A = Matrix.random(n, n);
        Matrix B = Matrix.random(n, 1);
        LUDecomposition lu = A.lu();
        boolean nonSingular = lu.isNonsingular();
        if (nonSingular){
            nonSingular = requiresPivot(A.getArray(), n);
        }
        while (!nonSingular) {
            A = Matrix.random(n, n);
            lu = A.lu();
            nonSingular = lu.isNonsingular();
            if (nonSingular){
                nonSingular = requiresPivot(A.getArray(), n);
            }
        }
        File file = new File(args[1]);
        FileOutputStream fos = new FileOutputStream(file);
        PrintStream ps = new PrintStream(fos);
        System.setOut(ps);
        System.out.println(n);

        System.out.print(A.getArray()[0][1]);
        for (int j = 1; j < n; j++) {
            System.out.print(" ");
            System.out.print(A.getArray()[0][j]);
        }
        System.out.println();
        for (int i = 1; i < n; i++) {
            System.out.print(A.getArray()[i][0]);
            for (int j = 1; j < n; j++) {
                System.out.print(" ");
                System.out.print(A.getArray()[i][j]);
            }
            System.out.println();
        }

        System.out.print(B.getArray()[1][0]);
        for (int j = 1; j < n; j++) {
            System.out.print(" ");
            System.out.print(B.getArray()[j][0]);
        }
        System.out.println();

        file = new File(args[2]);
        fos = new FileOutputStream(file);
        ps = new PrintStream(fos);
        System.setOut(ps);
        System.out.println(n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    System.out.print(1. + " ");
                } else {
                    System.out.print(0. + " ");
                }
            }
            System.out.println();
        }
        Matrix x = A.solve(B);
        System.out.print(x.getArray()[1][0]);
        for (int j = 1; j < n; j++) {
            System.out.print(" ");
            System.out.print(x.getArray()[j][0]);
        }
        System.out.println();
    }

    static boolean requiresPivot(double m[][], int size) {
        double[][] matrix = m;

        for (int i = 0; i < size; i++) {
            if (MyMatrix.compare(0., matrix[i][i], epsilon)) {
                return true;
            }
            for (int j=i+1; j<size; j++){
                double n = matrix[j][i]/matrix[i][i];
                for (int k=0; k<size; k++){
                    matrix[j][k] = matrix[j][k] - matrix[i][k]*n;
                }
            }
        }

        return false;
    }
}
