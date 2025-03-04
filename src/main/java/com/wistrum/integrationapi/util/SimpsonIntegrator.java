package com.wistrum.integrationapi.util;

import com.wistrum.integrationapi.model.IntegrationRequest;
import org.mariuszgromada.math.mxparser.*;

public class SimpsonIntegrator {
	IntegrationRequest request;
	
	public SimpsonIntegrator(IntegrationRequest request) {
		this.request = request;
	}
	
	public double integrate() {
		String function = request.getFunction();
		double upperBound = request.getUpperBound();
		double lowerBound = request.getLowerBound();
		int intervals = request.getIntervals();
		
		if(lowerBound > upperBound) {
			throw new IllegalArgumentException(
					"Lower Bound must be less than or equal to Upper Bound.");
		}
		if(intervals % 2 != 0) {
			throw new IllegalArgumentException("Intervals must be even.");
		}
		if(intervals < 0 || intervals > 1000000) {
			throw new IllegalArgumentException(
					"Intervals must be at least 1 and "
					+ "at most 1.0e6");
		}
		
		Function f = new Function("f(x) = " + function);
		double stepSize = (upperBound - lowerBound) / intervals;
		
		
		double fa = f.calculate(lowerBound);
		
		double fb = f.calculate(upperBound);
		double sum = fa + fb;
		
		for(int i = 1; i < intervals; i += 2) {
			sum += 4 * f.calculate(lowerBound + i * stepSize);
		}
		
		for(int i = 2; i < intervals; i += 2) {
			sum += 2 * f.calculate(lowerBound + i * stepSize);
		}
		
		return (stepSize / 3) * sum;
	}
}
