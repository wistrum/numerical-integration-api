package com.wistrum.integrationapi.util;

import com.wistrum.integrationapi.model.IntegrationMethod;
import com.wistrum.integrationapi.model.IntegrationRequest;

import java.util.concurrent.*;

import org.mariuszgromada.math.mxparser.*;

public class NumericalIntegrator {
	private static final long TIME_LIMIT_MS = 10000; //10 Second timeout
	private static final long MEMORY_LIMIT_MB = 100; //max 100mb usage
	
	public double integrate(IntegrationRequest request) throws Exception{
		//Exceptions
		if(request == null) {
			throw new IllegalArgumentException("Request cannot be null");
		}
		if (request.getLowerBound() > request.getUpperBound()) {
            throw new IllegalArgumentException(
            		"Lower bound must be less than or equal to the upper bound.");
        }
		Function f = new Function("f(x) = " + request.getFunction());
		if(!f.checkSyntax()) {
			throw new IllegalArgumentException(
					"Invalid function syntax" + f.getErrorMessage());
		}
		
		//Enforce timeout
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<Double> future = executor.submit(() -> {
            enforceMemoryLimit();
            return executeIntegration(request);
        });
		
		try {
            return future.get(TIME_LIMIT_MS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new RuntimeException("Integration exceeded time limit (" 
            		+ TIME_LIMIT_MS + " ms)");
        } catch (ExecutionException e) {
            throw new RuntimeException("Integration error: " 
            		+ e.getCause().getMessage());
        } finally {
            executor.shutdown();
        }
	}
	
	private double executeIntegration(IntegrationRequest request) {
		IntegrationMethod integrationMethod = request.getIntegrationMethod();
		switch(integrationMethod) {
			case TRAPEZOIDAL:
				TrapezoidalIntegrator trapezoidalIntegrator = new TrapezoidalIntegrator(request);
				return trapezoidalIntegrator.integrate();
			case SIMPSON:
				SimpsonIntegrator simpsonIntegrator = new SimpsonIntegrator(request);
				return simpsonIntegrator.integrate();
			case MIDPOINT:
				MidpointIntegrator midpointIntegrator = new MidpointIntegrator(request);
				return midpointIntegrator.integrate();
			case LOBATTO_QUADRATURE:
				LobattoQuadrature lq = new LobattoQuadrature(request);
				return lq.integrate();
			default:
				throw new IllegalArgumentException(
						"Unknown or Unimplemented Integration Method");
		}
	}
	
	private void enforceMemoryLimit() {
		long usedMemoryMB = (Runtime.getRuntime().totalMemory() 
				- Runtime.getRuntime().freeMemory()) / (1024 * 1024);
		if(usedMemoryMB > MEMORY_LIMIT_MB) {
			throw new RuntimeException("Memory usage exceeded limit ("
					+ MEMORY_LIMIT_MB +")");
		}
	}
}
