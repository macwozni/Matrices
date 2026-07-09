package pl.edu.agh.macwozni.matrixtw;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author macwozni
 */
public record MyMatrix(int size, double[][] lhs, double[][] rhs) {

    public MyMatrix {
        if (size <= 0) {
            throw new IllegalArgumentException("Matrix size must be positive");
        }
        lhs = copyMatrix(lhs, size, size, "left-hand-side matrix");
        rhs = copyMatrix(rhs, size, 1, "right-hand-side vector");
    }

    public MyMatrix(BufferedReader in) throws IOException {
        this(parse(in));
    }

    private MyMatrix(RawMatrix raw) {
        this(raw.size(), raw.lhs(), raw.rhs());
    }

    public static MyMatrix read(Path path) throws IOException {
        try (BufferedReader in = Files.newBufferedReader(path)) {
            return new MyMatrix(in);
        }
    }

    @Override
    public double[][] lhs() {
        return copyMatrix(lhs, size, size, "left-hand-side matrix");
    }

    @Override
    public double[][] rhs() {
        return copyMatrix(rhs, size, 1, "right-hand-side vector");
    }

    private static RawMatrix parse(BufferedReader in) throws IOException {
        // in first line there is a integer with matrix size
        String s = readRequiredLine(in, "matrix size");

        // parse integer
        int size = parseSize(s);
        if (size <= 0) {
            throw new IOException("Matrix size must be positive");
        }
        //create data structures for reading matrix and RHS vector
        double[][] lhs = new double[size][size];
        double[][] rhs = new double[size][1];

        // read matrix line by line
        for (int i = 0; i < size; i++) {
            // read line
            s = readRequiredLine(in, "matrix row " + i);
            // split line along space signs
            String[] sp = splitValues(s, size, "matrix row " + i);
            // parse each string to double
            for (int j = 0; j < size; j++) {
                lhs[i][j] = parseDouble(sp[j], "matrix row " + i + ", column " + j);
            }
        }
        // read RHS vecor line
        s = readRequiredLine(in, "right-hand-side vector");
        // split line along space signs
        String[] sp = splitValues(s, size, "right-hand-side vector");
        // parse each string to double
        for (int j = 0; j < size; j++) {
            rhs[j][0] = parseDouble(sp[j], "right-hand-side vector element " + j);
        }

        return new RawMatrix(size, lhs, rhs);
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

    private record RawMatrix(int size, double[][] lhs, double[][] rhs) {
    }

    private static double[][] copyMatrix(double[][] matrix, int rows, int columns, String description) {
        if (matrix == null) {
            throw new IllegalArgumentException("Missing " + description);
        }
        if (matrix.length != rows) {
            throw new IllegalArgumentException("Expected " + rows + " rows in " + description
                    + " but found " + matrix.length);
        }

        double[][] copy = new double[rows][columns];
        for (int i = 0; i < rows; i++) {
            if (matrix[i] == null) {
                throw new IllegalArgumentException("Missing row " + i + " in " + description);
            }
            if (matrix[i].length != columns) {
                throw new IllegalArgumentException("Expected " + columns + " values in "
                        + description + " row " + i + " but found " + matrix[i].length);
            }
            System.arraycopy(matrix[i], 0, copy[i], 0, columns);
        }
        return copy;
    }

    private static int parseSize(String value) throws IOException {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            throw new IOException("Invalid matrix size: " + value.trim(), exception);
        }
    }

    private static double parseDouble(String value, String description) throws IOException {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException exception) {
            throw new IOException("Invalid number in " + description + ": " + value, exception);
        }
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
