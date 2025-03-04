package com.wistrum.integrationapi.util;

import com.wistrum.integrationapi.model.IntegrationRequest;
import org.mariuszgromada.math.mxparser.*;

public class MidpointIntegrator {
	IntegrationRequest request;
	
	public MidpointIntegrator(IntegrationRequest request) {
		this.request = request;
	}
	
	public double integrate() {
		String function = request.getFunction();
		double upperBound = request.getUpperBound();
		double lowerBound = request.getLowerBound();
		int intervals = request.getIntervals();
		
		if(intervals < 0 || intervals > 1000000) {
			throw new IllegalArgumentException(
					"Intervals must be at least 1 and "
					+ "at most 1.0e6");
		}
		
		Function f = new Function("f(x) = " + function);
		double stepSize = (upperBound - lowerBound) / intervals;
		double sum = 0;
		
		for(int i = 0; i < intervals; i++) {
			double midPoint = lowerBound + (i + 0.5) * stepSize;
			sum += f.calculate(midPoint);
		}
		return sum * stepSize;
	}
}
