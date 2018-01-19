package pl.edu.agh.macwozni.matrixtw;

import Jama.Matrix;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Checker {

    static boolean compare(double a, double b, double epsilon) {
        double c = a - b;
        if (c < 0.0) {
            c *= -1.0;
        }
        if (c < epsilon) {
            return true;
        }
        return false;
    }

    public static void main(String args[]) throws FileNotFoundException, IOException {

        double epsilon = 0.00001;

        //read source file
        File fil = new File(args[0]);
        FileReader inputFil = new FileReader(fil);
        BufferedReader in = new BufferedReader(inputFil);

        String s = in.readLine();

        int size = Integer.parseInt(s);
        double[][] lhs = new double[size][size];
        double[][] rhs = new double[size][1];

        for (int i = 0; i < size; i++) {
            s = in.readLine();
            String[] sp = s.split(" ");
            for (int j = 0; j < size; j++) {
                lhs[i][j] = Double.parseDouble(sp[j]);
            }
        }
        s = in.readLine();
        String[] sp = s.split(" ");
        for (int j = 0; j < size; j++) {
            rhs[j][0] = Double.parseDouble(sp[j]);
        }

        //solve
        Matrix A = new Matrix(lhs);
        Matrix b = new Matrix(rhs);
        Matrix x = A.solve(b);

        //read output file
        fil = new File(args[1]);
        inputFil = new FileReader(fil);
        in = new BufferedReader(inputFil);

        s = in.readLine();

        size = Integer.parseInt(s);
        double[][] lhs_r = new double[size][size];
        double[][] rhs_r = new double[size][1];

        for (int i = 0; i < size; i++) {
            s = in.readLine();
            sp = s.split(" ");
            for (int j = 0; j < size; j++) {
                lhs_r[i][j] = Double.parseDouble(sp[j]);
            }
        }
        s = in.readLine();
        sp = s.split(" ");
        for (int j = 0; j < size; j++) {
            rhs_r[j][0] = Double.parseDouble(sp[j]);
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    if (!compare(1., lhs_r[i][j], epsilon)) {
                        System.out.println("Error " + i + " " + j);
                        System.exit(0);
                    }
                } else if (!compare(0., lhs_r[i][j], epsilon)) {
                    System.out.println("Error " + i + " " + j);
                    System.exit(0);
                }
            }
        }

        for (int j = 0; j < size; j++) {
            if (!compare(x.getArray()[j][0], rhs_r[j][0], epsilon)) {
                System.out.println("Error " + (size+1) + " " + j);
                System.exit(0);
            }
        }
    }
}
