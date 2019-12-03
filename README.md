Tool for checking students implementation of Gaussian Elimination
===========================

Generator
-------------------------

This is a tool to generate a system of linear equations.
Besides generating system of equation it checks if this is singular and if it requires pivoting due to having numerical zero on diagonal during elimination.

Input parameters to invoke:  
[size of matrix - integer] [address of output file with system of equations - string] [address of output file with solved system of equations - string]

Checker
-------------------------

This is a tool to check if solution from students implementation is correct. It checks if differences between solutions are numerical zero.

Input parameters to invoke:  
[address of input file with system of equations - string] [address of input file with solved system of equations - string]

Numerical zero
-------------------------

We defin numerical zero as everything smaller than 0.00001.
This is controlled by epsilon varaible both in Generator and Checker independently.

Project type
-------------------------

This is a simple Maven project. Compile with mvn, or any IDE that support Maven - for example: InteliJ, Eclipse, NetBeans and many more.

Usage permission
-------------------------

This is intended to be used during "Teoria Wspolbieznosci" classes.

