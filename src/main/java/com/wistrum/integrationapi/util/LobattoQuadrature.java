package com.wistrum.integrationapi.util;

import com.wistrum.integrationapi.model.IntegrationRequest;
import org.mariuszgromada.math.mxparser.Function;
import java.util.Arrays;

public class LobattoQuadrature {
    private static final double EPSILON = 1e-10;
    private static final int MAX_ITERATIONS = 1000;
    
    private final Function f;
    private final double lowerBound;
    private final double upperBound;
    private final int intervals;

    public LobattoQuadrature(IntegrationRequest request) {
        this.f = new Function("f(x) = " + request.getFunction());
        if (!f.checkSyntax()) {
            throw new IllegalArgumentException("Invalid function syntax: " + request.getFunction());
        }
        this.lowerBound = request.getLowerBound();
        this.upperBound = request.getUpperBound();
        this.intervals = request.getIntervals();
    }


    public double integrate() {
        if (intervals < 2) throw new IllegalArgumentException(
        		"Lobatto quadrature requires at least two nodes.");
        
        double[] nodes = findLobattoNodes(intervals);
        double[] weights = computeWeights(nodes);
        
        double integral = 0.0;
        for (int i = 0; i < intervals; i++) {
            double x_mapped = (upperBound - lowerBound) / 2 * nodes[i] 
            		+ (lowerBound + upperBound) / 2;
            double fVal = f.calculate(x_mapped);
            if (Double.isNaN(fVal) || Double.isInfinite(fVal)) {
                throw new ArithmeticException("Function evaluation failed at x=" + x_mapped);
            }
            integral += weights[i] * fVal;
        }
        return (upperBound - lowerBound) / 2 * integral;
    }

    private static double[] findLobattoNodes(int n) {
        double[] nodes = new double[n];
        nodes[0] = -1.0;
        nodes[n - 1] = 1.0;
        
        for (int i = 1; i < n - 1; i++) {
            double x = Math.cos(Math.PI * (i + 0.5) / (n - 1));
            for (int iter = 0; iter < MAX_ITERATIONS; iter++) {
                double p = legendrePolynomial(n - 1, x);
                double dp = legendreDerivative(n - 1, x);
                
                if (Math.abs(dp) < EPSILON) break;
                
                double dx = p / dp;
                if (Math.abs(dx) < EPSILON) break;
                x -= dx;
            }
            nodes[i] = x;
        }
        return nodes;
    }

    private static double[] computeWeights(double[] nodes) {
        int n = nodes.length;
        double[] weights = new double[n];
        for (int i = 0; i < n; i++) {
            double x = nodes[i];
            double p_prime = legendreDerivative(n - 1, x);
            if (Math.abs(p_prime) < EPSILON) throw new ArithmeticException(
            		"Zero derivative encountered in weight computation");
            weights[i] = 2.0 / ((1 - x * x + EPSILON) * p_prime * p_prime);
        }
        return weights;
    }

    private static double legendrePolynomial(int n, double x) {
        if (n == 0) return 1.0;
        if (n == 1) return x;
        double p0 = 1.0, p1 = x, p2;
        for (int k = 2; k <= n; k++) {
            p2 = ((2.0 * k - 1) * x * p1 - (k - 1) * p0) / k;
            p0 = p1;
            p1 = p2;
        }
        return p1;
    }

    private static double legendreDerivative(int n, double x) {
        if (Math.abs(x) == 1.0) {
            return 0.5 * n * (n + 1) * (x == 1 ? 1 : -1);
        }
        return n * (x * legendrePolynomial(n, x) - legendrePolynomial(n - 1, x)) 
        		/ (x * x - 1 + EPSILON);
    }
}
