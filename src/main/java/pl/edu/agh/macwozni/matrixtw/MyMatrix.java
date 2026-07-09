package pl.edu.agh.macwozni.matrixtw;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Parsed matrix file used by the generator and checker tools.
 *
 * <p>The matrix file format contains a positive size, a square left-hand-side
 * matrix, and a right-hand-side column vector. Array inputs are copied on
 * construction and accessors return copies, so callers cannot mutate the stored
 * values accidentally.</p>
 *
 * @param size number of rows and columns in the left-hand-side matrix
 * @param lhs left-hand-side matrix values
 * @param rhs right-hand-side vector stored as an {@code n x 1} matrix
 */
public record MyMatrix(int size, double[][] lhs, double[][] rhs) {

    /**
     * Creates an immutable-by-copy matrix representation.
     *
     * @throws IllegalArgumentException if the size is not positive or the arrays
     *         do not match the expected dimensions
     */
    public MyMatrix {
        if (size <= 0) {
            throw new IllegalArgumentException("Matrix size must be positive");
        }
        lhs = copyMatrix(lhs, size, size, "left-hand-side matrix");
        rhs = copyMatrix(rhs, size, 1, "right-hand-side vector");
    }

    /**
     * Reads a matrix from a buffered character stream.
     *
     * @param in input stream containing a matrix file
     * @throws IOException if the stream is incomplete or contains invalid matrix data
     */
    public MyMatrix(BufferedReader in) throws IOException {
        this(parse(in));
    }

    /**
     * Creates a matrix from raw parsed values.
     *
     * @param raw parsed matrix values
     */
    private MyMatrix(RawMatrix raw) {
        this(raw.size(), raw.lhs(), raw.rhs());
    }

    /**
     * Reads a matrix file from disk.
     *
     * @param path path to the matrix file
     * @return parsed matrix representation
     * @throws IOException if the file cannot be read or contains invalid matrix data
     */
    public static MyMatrix read(Path path) throws IOException {
        try (BufferedReader in = Files.newBufferedReader(path)) {
            return new MyMatrix(in);
        }
    }

    /**
     * Returns a defensive copy of the left-hand-side matrix.
     *
     * @return copied {@code n x n} matrix values
     */
    @Override
    public double[][] lhs() {
        return copyMatrix(lhs, size, size, "left-hand-side matrix");
    }

    /**
     * Returns a defensive copy of the right-hand-side vector.
     *
     * @return copied {@code n x 1} vector values
     */
    @Override
    public double[][] rhs() {
        return copyMatrix(rhs, size, 1, "right-hand-side vector");
    }

    /**
     * Parses a matrix from the project text format.
     *
     * @param in input stream to parse
     * @return raw parsed matrix values
     * @throws IOException if the stream is incomplete or contains invalid data
     */
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
     * Compares two floating-point values with an absolute tolerance.
     *
     * @param a first value to compare
     * @param b second value to compare
     * @param epsilon maximum accepted absolute difference
     * @return {@code true} if the values differ by less than {@code epsilon}
     */
    static boolean compare(double a, double b, double epsilon) {
        double c = Math.abs(a - b);
        return c < epsilon;
    }

    /**
     * Internal parsed representation used before canonical record validation.
     *
     * @param size parsed matrix size
     * @param lhs parsed left-hand-side matrix
     * @param rhs parsed right-hand-side vector
     */
    private record RawMatrix(int size, double[][] lhs, double[][] rhs) {
    }

    /**
     * Validates and copies a two-dimensional array.
     *
     * @param matrix source matrix
     * @param rows expected row count
     * @param columns expected column count
     * @param description human-readable value name used in error messages
     * @return copied matrix
     * @throws IllegalArgumentException if the matrix is missing or has unexpected dimensions
     */
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

    /**
     * Parses the matrix size line.
     *
     * @param value text value to parse
     * @return parsed matrix size
     * @throws IOException if the value is not an integer
     */
    private static int parseSize(String value) throws IOException {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            throw new IOException("Invalid matrix size: " + value.trim(), exception);
        }
    }

    /**
     * Parses a floating-point value from a matrix file.
     *
     * @param value text value to parse
     * @param description human-readable location used in error messages
     * @return parsed floating-point value
     * @throws IOException if the value is not a valid {@code double}
     */
    private static double parseDouble(String value, String description) throws IOException {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException exception) {
            throw new IOException("Invalid number in " + description + ": " + value, exception);
        }
    }

    /**
     * Reads a line that must be present.
     *
     * @param in input stream to read from
     * @param description human-readable line description used in error messages
     * @return read line
     * @throws IOException if the stream ends before the required line
     */
    private static String readRequiredLine(BufferedReader in, String description) throws IOException {
        String line = in.readLine();
        if (line == null) {
            throw new IOException("Missing " + description);
        }
        return line;
    }

    /**
     * Splits and validates one whitespace-separated value line.
     *
     * @param line line to split
     * @param expectedCount expected number of values
     * @param description human-readable line description used in error messages
     * @return split values
     * @throws IOException if the line is empty or has an unexpected number of values
     */
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
