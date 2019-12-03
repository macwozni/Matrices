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
     * @param args should be 2 strings with addresses of 2 files with matrixes at given format
     * @throws FileNotFoundException if the give file does not exist we just frow exception - from main subroutine...
     * @throws IOException if there is some problem with IO we just frow exception - from main subroutine...
     */
    public static void main(String args[]) throws FileNotFoundException, IOException {

        // print arguments count
        System.out.println(args.length);
        // print arguments list
        for (String arg : args) {
            System.out.println(arg);
        }
        
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

        // in processed/solved matrix it should be 1 on diagonal and 0 elsewhere
        for (int i = 0; i < processed.size; i++) {
            for (int j = 0; j < processed.size; j++) {
                // if diagonal - should be 1.0
                if (i == j) {
                    // if it is not 1.0 - print it to the output and exit
                    if (!MyMatrix.compare(1., processed.lhs[i][j], epsilon)) {
                        System.out.println("Error 1 " + i + " " + j);
                        System.exit(0);
                    }
                    //if  not diagonal - should be 0.0
                } else if (!MyMatrix.compare(0., processed.lhs[i][j], epsilon)) {
                    // if it is not 0.0 - print it to the output and exit
                    System.out.println("Error 2 " + i + " " + j);
                    System.exit(0);
                }
            }
        }

        System.out.print(x.getArray()[0][0]);
        for (int j = 1; j < processed.size; j++) {
            System.out.print(" ");
            System.out.print(x.getArray()[j][0]);
        }
        System.out.println();
        
        System.out.print(processed.rhs[0][0]);
        for (int j = 1; j < processed.size; j++) {
            System.out.print(" ");
            System.out.print(processed.rhs[j][0]);
        }
        System.out.println();
        
        // check RHS vector - should be equal to the one from solved here initial problem
        for (int j = 0; j < processed.size; j++) {
            if (!MyMatrix.compare(x.getArray()[j][0], processed.rhs[j][0], epsilon)) {
                // if it is not equal - print it to the output and exit
                System.out.println("Error 3 " + (processed.size + 1) + " " + j);
                System.out.println(x.getArray()[j][0] + " " + processed.rhs[j][0]);
                System.exit(0);
            }
        }
    }
}
