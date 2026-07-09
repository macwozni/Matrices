package pl.edu.agh.macwozni.matrixtw;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.ejml.simple.SimpleMatrix;

/**
 * @author macwozni
 */
public class Checker {

    // machine precision epsilon
    static final double EPSILON = 0.00001;

    /**
     * @param args should be 2 strings with addresses of 2 files with matrixes at given format
     * @throws IOException if there is some problem with IO we just frow exception - from main subroutine...
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

    public sealed interface CheckResult permits CheckResult.Success, CheckResult.Failure {

        record Success() implements CheckResult {
        }

        record Failure(List<String> lines) implements CheckResult {

            public Failure {
                if (lines == null || lines.isEmpty()) {
                    throw new IllegalArgumentException("Failure must contain at least one line");
                }
                lines = List.copyOf(lines);
            }

            public Failure(String line) {
                this(List.of(line));
            }
        }
    }
}
