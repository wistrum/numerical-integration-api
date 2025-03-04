package com.wistrum.integrationapi.util;

import com.wistrum.integrationapi.model.IntegrationRequest;
import org.mariuszgromada.math.mxparser.Function;
import java.util.ArrayList;
import java.util.List;

public class MidpointIntegrator {
    private static final int MAX_INTERVALS = 1_000_000;
    private static final double MAX_FUNCTION_VALUE = 1e150;

    private final IntegrationRequest request;
    private final Function f;

    public MidpointIntegrator(IntegrationRequest request) {
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

        double sum = 0.0;
        
        for (int i = 0; i < intervals; i++) {
            double midPoint = lowerBound + (i + 0.5) * stepSize;
            double fMid = evaluateFunctionSafely(midPoint);
            sum += fMid;
        }
        return sum * stepSize;
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
