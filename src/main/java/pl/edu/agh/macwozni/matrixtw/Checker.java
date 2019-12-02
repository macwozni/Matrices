package pl.edu.agh.macwozni.matrixtw;

import Jama.Matrix;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author macwozni
 */
public class Checker {

    // machine precision epsilon
    static double epsilon = 0.00001;

    /**
     * @param a first variable for comparisson
     * @param b second variable for comparisson
     * @param epsilon machine precission for floating point
     * @return true if equals or within bounds of epsilon precission
     */
    static boolean compare(double a, double b, double epsilon) {
        double c = Math.abs(a - b);
        return c < epsilon;
    }

    
    
    public static void main(String args[]) throws FileNotFoundException, IOException {

        // if there are more or less arguments then 2 file addresses
        if (args.length != 2) {
            System.err.print("wrong amount of arguments");
            System.exit(1);
        }

        // read source file
        // a file with unprocessed unsolved matrix
        File fil = new File(args[0]);
        FileReader inputFil = new FileReader(fil);
        BufferedReader in = new BufferedReader(inputFil);

        // parse file and create data structure
        MyMatrix source = new MyMatrix(in);

        // solve
        //create data structures for solver
        Matrix A = new Matrix(source.lhs);
        Matrix b = new Matrix(source.rhs);
        // x=a/b
        Matrix x = A.solve(b);

        // read output file
        // a file with processed/solved matrix
        fil = new File(args[1]);
        inputFil = new FileReader(fil);
        in = new BufferedReader(inputFil);

        // parse file and create data structure
        MyMatrix processed = new MyMatrix(in);

        for (int i = 0; i < processed.size; i++) {
            for (int j = 0; j < processed.size; j++) {
                if (i == j) {
                    if (!compare(1., processed.lhs[i][j], epsilon)) {
                        System.out.println("Error " + i + " " + j);
                        System.exit(0);
                    }
                } else if (!compare(0., processed.lhs[i][j], epsilon)) {
                    System.out.println("Error " + i + " " + j);
                    System.exit(0);
                }
            }
        }

        for (int j = 0; j < processed.size; j++) {
            if (!compare(x.getArray()[j][0], processed.rhs[j][0], epsilon)) {
                System.out.println("Error " + (processed.size + 1) + " " + j);
                System.exit(0);
            }
        }
    }
}
