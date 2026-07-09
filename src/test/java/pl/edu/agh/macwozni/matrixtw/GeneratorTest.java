package pl.edu.agh.macwozni.matrixtw;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GeneratorTest {

    @Test
    void detectsPivotRequirement() {
        double[][] matrix = {
            {0.0, 1.0},
            {1.0, 0.0}
        };

        assertTrue(Generator.requiresPivot(matrix, 2));
    }

    @Test
    void pivotCheckDoesNotMutateInput() {
        double[][] matrix = {
            {2.0, 1.0},
            {4.0, 3.0}
        };
        double[][] original = copy(matrix);

        assertFalse(Generator.requiresPivot(matrix, 2));
        assertArrayEquals(original[0], matrix[0]);
        assertArrayEquals(original[1], matrix[1]);
    }

    @Test
    void mainWritesFilesWithoutChangingStandardOutput(@TempDir Path tempDir) throws Exception {
        Path input = tempDir.resolve("input.txt");
        Path reference = tempDir.resolve("reference.txt");
        PrintStream originalOut = System.out;

        Generator.main(new String[] {"2", input.toString(), reference.toString()});

        assertSame(originalOut, System.out);
        assertTrue(Files.size(input) > 0);
        assertTrue(Files.size(reference) > 0);
        assertInstanceOf(Checker.CheckResult.Success.class, Checker.check(input, reference));
    }

    private static double[][] copy(double[][] matrix) {
        double[][] copy = new double[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            copy[i] = matrix[i].clone();
        }
        return copy;
    }
}
