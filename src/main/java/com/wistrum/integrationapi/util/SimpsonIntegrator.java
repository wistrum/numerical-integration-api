package com.wistrum.integrationapi.util;

import com.wistrum.integrationapi.model.IntegrationRequest;
import org.mariuszgromada.math.mxparser.Function;
import java.util.ArrayList;
import java.util.List;

public class SimpsonIntegrator {
    private static final int MAX_INTERVALS = 1_000_000;
    private static final double MAX_FUNCTION_VALUE = 1e150;

    private final IntegrationRequest request;
    private final Function f;

    public SimpsonIntegrator(IntegrationRequest request) {
        this.request = request;
        
        // Validate function syntax
        this.f = new Function("f(x) = " + request.getFunction());
        if (!f.checkSyntax()) {
            throw new IllegalArgumentException(
            		"Invalid function syntax: " + request.getFunction());
        }
    }

    public double integrate() {
        validateInputs();
        checkFunctionStability();

        double lowerBound = request.getLowerBound();
        double upperBound = request.getUpperBound();
        int intervals = request.getIntervals();
        double stepSize = Math.fma(1.0 / intervals, (upperBound - lowerBound), 0);

        double fa = evaluateFunctionSafely(lowerBound);
        double fb = evaluateFunctionSafely(upperBound);
        double sum = fa + fb;

        // Sum for odd indices (4*f(x))
        for (int i = 1; i < intervals; i += 2) {
            double xi = lowerBound + i * stepSize;
            sum += 4 * evaluateFunctionSafely(xi);
        }

        // Sum for even indices (2*f(x))
        for (int i = 2; i < intervals; i += 2) {
            double xi = lowerBound + i * stepSize;
            sum += 2 * evaluateFunctionSafely(xi);
        }

        return (stepSize / 3) * sum;
    }

    private void validateInputs() {
        double lowerBound = request.getLowerBound();
        double upperBound = request.getUpperBound();
        int intervals = request.getIntervals();

        if (Double.isNaN(lowerBound) || Double.isNaN(upperBound)) {
            throw new IllegalArgumentException("Integration bounds cannot be NaN");
        }
        if (lowerBound >= upperBound) {
            throw new IllegalArgumentException("Lower bound must be less than upper bound");
        }
        if (intervals < 1) {
            throw new IllegalArgumentException("At least one interval is required");
        }
        if (intervals > MAX_INTERVALS) {
            throw new IllegalArgumentException("Maximum " + MAX_INTERVALS + " intervals allowed");
        }
        if (intervals % 2 != 0) {
            throw new IllegalArgumentException("Intervals must be even for Simpson's rule.");
        }
    }

    private void checkFunctionStability() {
        double lowerBound = request.getLowerBound();
        double upperBound = request.getUpperBound();
        
        double[] testPoints = {
            lowerBound, 
            upperBound, 
            (lowerBound + upperBound) / 2,
            lowerBound + (upperBound - lowerBound) / 4,
            upperBound - (upperBound - lowerBound) / 4
        };
        
        List<String> instabilityReasons = new ArrayList<>();
        for (double x : testPoints) {
            try {
                double result = f.calculate(x);
                if (Double.isInfinite(result) || Math.abs(result) > MAX_FUNCTION_VALUE) {
                    instabilityReasons.add("Extreme value at x = " + x);
                }
            } catch (Exception e) {
                instabilityReasons.add("Error at x = " + x + ": " + e.getMessage());
            }
        }
        if (!instabilityReasons.isEmpty()) {
            throw new ArithmeticException(
            		"Function instability detected: " + String.join("; ", instabilityReasons));
        }
    }

    private double evaluateFunctionSafely(double x) {
        try {
            double result = f.calculate(x);
            if (Double.isNaN(result) || Double.isInfinite(result) 
            		|| Math.abs(result) > MAX_FUNCTION_VALUE) {
                throw new ArithmeticException("Function may be discontinuous near x = " + x);
            }
            return result;
        } catch (Exception e) {
            throw new ArithmeticException(
            		"Function evaluation failed at x = " + x + ": " + e.getMessage());
        }
    }
}
