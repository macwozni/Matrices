package pl.edu.agh.macwozni.matrixtw;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

class MyMatrixTest {

    @Test
    void parsesWhitespaceSeparatedMatrix() throws IOException {
        MyMatrix matrix = new MyMatrix(reader("""
                2
                1.0   0.0
                0.0 1.0
                3.0   4.0
                """));

        assertEquals(2, matrix.size());
        assertArrayEquals(new double[] {1.0, 0.0}, matrix.lhs()[0]);
        assertArrayEquals(new double[] {0.0, 1.0}, matrix.lhs()[1]);
        assertArrayEquals(new double[] {3.0}, matrix.rhs()[0]);
        assertArrayEquals(new double[] {4.0}, matrix.rhs()[1]);
    }

    @Test
    void rejectsWrongRowLength() {
        IOException exception = assertThrows(IOException.class, () -> new MyMatrix(reader("""
                2
                1.0
                0.0 1.0
                3.0 4.0
                """)));

        assertTrue(exception.getMessage().contains("Expected 2 values in matrix row 0"));
    }

    @Test
    void rejectsInvalidNumbersWithContext() {
        IOException exception = assertThrows(IOException.class, () -> new MyMatrix(reader("""
                1
                not-a-number
                3.0
                """)));

        assertTrue(exception.getMessage().contains("Invalid number in matrix row 0, column 0"));
    }

    @Test
    void copiesInputArraysDefensively() {
        double[][] lhs = {{2.0}};
        double[][] rhs = {{3.0}};

        MyMatrix matrix = new MyMatrix(1, lhs, rhs);
        lhs[0][0] = 9.0;
        rhs[0][0] = 10.0;

        assertEquals(2.0, matrix.lhs()[0][0]);
        assertEquals(3.0, matrix.rhs()[0][0]);

        double[][] exposedLhs = matrix.lhs();
        exposedLhs[0][0] = 11.0;

        assertEquals(2.0, matrix.lhs()[0][0]);
    }

    private static BufferedReader reader(String text) {
        return new BufferedReader(new StringReader(text));
    }
}
