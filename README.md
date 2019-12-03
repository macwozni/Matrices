Tool for checking students implementation of Gaussian Elimination
===========================

Generator
-------------------------
This is a tool to generate a system of linear equations.Besides generating system of equation it checks if this is singular and if it requires pivoting due to having numerical zero on diagonal during elimination.

Input parameters to invoke:  
[size of matrix - integer] [address of output file with system of equations - string] [address of output file with solved system of equations - string]

Checker
-------------------------

This is a tool to check if solution from students implementation is correct. It checks if differences between solutions are numerical zero.

Input parameters to invoke:  
[address of input file with system of equations - string] [address of input file with solved system of equations - string]

Numerical zero
-------------------------

We define numerical zero as everything smaller than 0.00001.This is controlled by epsilon variable both in Generator and Checker independently.

Matrix file format
-------------------------

Files are formatted in following manner  
[size - integer]  
[matrix - double]  
[RHS - double]

For example:

3  
1.2 1.5 1.6  
2.3 2.9 2.0  
3.0 3.1 3.3  
0.1 0.2 0.3

where matrix is of size 3 and has following value  
1.2 1.5 1.6  
2.3 2.9 2.0  
3.0 3.1 3.3  
and RHS has following value  
0.1 0.2 0.3


Project type
-------------------------

This is a simple Maven project. Compile with mvn, or any IDE that support Maven - for example: IntelliJ, Eclipse, NetBeans and many more.

Usage permission
-------------------------

This is intended to be used during "Teoria Wspolbieznosci" classes.
