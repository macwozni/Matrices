package pl.edu.agh.macwozni.matrixtw;

import Jama.LUDecomposition;
import Jama.Matrix;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class Generator {

    // machine precision epsilon
    static double epsilon = 0.00001;

    public static void main(String args[]) throws FileNotFoundException, IOException {
        
        // print arguments count
        System.out.println(args.length);
        // print arguments list
        for (String arg : args) {
            System.out.println(arg);
        }

        // if there are more or less arguments then matrix size and 2 file addresses
        if (args.length != 3) {
            System.err.print("wrong amount of arguments");
            System.exit(1);
        }

        // parse matrix size
        int n = Integer.parseInt(args[0]);
        
        // generate random system of equations
        // LHS
        Matrix A = Matrix.random(n, n);
        // RHS
        Matrix B = Matrix.random(n, 1);
        // try to solve system of equations
        LUDecomposition lu = A.lu();
        // check if it is non singular
        boolean nonSingular = lu.isNonsingular();
        // if it is non singular - check if it requires pivot during gaussian elimination
        if (nonSingular){
            nonSingular = requiresPivot(A.getArray(), n);
        }
        // if it is non singular or requires pivot try to generate another system
        // until we find something that meets our requirements
        while (!nonSingular) {
            // generate random system of equations
            A = Matrix.random(n, n);
            // try to solve system of equations
            lu = A.lu();
            // check if it is non singular
            nonSingular = lu.isNonsingular();
            // if it is non singular - check if it requires pivot during gaussian elimination
            if (nonSingular){
                nonSingular = requiresPivot(A.getArray(), n);
            }
        }
        
        // open file for output - unsolved system of equations
        File file = new File(args[1]);
        FileOutputStream fos = new FileOutputStream(file);
        PrintStream ps = new PrintStream(fos);
        // set default output stream to file
        System.setOut(ps);
        
        // print matrix size
        System.out.println(n);
        // print matrix
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

        // print RHS
        System.out.print(B.getArray()[1][0]);
        for (int j = 1; j < n; j++) {
            System.out.print(" ");
            System.out.print(B.getArray()[j][0]);
        }
        System.out.println();

        // open file for output - solved system of equations
        file = new File(args[2]);
        fos = new FileOutputStream(file);
        ps = new PrintStream(fos);
        // set default output stream to file
        System.setOut(ps);
        
        // print size of matrix
        System.out.println(n);
        // matrix has 1.0 on diagonal, 0.0 elsewhere
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
        // solve system of equations
        Matrix x = A.solve(B);
        // print solution
        System.out.print(x.getArray()[0][0]);
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
