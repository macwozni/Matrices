MatrixTW
========

MatrixTW is a small Java project used during the "Teoria Wspolbieznosci"
classes. It helps prepare and verify test cases for student implementations of
Gaussian elimination.

The project contains two command-line tools:

- `Generator` creates a random system of linear equations.
- `Checker` verifies whether a processed result file contains the expected
  solution.

Project Structure
-----------------

```text
src/main/java/pl/edu/agh/macwozni/matrixtw/
  Generator.java  Generates input and reference output files.
  Checker.java    Checks a student's output file.
  MyMatrix.java   Represents parsed matrix files and compares floating-point values.
src/test/java/pl/edu/agh/macwozni/matrixtw/
  *Test.java      Unit tests for parsing, generation, and checking.
```

The project uses the EJML matrix library and JUnit tests. It can be built with
either Maven or the included Gradle wrapper.

Requirements
------------

- JDK 26 or newer
- Maven 3.9 or newer
- Gradle 9.6.1 or newer, or the included Gradle wrapper

Build
-----

Compile the project with Maven:

```bash
mvn compile
```

The Maven build enforces JDK 26 or newer and compiles the project with
`--release 26`.

Compile the project with Gradle:

```bash
./gradlew compileJava
```

The Gradle build uses the Java 26 toolchain and also compiles with
`--release 26`.

Run the unit test suite with Maven:

```bash
mvn test
```

Run the unit test suite with Gradle:

```bash
./gradlew test
```

Generate the API documentation with Maven:

```bash
mvn javadoc:javadoc
```

The Maven documentation is written to `target/reports/apidocs/`.

Generate the API documentation with Gradle:

```bash
./gradlew javadoc
```

The Gradle documentation is written to `build/docs/javadoc/`.

You can also open the project in any IDE with Maven support, such as IntelliJ
IDEA, Eclipse, or NetBeans.

Matrix File Format
------------------

Matrix files use the following text format:

```text
<size>
<matrix row 1>
<matrix row 2>
...
<matrix row n>
<right-hand-side vector>
```

Example:

```text
3
1.2 1.5 1.6
2.3 2.9 2.0
3.0 3.1 3.3
0.1 0.2 0.3
```

This represents a system with a `3 x 3` matrix:

```text
1.2 1.5 1.6
2.3 2.9 2.0
3.0 3.1 3.3
```

and a right-hand-side vector:

```text
0.1 0.2 0.3
```

Generator
---------

`Generator` creates:

1. An input file containing a generated system of linear equations `A * x = b`.
2. A reference output file containing the identity matrix and the correct
   solution vector `x`.

Arguments:

```text
<matrix size> <input output file> <reference output file>
```

Example:

```bash
mvn exec:java \
  -Dexec.mainClass=pl.edu.agh.macwozni.matrixtw.Generator \
  -Dexec.args="10 out10.txt res10.txt"
```

Gradle example:

```bash
./gradlew runGenerator --args="10 out10.txt res10.txt"
```

The generated matrix is checked before being written. The generator avoids
singular systems and systems that would require pivoting because of a numerical
zero on the diagonal during simple Gaussian elimination.

Checker
-------

`Checker` verifies a processed result file produced by a student's Gaussian
elimination implementation.

Arguments:

```text
<input file with original system> <processed result file>
```

Example:

```bash
mvn exec:java \
  -Dexec.mainClass=pl.edu.agh.macwozni.matrixtw.Checker \
  -Dexec.args="out10.txt student_result.txt"
```

Gradle example:

```bash
./gradlew runChecker --args="out10.txt student_result.txt"
```

The checker:

1. Reads the original system.
2. Solves it using the EJML library.
3. Reads the processed result file.
4. Checks whether the processed matrix is the identity matrix.
5. Checks whether the processed right-hand-side vector matches the expected
   solution.

If the result is incorrect, the checker prints one of the following errors:

- `Error 1 i j` - the diagonal element at row `i`, column `j` is not equal to
  `1.0`.
- `Error 2 i j` - a non-diagonal element at row `i`, column `j` is not equal to
  `0.0`.
- `Error 3 i j` - the solution vector differs from the expected solution.
- `Error size expected actual` - the processed matrix size differs from the
  original matrix size.

The checker exits with status code `0` when the result is correct and `1` when
an error is detected.

Numerical Precision
-------------------

Floating-point values are compared with an epsilon tolerance:

```text
0.0000000001
```

Two values are treated as equal when their absolute difference is smaller than
this tolerance.

Example Files
-------------

The repository contains example files:

- `out10.txt` - an example generated system.
- `res10.txt` - the corresponding reference result.

License / Usage
---------------

This project is intended for educational use during the "Teoria Wspolbieznosci"
classes.
For noncommercial use only.
