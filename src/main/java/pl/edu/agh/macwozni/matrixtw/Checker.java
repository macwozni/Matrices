package pl.edu.agh.macwozni.matrixtw;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.ejml.simple.SimpleMatrix;

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
        // if there are more or less arguments then 2 file addresses
        if (args.length != 2) {
            System.err.print("wrong amount of arguments");
            System.exit(1);
        }

        MyMatrix source;
        // read source file with unprocessed unsolved matrix
        try (BufferedReader in = new BufferedReader(new FileReader(args[0]))) {
            // parse file and create data structure
            source = new MyMatrix(in);
        }

        // solve
        //create data structures for solver
        SimpleMatrix A = new SimpleMatrix(source.lhs);
        SimpleMatrix b = new SimpleMatrix(source.rhs);
        // x=a/b
        SimpleMatrix x = A.solve(b);

        MyMatrix processed;
        // read output file with processed/solved matrix
        try (BufferedReader in = new BufferedReader(new FileReader(args[1]))) {
            // parse file and create data structure
            processed = new MyMatrix(in);
        }

        if (source.size != processed.size) {
            fail("Error size " + source.size + " " + processed.size);
        }

        // in processed/solved matrix it should be 1 on diagonal and 0 elsewhere
        for (int i = 0; i < processed.size; i++) {
            for (int j = 0; j < processed.size; j++) {
                // if diagonal - should be 1.0
                if (i == j) {
                    // if it is not 1.0 - print it to the output and exit
                    if (!MyMatrix.compare(1., processed.lhs[i][j], epsilon)) {
                        fail("Error 1 " + i + " " + j);
                    }
                    //if  not diagonal - should be 0.0
                } else if (!MyMatrix.compare(0., processed.lhs[i][j], epsilon)) {
                    // if it is not 0.0 - print it to the output and exit
                    fail("Error 2 " + i + " " + j);
                }
            }
        }
        
        // check RHS vector - should be equal to the one from solved here initial problem
        for (int j = 0; j < processed.size; j++) {
            if (!MyMatrix.compare(x.get(j, 0), processed.rhs[j][0], epsilon)) {
                // if it is not equal - print it to the output and exit
                System.out.println("Error 3 " + (processed.size + 1) + " " + j);
                System.out.println(x.get(j, 0) + " " + processed.rhs[j][0]);
                System.exit(1);
            }
        }
    }

    private static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }
}
