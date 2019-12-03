package pl.edu.agh.macwozni.matrixtw;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author macwozni
 */
public class MyMatrix {
    int size;
    double[][] lhs;
    double[][] rhs;
    
    public MyMatrix(BufferedReader in) throws IOException{
        // in first line there is a integer with matrix size
        String s = in.readLine();

        // parse integer
        size = Integer.parseInt(s);
        //create data structures for reading matrix and RHS vector
        lhs = new double[size][size];
        rhs = new double[size][1];

        // read matrix line by line
        for (int i = 0; i < size; i++) {
            // read line
            s = in.readLine();
            // split line along space signs
            String[] sp = s.split(" ");
            // parse each string to double
            for (int j = 0; j < size; j++) {
                lhs[i][j] = Double.parseDouble(sp[j]);
            }
        }
        // read RHS vecor line
        s = in.readLine();
        // split line along space signs
        String[] sp = s.split(" ");
        // parse each string to double
        for (int j = 0; j < size; j++) {
            rhs[j][0] = Double.parseDouble(sp[j]);
        }
    }
    
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
}
