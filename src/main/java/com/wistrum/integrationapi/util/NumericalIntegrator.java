package com.wistrum.integrationapi.util;

import com.wistrum.integrationapi.model.IntegrationMethod;
import com.wistrum.integrationapi.model.IntegrationRequest;
import org.mariuszgromada.math.mxparser.Function;

import java.util.concurrent.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class NumericalIntegrator {
    private static final long TIME_LIMIT_MS = 10000; // 10-second timeout
    private static final long MEMORY_LIMIT_MB = 100; // Max 100MB usage

    public double integrate(IntegrationRequest request) throws Exception {
        // Validate input
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (request.getLowerBound() > request.getUpperBound()) {
            throw new IllegalArgumentException("Lower bound must be less than or equal to the upper bound.");
        }

        // Validate function syntax
        Function f = new Function("f(x) = " + request.getFunction());
        if (!f.checkSyntax()) {
            throw new IllegalArgumentException("Invalid function syntax: " + f.getErrorMessage());
        }

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
                throw new IllegalArgumentException("Unknown or unimplemented integration method");
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
}
