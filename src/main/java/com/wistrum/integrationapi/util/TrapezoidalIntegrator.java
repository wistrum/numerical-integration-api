package com.wistrum.integrationapi.util;

import com.wistrum.integrationapi.model.IntegrationRequest;
import org.mariuszgromada.math.mxparser.*;

public class TrapezoidalIntegrator {
	IntegrationRequest request;
	
	public TrapezoidalIntegrator(IntegrationRequest request){
		this.request = request;
	}
	public double integrate() {
		String function = request.getFunction();
		double lowerBound = request.getLowerBound();
		double upperBound = request.getUpperBound();
		int intervals = request.getIntervals();
		
		if(intervals < 0 || intervals > 1000000) {
			throw new IllegalArgumentException(
					"Intervals must be at least 1 and "
					+ "at most 1.0e6");
		}
		
		double stepSize = (upperBound - lowerBound) / intervals;
		Function f = new Function("f(x) = "+ function);
		
		double fa = f.calculate(lowerBound);
		double fb = f.calculate(upperBound);
		double sum = 0;
		double xi = lowerBound + stepSize;
		
		for(int i = 1; i < intervals; i++) {
			sum += f.calculate(xi);
			xi += stepSize;
		}
		
		return 0.5*stepSize*(fa + fb + sum);
	}
}
