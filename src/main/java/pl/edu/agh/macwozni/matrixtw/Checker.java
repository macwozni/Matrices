package pl.edu.agh.macwozni.matrixtw;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.ejml.simple.SimpleMatrix;

/**
 * Command-line tool for validating processed Gaussian elimination results.
 *
 * <p>The checker reads the original system, solves it with EJML, and compares
 * the student's processed output against the expected identity matrix and
 * solution vector.</p>
 */
public class Checker {

    /**
     * Absolute tolerance used when comparing floating-point values.
     */
    static final double EPSILON = 0.00001;

    /**
     * Validates a processed matrix file against a source matrix file.
     *
     * <p>The expected argument order is:</p>
     *
     * <pre>{@code
     * <source matrix file> <processed matrix file>
     * }</pre>
     *
     * @param args command-line arguments
     * @throws IOException if either input file cannot be read
     */
    public static void main(String args[]) throws IOException {
        // if there are more or less arguments then 2 file addresses
        if (args.length != 2) {
            System.err.print("wrong amount of arguments");
            System.exit(1);
            return;
        }

        CheckResult result = check(Path.of(args[0]), Path.of(args[1]));
        switch (result) {
            case CheckResult.Success success -> {
                // nothing to print for a correct result
            }
            case CheckResult.Failure failure -> {
                failure.lines().forEach(System.out::println);
                System.exit(1);
            }
        }
    }

    /**
     * Checks a processed matrix file against the source system.
     *
     * <p>A successful processed file contains an identity matrix on the
     * left-hand side and the correct solution vector on the right-hand side.</p>
     *
     * @param sourcePath path to the original generated system
     * @param processedPath path to the processed student result
     * @return success or failure with diagnostic lines
     * @throws IOException if either matrix file cannot be read or parsed
     */
    static CheckResult check(Path sourcePath, Path processedPath) throws IOException {
        // read source file with unprocessed unsolved matrix
        MyMatrix source = MyMatrix.read(sourcePath);

        // solve
        //create data structures for solver
        SimpleMatrix A = new SimpleMatrix(source.lhs());
        SimpleMatrix b = new SimpleMatrix(source.rhs());
        // x=a/b
        SimpleMatrix x = A.solve(b);

        // read output file with processed/solved matrix
        MyMatrix processed = MyMatrix.read(processedPath);

        if (source.size() != processed.size()) {
            return new CheckResult.Failure("Error size " + source.size() + " " + processed.size());
        }

        double[][] processedLhs = processed.lhs();
        double[][] processedRhs = processed.rhs();

        // in processed/solved matrix it should be 1 on diagonal and 0 elsewhere
        for (int i = 0; i < processed.size(); i++) {
            for (int j = 0; j < processed.size(); j++) {
                // if diagonal - should be 1.0
                if (i == j) {
                    // if it is not 1.0 - print it to the output and exit
                    if (!MyMatrix.compare(1., processedLhs[i][j], EPSILON)) {
                        return new CheckResult.Failure("Error 1 " + i + " " + j);
                    }
                    //if  not diagonal - should be 0.0
                } else if (!MyMatrix.compare(0., processedLhs[i][j], EPSILON)) {
                    // if it is not 0.0 - print it to the output and exit
                    return new CheckResult.Failure("Error 2 " + i + " " + j);
                }
            }
        }
        
        // check RHS vector - should be equal to the one from solved here initial problem
        for (int j = 0; j < processed.size(); j++) {
            if (!MyMatrix.compare(x.get(j, 0), processedRhs[j][0], EPSILON)) {
                // if it is not equal - print it to the output and exit
                return new CheckResult.Failure(List.of(
                        "Error 3 " + (processed.size() + 1) + " " + j,
                        x.get(j, 0) + " " + processedRhs[j][0]));
            }
        }

        return new CheckResult.Success();
    }

    /**
     * Result of checking a processed matrix file.
     */
    public sealed interface CheckResult permits CheckResult.Success, CheckResult.Failure {

        /**
         * Successful check result.
         */
        record Success() implements CheckResult {
        }

        /**
         * Failed check result with the exact diagnostic lines printed by the
         * command-line checker.
         *
         * @param lines diagnostic lines describing the first detected problem
         */
        record Failure(List<String> lines) implements CheckResult {

            /**
             * Creates a failure result.
             *
             * @throws IllegalArgumentException if {@code lines} is {@code null} or empty
             */
            public Failure {
                if (lines == null || lines.isEmpty()) {
                    throw new IllegalArgumentException("Failure must contain at least one line");
                }
                lines = List.copyOf(lines);
            }

            /**
             * Creates a failure result containing one diagnostic line.
             *
             * @param line diagnostic line describing the problem
             */
            public Failure(String line) {
                this(List.of(line));
            }
        }
    }
}
