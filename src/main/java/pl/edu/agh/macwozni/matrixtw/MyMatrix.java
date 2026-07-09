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
        String s = readRequiredLine(in, "matrix size");

        // parse integer
        size = Integer.parseInt(s.trim());
        if (size <= 0) {
            throw new IOException("Matrix size must be positive");
        }
        //create data structures for reading matrix and RHS vector
        lhs = new double[size][size];
        rhs = new double[size][1];

        // read matrix line by line
        for (int i = 0; i < size; i++) {
            // read line
            s = readRequiredLine(in, "matrix row " + i);
            // split line along space signs
            String[] sp = splitValues(s, size, "matrix row " + i);
            // parse each string to double
            for (int j = 0; j < size; j++) {
                lhs[i][j] = Double.parseDouble(sp[j]);
            }
        }
        // read RHS vecor line
        s = readRequiredLine(in, "right-hand-side vector");
        // split line along space signs
        String[] sp = splitValues(s, size, "right-hand-side vector");
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

    private static String readRequiredLine(BufferedReader in, String description) throws IOException {
        String line = in.readLine();
        if (line == null) {
            throw new IOException("Missing " + description);
        }
        return line;
    }

    private static String[] splitValues(String line, int expectedCount, String description) throws IOException {
        String trimmed = line.trim();
        if (trimmed.length() == 0) {
            throw new IOException("Missing values in " + description);
        }

        String[] values = trimmed.split("\\s+");
        if (values.length != expectedCount) {
            throw new IOException("Expected " + expectedCount + " values in " + description
                    + " but found " + values.length);
        }
        return values;
    }
}
