package com.wistrum.integrationapi.util;

import com.wistrum.integrationapi.model.IntegrationRequest;
import org.mariuszgromada.math.mxparser.Function;

import java.util.Arrays;

public class GaussLegendreQuadrature {
    private static final double EPSILON = 1e-10;
    private static final int MAX_ITERATIONS = 100;
    
    private final Function f;
    private final double lowerBound;
    private final double upperBound;
    private final int intervals;

    public GaussLegendreQuadrature(IntegrationRequest request) {
        this.f = new Function("f(x) = " + request.getFunction());
        this.lowerBound = request.getLowerBound();
        this.upperBound = request.getUpperBound();
        this.intervals = request.getIntervals();
    }

    public double integrate() {
        double[] nodes = findLegendreNodes(intervals);
        double[] weights = computeWeights(nodes);
        
        double integral = 0.0;
        double scale = (upperBound - lowerBound) / 2.0;
        double shift = (upperBound + lowerBound) / 2.0;
        
        for (int i = 0; i < intervals; i++) {
            double x_mapped = scale * nodes[i] + shift;
            integral += weights[i] * f.calculate(x_mapped);
        }
        return scale * integral;
    }

    private static double[] findLegendreNodes(int n) {
        double[] nodes = new double[n];
        
        for (int i = 0; i < n; i++) {
            double x = Math.cos(Math.PI * (i + 0.75) / (n + 0.5)); // Good initial estimate

            for (int iter = 0; iter < MAX_ITERATIONS; iter++) {
                double p = legendrePolynomial(n, x);
                double dp = legendreDerivative(n, x);
                
                if (Math.abs(dp) < EPSILON) break;
                
                double dx = p / dp;
                x -= dx;
                if (Math.abs(dx) < EPSILON) break;
            }
            nodes[i] = x;
        }
        Arrays.sort(nodes);
        return nodes;
    }

    private static double[] computeWeights(double[] nodes) {
        int n = nodes.length;
        double[] weights = new double[n];
        for (int i = 0; i < n; i++) {
            double x = nodes[i];
            double p_prime = legendreDerivative(n, x);
            weights[i] = 2.0 / ((1 - x * x) * p_prime * p_prime);
        }
        return weights;
    }

    public static double legendrePolynomial(int n, double x) {
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
        return n * (x * legendrePolynomial(n, x) - legendrePolynomial(n - 1, x)) / (x * x - 1);
    }
}
