package com.wistrum.integrationapi.util;

import com.wistrum.integrationapi.model.IntegrationMethod;
import com.wistrum.integrationapi.model.IntegrationRequest;
import org.mariuszgromada.math.mxparser.Function;
import org.mariuszgromada.math.mxparser.License;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class NumericalIntegrator {
	static {
        License.iConfirmNonCommercialUse("wistrum");
    }
    private static final long TIME_LIMIT_MS = 10000; // 10-second timeout
    private static final long MEMORY_LIMIT_MB = 100; // Max 100MB usage
    
    public double integrate(IntegrationRequest request) throws Exception {
    	
        // Validate input
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null.");
        }
        if (request.getLowerBound() > request.getUpperBound()) {
            throw new IllegalArgumentException("Lower bound must be less than or equal to the upper bound.");
        }
        if (request.getIntervals() < 1) {
        	throw new IllegalArgumentException("Intervals must be at least 1");
        }
        // Validate function syntax
        Function f = new Function("f(x) = " + request.getFunction());
        if (!f.checkSyntax()) {
            throw new IllegalArgumentException("Invalid function syntax: " + f.getErrorMessage());
        }

        // Check for division by zero or singularities
        checkForDivisionByZero(f, request.getLowerBound(), request.getUpperBound());

        // Enforce timeout and memory limits
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Double> future = executor.submit(() -> {
            enforceMemoryLimit();
            return executeIntegration(request);
        });

        try {
            return future.get(TIME_LIMIT_MS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new TimeoutException("Integration exceeded time limit (" + TIME_LIMIT_MS + " ms)");
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof ArithmeticException) {
                throw new ArithmeticException("Integration failed: " + cause.getMessage());
            } else {
                throw new RuntimeException("Integration error: " + cause.getMessage());
            }
        } finally {
            executor.shutdown();
        }
    }

    private double executeIntegration(IntegrationRequest request) {
        IntegrationMethod integrationMethod = request.getIntegrationMethod();
        System.out.println(request.getIntegrationMethod().toString());
        switch (integrationMethod) {
            case TRAPEZOIDAL:
                return new TrapezoidalIntegrator(request).integrate();
            case SIMPSON:
                return new SimpsonIntegrator(request).integrate();
            case MIDPOINT:
                return new MidpointIntegrator(request).integrate();
            case LOBATTO_QUADRATURE:
                return new LobattoQuadrature(request).integrate();
            case GAUSS_LEGENDRE_QUADRATURE:
                return new GaussLegendreQuadrature(request).integrate();
            default:
                throw new IllegalArgumentException("Unknown or unimplemented integration method.");
        }
    }

    private void enforceMemoryLimit() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        long usedMemoryMB = heapMemoryUsage.getUsed() / (1024 * 1024);

        if (usedMemoryMB > MEMORY_LIMIT_MB) {
            throw new RuntimeException("Memory usage exceeded limit (" + MEMORY_LIMIT_MB + " MB)");
        }
    }

    private void checkForDivisionByZero(Function f, double lowerBound, double upperBound) {
        String functionStr = f.getFunctionExpressionString();
        
        // Try extracting denominator (assumes format like "p(x)/q(x)")
        String denominator = extractDenominator(functionStr);
        if (denominator == null) return; // No denominator, no division by zero risk

        // Solve q(x) = 0
        Function q = new Function("q(x) = " + denominator);
        for (double x : findRoots(q, lowerBound, upperBound)) {
            if (x >= lowerBound && x <= upperBound) {
                throw new ArithmeticException("Singularity detected at x = " + x);
            }
        }
    }

    // Extract denominator from "p(x)/q(x)" format
    private String extractDenominator(String function) {
        int slashIndex = function.indexOf('/');
        if (slashIndex == -1) return null; // No division present
        
        return function.substring(slashIndex + 1).trim(); // Assume simple p(x)/q(x) structure
    }

    // Solve q(x) = 0 numerically (brute-force approach, but could use symbolic solving)
    private List<Double> findRoots(Function q, double lower, double upper) {
        List<Double> roots = new ArrayList<>();
        double stepSize = (upper - lower) / 10000.0; // Fine-grained search
        double prevVal = q.calculate(lower);

        for (double x = lower + stepSize; x <= upper; x += stepSize) {
            double currVal = q.calculate(x);
            if (Double.isNaN(currVal) || Double.isInfinite(currVal)) continue;

            if (Math.signum(prevVal) != Math.signum(currVal)) { 
                // A root exists between (x - stepSize) and x
                roots.add(x - stepSize / 2.0); // Approximate root location
            }
            prevVal = currVal;
        }
        return roots;
    }

}
