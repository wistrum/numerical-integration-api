package com.wistrum.integrationapi.util;

import com.wistrum.integrationapi.model.IntegrationRequest;
import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.Argument;
import java.util.function.Function;

public class LobattoQuadrature {
    private final Function<Double, Double> function;
    private final double lowerBound;
    private final double upperBound;
    private final int intervals;

    public LobattoQuadrature(IntegrationRequest request) {
        this.lowerBound = request.getLowerBound();
        this.upperBound = request.getUpperBound();
        this.intervals = request.getIntervals();
        this.function = parseFunction(request.getFunction());
    }

    private Function<Double, Double> parseFunction(String functionStr) {
        return x -> {
            Argument argX = new Argument("x", x);
            Expression expression = new Expression(functionStr, argX);
            return expression.calculate();
        };
    }

    //Legendre Polynomial P_n(x)
    private double legendrePolynomial(int n, double x) {
        if (n == 0) return 1;
        if (n == 1) return x;
        return ((2 * n - 1) * x * legendrePolynomial(n - 1, x) - (n - 1) * legendrePolynomial(n - 2, x)) / n;
    }

    //Derivative P_n'(x)
    private double legendreDerivative(int n, double x) {
        return n * (legendrePolynomial(n - 1, x) - x * legendrePolynomial(n, x)) / (1 - x * x);
    }

    //Lobatto Nodes (including endpoints)
    private double[] findLobattoNodes(int n) {
        double[] nodes = new double[n];
        nodes[0] = -1;
        nodes[n - 1] = 1;

        for (int i = 1; i < n - 1; i++) {
            double x = Math.cos(Math.PI * (i + 0.5) / (n - 1)); // Initial guess
            double xPrev;
            do {
                xPrev = x;
                x -= legendrePolynomial(n - 1, x) / legendreDerivative(n - 1, x);
            } while (Math.abs(x - xPrev) > 1e-9);
            nodes[i] = x;
        }
        return nodes;
    }

    //Weights
    private double[] computeWeights(int n, double[] nodes) {
        double[] weights = new double[n];
        weights[0] = 2.0 / (n * (n - 1));
        weights[n - 1] = weights[0];

        for (int i = 1; i < n - 1; i++) {
            double Pn_1 = legendrePolynomial(n - 1, nodes[i]);
            weights[i] = 2.0 / (n * (n - 1) * Pn_1 * Pn_1);
        }
        return weights;
    }

    public double integrate() {
        int n = intervals;
        if (n < 2) throw new IllegalArgumentException("Intervals must be at least 2");

        double[] nodes = findLobattoNodes(n);
        double[] weights = computeWeights(n, nodes);
        double sum = 0.0;

        for (int i = 0; i < n; i++) {
            double xi = (upperBound - lowerBound) / 2 * nodes[i] + (lowerBound + upperBound) / 2;
            sum += weights[i] * function.apply(xi);
        }
        return (upperBound - lowerBound) / 2 * sum;
    }
}
