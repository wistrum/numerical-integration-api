package com.wistrum.integrationapi.util;

import com.wistrum.integrationapi.model.IntegrationRequest;
import org.mariuszgromada.math.mxparser.Function;

public class LobattoQuadrature {
    private final Function f;
    private final double lowerBound;
    private final double upperBound;
    private final int intervals;

    public LobattoQuadrature(IntegrationRequest request) {
        this.lowerBound = request.getLowerBound();
        this.upperBound = request.getUpperBound();
        this.intervals = request.getIntervals();
        this.f = new Function("f(x) = " + request.getFunction());

        if (!f.checkSyntax()) {
            throw new IllegalArgumentException(
            		"Invalid function syntax: " + request.getFunction());
        }

        validateFunction();
    }

    private void validateFunction() {
        double midpoint = (lowerBound + upperBound) / 2;
        double testEval = f.calculate(midpoint);
        
        if (Double.isNaN(testEval) || Double.isInfinite(testEval)) {
            throw new IllegalArgumentException(
            		"Function evaluation failed at midpoint: f(" + midpoint + ")");
        }
        
        // check for common singularities
        if (lowerBound <= 0 && upperBound >= 0 
        		&& f.getFunctionExpressionString().contains("/x")) {
            throw new IllegalArgumentException(
            		"Function contains division by zero risk at x=0.");
        }
    }

    private double legendrePolynomial(int n, double x) {
        if (n == 0) return 1.0;
        if (n == 1) return x;
        double p0 = 1.0, p1 = x, p2 = 0.0;
        for (int k = 2; k <= n; k++) {
            p2 = ((2 * k - 1) * x * p1 - (k - 1) * p0) / k;
            p0 = p1;
            p1 = p2;
        }
        return p1;
    }

    private double legendreDerivative(int n, double x) {
        return n * (legendrePolynomial(n - 1, x) - x 
        		* legendrePolynomial(n, x)) / (1 - x * x);
    }

    private double[] findLobattoNodes(int n) {
        double[] nodes = new double[n];
        nodes[0] = -1;
        nodes[n - 1] = 1;

        for (int i = 1; i < n - 1; i++) {
            double x = Math.cos(Math.PI * (i + 0.5) / (n - 1));//initial guess
            double xPrev;
            int iterations = 0;

            do {
                xPrev = x;
                double p = legendrePolynomial(n - 1, x);
                double dp = legendreDerivative(n - 1, x);

                if (Math.abs(dp) < 1e-12) {//avoid division by near-zero
                    break;
                }

                x -= p / dp;
                iterations++;

                if (iterations >= 100) {
                    throw new ArithmeticException("Newton’s method failed to converge for Lobatto node at i=" + i);
                }

            } while (Math.abs(x - xPrev) > 1e-9);

            nodes[i] = x;
        }
        return nodes;
    }

    private double[] computeWeights(int n, double[] nodes) {
        double[] weights = new double[n];
        weights[0] = 2.0 / (n * (n - 1));
        weights[n - 1] = weights[0];

        for (int i = 1; i < n - 1; i++) {
            double Pn_1 = legendrePolynomial(n - 1, nodes[i]);
            weights[i] = 2.0 / ((n - 1) * (n - 1) * Pn_1 * Pn_1);
        }
        return weights;
    }

    public double integrate() {
        int n = intervals;
        if (n < 2 || n > 5000) { // Capped for performance
            throw new IllegalArgumentException(
            		"Intervals must be at least 2 and at most 5000.");
        }

        if (Math.abs(upperBound - lowerBound) < 1e-12) {
            return 0.0; //interval too small, return 0, avoid numerical issues
        }

        double[] nodes = findLobattoNodes(n);
        double[] weights = computeWeights(n, nodes);
        double sum = 0.0;

        for (int i = 0; i < n; i++) {
            double xi = (upperBound - lowerBound) / 2 
            		* nodes[i] + (lowerBound + upperBound) / 2;
            double fVal = f.calculate(xi);

            if (Double.isNaN(fVal) || Double.isInfinite(fVal)) {
                throw new ArithmeticException(
                		"Function evaluation failed at x=" + xi);
            }

            sum += weights[i] * fVal;
        }

        return (upperBound - lowerBound) / 2 * sum;
        //does this even work????
    }
}
