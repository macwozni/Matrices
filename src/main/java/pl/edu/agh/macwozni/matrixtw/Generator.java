package pl.edu.agh.macwozni.matrixtw;

import Jama.LUDecomposition;
import Jama.Matrix;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class Generator {

    public static void main(String args[]) throws FileNotFoundException {
        int n = 15;
        int k = 10;
        Matrix A = Matrix.random(n, n);
        Matrix b = Matrix.random(n, 1);
        LUDecomposition lu = A.lu();
        boolean nonSingular = lu.isNonsingular();
        while (!nonSingular) {
            A = Matrix.random(n, n);
            lu = A.lu();
            nonSingular = lu.isNonsingular();
            System.out.println(nonSingular);
        }
        File file = new File("/home/maciekw/Dropbox/studenci/TW Konspekt/projekt/out" + k + ".txt");
        FileOutputStream fos = new FileOutputStream(file);
        PrintStream ps = new PrintStream(fos);
        System.setOut(ps);
        System.out.println(n);
        A.print(0, 10);
        b.transpose().print(0, 10);

        file = new File("/home/maciekw/Dropbox/studenci/TW Konspekt/projekt/res" + k + ".txt");
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
        A.solve(b).transpose().print(0, 10);
    }
}