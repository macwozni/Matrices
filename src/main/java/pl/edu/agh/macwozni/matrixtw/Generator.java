package pl.edu.agh.macwozni.matrixtw;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import org.ejml.simple.SimpleMatrix;

/**
 * Command-line tool for creating Gaussian elimination test files.
 *
 * <p>The generator creates two files for a random linear system:
 * the source file contains the matrix equation {@code A * x = b}, while the
 * reference file contains the identity matrix and the exact solution vector
 * computed with EJML. Generated matrices are filtered so they are nonsingular
 * and do not require pivoting during simple Gaussian elimination.</p>
 */
public class Generator {

    /**
     * Absolute tolerance used when checking whether a floating-point value is
     * numerically zero.
     */
    static final double EPSILON = 0.00001;

    /**
     * Generates a source matrix file and a matching reference solution file.
     *
     * <p>The expected argument order is:</p>
     *
     * <pre>{@code
     * <matrix size> <source output file> <reference output file>
     * }</pre>
     *
     * @param args command-line arguments
     * @throws IOException if either output file cannot be written
     */
    public static void main(String args[]) throws IOException {
        // if there are more or less arguments then matrix size and 2 file addresses
        if (args.length != 3) {
            System.err.print("wrong amount of arguments");
            System.exit(1);
            return;
        }

        // parse matrix size
        int n;
        try {
            n = parseMatrixSize(args[0]);
        } catch (IllegalArgumentException exception) {
            System.err.print(exception.getMessage());
            System.exit(1);
            return;
        }

        GeneratedSystem system = generateSystem(n);

        // write unsolved system of equations
        writeSource(Path.of(args[1]), system);

        // write solved system of equations
        writeSolution(Path.of(args[2]), system);
    }

    /**
     * Creates a random linear system that is suitable for the assignment.
     *
     * <p>The returned matrix is nonsingular and can be processed by plain
     * Gaussian elimination without pivoting.</p>
     *
     * @param size number of equations and unknowns
     * @return generated source system and its solution
     */
    static GeneratedSystem generateSystem(int size) {
        SimpleMatrix A;
        SimpleMatrix B;
        do {
            // generate random system of equations
            A = SimpleMatrix.random_DDRM(size, size);
            B = SimpleMatrix.random_DDRM(size, 1);
        } while (!isAcceptable(A, size));

        return new GeneratedSystem(size, A, B, A.solve(B));
    }

    /**
     * Parses and validates the matrix size passed on the command line.
     *
     * @param value text value to parse
     * @return positive matrix size
     * @throws IllegalArgumentException if the value is not an integer or is not positive
     */
    static int parseMatrixSize(String value) {
        int size;
        try {
            size = Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("matrix size must be an integer", exception);
        }
        if (size <= 0) {
            throw new IllegalArgumentException("matrix size must be positive");
        }
        return size;
    }

    /**
     * Writes the generated source system to a matrix file.
     *
     * @param path destination file path
     * @param system generated system to write
     * @throws IOException if the destination file cannot be written
     */
    static void writeSource(Path path, GeneratedSystem system) throws IOException {
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(path))) {
            writeSource(out, system);
        }
    }

    /**
     * Writes the reference solution to a matrix file.
     *
     * @param path destination file path
     * @param system generated system whose solution should be written
     * @throws IOException if the destination file cannot be written
     */
    static void writeSolution(Path path, GeneratedSystem system) throws IOException {
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(path))) {
            writeSolution(out, system);
        }
    }

    /**
     * Writes the source representation of a generated system.
     *
     * @param out writer receiving the matrix file contents
     * @param system generated system to write
     */
    private static void writeSource(PrintWriter out, GeneratedSystem system) {
        // print matrix size
        out.println(system.size());
        // print matrix
        writeMatrix(out, system.leftHandSide());

        // print RHS
        writeVector(out, system.rightHandSide());
    }

    /**
     * Writes the solved representation of a generated system.
     *
     * @param out writer receiving the matrix file contents
     * @param system generated system to write
     */
    private static void writeSolution(PrintWriter out, GeneratedSystem system) {
        // print size of matrix
        out.println(system.size());
        // matrix has 1.0 on diagonal, 0.0 elsewhere
        writeIdentity(out, system.size());
        // print solution
        writeVector(out, system.solution());
    }

    /**
     * Writes all values from a matrix in row-major order.
     *
     * @param out writer receiving the matrix rows
     * @param matrix matrix to write
     */
    private static void writeMatrix(PrintWriter out, SimpleMatrix matrix) {
        for (int i = 0; i < matrix.getNumRows(); i++) {
            for (int j = 0; j < matrix.getNumCols(); j++) {
                out.print(matrix.get(i, j) + " ");
            }
            out.println();
        }
    }

    /**
     * Writes an identity matrix with the project matrix-file formatting.
     *
     * @param out writer receiving the matrix rows
     * @param size number of rows and columns
     */
    private static void writeIdentity(PrintWriter out, int size) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                out.print((i == j ? 1.0 : 0.0) + " ");
            }
            out.println();
        }
    }

    /**
     * Writes a column vector as one whitespace-separated line.
     *
     * @param out writer receiving the vector line
     * @param vector column vector to write
     */
    private static void writeVector(PrintWriter out, SimpleMatrix vector) {
        for (int j = 0; j < vector.getNumRows(); j++) {
            out.print(vector.get(j, 0) + " ");
        }
        out.println();
    }

    /**
     * Checks whether a generated matrix can be used as a test input.
     *
     * @param matrix candidate left-hand-side matrix
     * @param size number of rows and columns in the matrix
     * @return {@code true} if the matrix is nonsingular and does not require pivoting
     */
    static boolean isAcceptable(SimpleMatrix matrix, int size) {
        boolean nonsingular = !MyMatrix.compare(0., matrix.determinant(), EPSILON);
        return nonsingular && !requiresPivot(matrix.toArray2(), size);
    }

    /**
     * Checks whether plain Gaussian elimination would require pivoting.
     *
     * <p>The input matrix is copied before elimination, so this method does not
     * modify the caller's array.</p>
     *
     * @param m matrix for Gaussian elimination
     * @param size number of rows and columns to inspect
     * @return {@code true} if a numerically zero pivot appears during elimination
     */
    static boolean requiresPivot(double[][] m, int size) {
        // Work on a copy so checking does not change the generated matrix.
        double[][] matrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(m[i], 0, matrix[i], 0, size);
        }

        // for each row
        for (int i = 0; i < size; i++) {
            // check if we have 0.0 on diagonal
            if (MyMatrix.compare(0., matrix[i][i], EPSILON)) {
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

    /**
     * Complete generated test case.
     *
     * @param size number of equations and unknowns
     * @param leftHandSide matrix {@code A} from {@code A * x = b}
     * @param rightHandSide vector {@code b} from {@code A * x = b}
     * @param solution vector {@code x} that solves the generated system
     */
    record GeneratedSystem(int size, SimpleMatrix leftHandSide, SimpleMatrix rightHandSide,
            SimpleMatrix solution) {
    }
}
