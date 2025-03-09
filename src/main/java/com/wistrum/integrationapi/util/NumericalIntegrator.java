package com.wistrum.integrationapi.util;

import com.wistrum.integrationapi.model.IntegrationMethod;
import com.wistrum.integrationapi.model.IntegrationRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import org.mariuszgromada.math.mxparser.Function;
import org.mariuszgromada.math.mxparser.License;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.expression.AST;


public class NumericalIntegrator {
	static {
        License.iConfirmNonCommercialUse("wistrum");
    }
    private static final long TIME_LIMIT_MS = 20000; // 20-second timeout
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
        
        // Try extracting denominator
        String denominator = extractDenominator(functionStr);
        if (denominator == null) return; // No denominator, no division by zero risk
        //Check if denominator evaluates to 0 anywhere within bounds
        for (double x : findRoots(denominator)) {
            if (x >= lowerBound && x <= upperBound) {
                throw new ArithmeticException("Singularity detected at x = " + x);
            }
        }
    }

    // Extract denominator from "p(x)/q(x)" format
    private String extractDenominator(String function) {
    	ExprEvaluator evaluator = new ExprEvaluator();
        IExpr result = evaluator.evaluate("Simplify(" + function + ")");//get p(x)/q(x) format
        String simplified = result.toScript();
        int slashIndex = simplified.indexOf('/');
        if (slashIndex == -1) return null; // No division present
        
        return simplified.substring(slashIndex + 1).trim();
    }

    // Solve denominator = 0
    private List<Double> findRoots(String denominator) {
        List<Double> roots = new ArrayList<>();
        ExprEvaluator evaluator = new ExprEvaluator(false, (short) 100);
        String equation = "Solve(" + denominator + "== 0, x)";
        IExpr result = evaluator.evaluate(equation);
        if (result.isAST()) {  // Ensure result is a list of solutions
            AST list = (AST) result;
            for (IExpr solution : list) {
                if (solution.isAST() && solution.size() == 2) {
                    IExpr value = solution.getAt(1);
                    if (value.last().isNumber()) {
                        roots.add(value.last().toDoubleDefault());
                    }
                }
            }
        }
        return roots;
    }

}
