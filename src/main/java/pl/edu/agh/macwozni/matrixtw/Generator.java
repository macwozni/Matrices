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
        // if there are more or less arguments then matrix size and 2 file addresses
        if (args.length != 3) {
            System.err.print("wrong amount of arguments");
            System.exit(1);
        }

        // parse matrix size
        int n = Integer.parseInt(args[0]);
        if (n <= 0) {
            System.err.print("matrix size must be positive");
            System.exit(1);
        }
        
        Matrix A;
        Matrix B;
        do {
            // generate random system of equations
            A = Matrix.random(n, n);
            B = Matrix.random(n, 1);
        } while (!isAcceptable(A, n));
        
        // open file for output - unsolved system of equations
        File file = new File(args[1]);
        FileOutputStream fos = new FileOutputStream(file);
        PrintStream ps = new PrintStream(fos);
        // set default output stream to file
        System.setOut(ps);
        
        // print matrix size
        System.out.println(n);
        // print matrix
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(A.getArray()[i][j] + " ");
            }
            System.out.println();
        }

        // print RHS
        for (int j = 0; j < n; j++) {
            System.out.print(B.getArray()[j][0] + " ");
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
        for (int j = 0; j < n; j++) {
            System.out.print(x.getArray()[j][0] + " ");
        }
        System.out.println();
    }

    /**
     * @param matrix matrix for generated system
     * @param size size of the matrix
     * @return true if matrix is nonsingular and does not require pivoting
     */
    static boolean isAcceptable(Matrix matrix, int size) {
        LUDecomposition lu = matrix.lu();
        return lu.isNonsingular() && !requiresPivot(matrix.getArray(), size);
    }

    /**
     * @param m matrix for gaussian elimination
     * @param size size of the matrix
     * @return true if matrix requires pivoting during gaussian elimination
     * This subroutine checks if matrix requires pivoting during simple gaussian elimination.
     */
    static boolean requiresPivot(double m[][], int size) {
        // Work on a copy so checking does not change the generated matrix.
        double[][] matrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(m[i], 0, matrix[i], 0, size);
        }

        // for each row
        for (int i = 0; i < size; i++) {
            // check if we have 0.0 on diagonal
            if (MyMatrix.compare(0., matrix[i][i], epsilon)) {
                // if yes - return true
                return true;
            }
            // for each row below
            for (int j=i+1; j<size; j++){
                // compute mulitplier
                double n = matrix[j][i]/matrix[i][i];
                // subtract each element of one row from another
                for (int k=0; k<size; k++){
                    matrix[j][k] = matrix[j][k] - matrix[i][k]*n;
                }
            }
        }

        // now we know, that pivoting is not required during elimination
        return false;
    }
}
