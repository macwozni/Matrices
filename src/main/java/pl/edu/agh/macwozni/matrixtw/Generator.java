package pl.edu.agh.macwozni.matrixtw;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import org.ejml.simple.SimpleMatrix;

public class Generator {

    // machine precision epsilon
    static final double EPSILON = 0.00001;

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

    static void writeSource(Path path, GeneratedSystem system) throws IOException {
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(path))) {
            writeSource(out, system);
        }
    }

    static void writeSolution(Path path, GeneratedSystem system) throws IOException {
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(path))) {
            writeSolution(out, system);
        }
    }

    private static void writeSource(PrintWriter out, GeneratedSystem system) {
        // print matrix size
        out.println(system.size());
        // print matrix
        writeMatrix(out, system.leftHandSide());

        // print RHS
        writeVector(out, system.rightHandSide());
    }

    private static void writeSolution(PrintWriter out, GeneratedSystem system) {
        // print size of matrix
        out.println(system.size());
        // matrix has 1.0 on diagonal, 0.0 elsewhere
        writeIdentity(out, system.size());
        // print solution
        writeVector(out, system.solution());
    }

    private static void writeMatrix(PrintWriter out, SimpleMatrix matrix) {
        for (int i = 0; i < matrix.getNumRows(); i++) {
            for (int j = 0; j < matrix.getNumCols(); j++) {
                out.print(matrix.get(i, j) + " ");
            }
            out.println();
        }
    }

    private static void writeIdentity(PrintWriter out, int size) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                out.print((i == j ? 1.0 : 0.0) + " ");
            }
            out.println();
        }
    }

    private static void writeVector(PrintWriter out, SimpleMatrix vector) {
        for (int j = 0; j < vector.getNumRows(); j++) {
            out.print(vector.get(j, 0) + " ");
        }
        out.println();
    }

    /**
     * @param matrix matrix for generated system
     * @param size size of the matrix
     * @return true if matrix is nonsingular and does not require pivoting
     */
    static boolean isAcceptable(SimpleMatrix matrix, int size) {
        boolean nonsingular = !MyMatrix.compare(0., matrix.determinant(), EPSILON);
        return nonsingular && !requiresPivot(matrix.toArray2(), size);
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

    record GeneratedSystem(int size, SimpleMatrix leftHandSide, SimpleMatrix rightHandSide,
            SimpleMatrix solution) {
    }
}
