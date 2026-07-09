package pl.edu.agh.macwozni.matrixtw;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CheckerTest {

    @Test
    void acceptsCorrectProcessedMatrix(@TempDir Path tempDir) throws Exception {
        Path source = write(tempDir, "source.txt", """
                2
                1.0 0.0
                0.0 1.0
                2.0 3.0
                """);
        Path processed = write(tempDir, "processed.txt", """
                2
                1.0 0.0
                0.0 1.0
                2.0 3.0
                """);

        assertInstanceOf(Checker.CheckResult.Success.class, Checker.check(source, processed));
    }

    @Test
    void reportsInvalidIdentityDiagonal(@TempDir Path tempDir) throws Exception {
        Path source = write(tempDir, "source.txt", """
                2
                1.0 0.0
                0.0 1.0
                2.0 3.0
                """);
        Path processed = write(tempDir, "processed.txt", """
                2
                0.0 0.0
                0.0 1.0
                2.0 3.0
                """);

        Checker.CheckResult.Failure failure = assertInstanceOf(
                Checker.CheckResult.Failure.class,
                Checker.check(source, processed));

        assertEquals(List.of("Error 1 0 0"), failure.lines());
    }

    @Test
    void reportsWrongSolutionVector(@TempDir Path tempDir) throws Exception {
        Path source = write(tempDir, "source.txt", """
                2
                1.0 0.0
                0.0 1.0
                2.0 3.0
                """);
        Path processed = write(tempDir, "processed.txt", """
                2
                1.0 0.0
                0.0 1.0
                2.0 4.0
                """);

        Checker.CheckResult.Failure failure = assertInstanceOf(
                Checker.CheckResult.Failure.class,
                Checker.check(source, processed));

        assertEquals(List.of("Error 3 3 1", "3.0 4.0"), failure.lines());
    }

    private static Path write(Path directory, String fileName, String content) throws Exception {
        Path path = directory.resolve(fileName);
        Files.writeString(path, content);
        return path;
    }
}
